package com.pancast.dongle.gaen

import android.util.Log
import com.pancast.dongle.utilities.Constants.HMAC_KEY
import com.pancast.dongle.utilities.Constants.MCC_CODE
import com.pancast.dongle.utilities.byteArrayOfInts
import com.pancast.dongle.utilities.decodeHex
import com.pancast.dongle.utilities.getMinutesSinceLinuxEpoch
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun computeHMACAuthenticationString(): ByteArray {
    val currentHour = getMinutesSinceLinuxEpoch() / 60
    val base = "$MCC_CODE:00000:$currentHour"
    val key = HMAC_KEY.decodeHex()
    return HMAC(base.toByteArray(), key)
}

fun HMAC(message: ByteArray, key: ByteArray): ByteArray {
    val hmacProvider = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key, "HmacSHA256")
    hmacProvider.init(secretKey)
    return hmacProvider.doFinal(message)

}

// Temporary Exposure Keys are generated every 24 hours
// We obtain TEKs from the server, as well as their associated EN interval number
// TEKs are 16 byte fields
// We derive the RPI key as follows:
// HKDF(Key, Salt, Info, OutputLength)
// HKDF(TEK, NULL, "EN-RPIK", 16)


fun getRPIsFromTEK(temporaryExposureKey: ByteArray, ENIntervalNumber: Int): List<ByteArray> {
    val intervals = (ENIntervalNumber..(ENIntervalNumber + 144)).map { it }
    return intervals.map {
        AES128(HKDF(temporaryExposureKey), it)
    }
}

// implementing https://datatracker.ietf.org/doc/html/rfc5869
// length is statically 0
fun HKDF(temporaryExposureKey: ByteArray): ByteArray {
    val salt = byteArrayOfInts(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    val info = "EN-RPIK"

    val hmacProvider = Mac.getInstance("HmacSHA256")
    val extractSecretKey = SecretKeySpec(salt, "HmacSHA256")
    hmacProvider.init(extractSecretKey)
    val prk = hmacProvider.doFinal(temporaryExposureKey)

    val expandSecretKey = SecretKeySpec(info.toByteArray() + 0x01.toByte(), "HmacSHA256")
    hmacProvider.init(expandSecretKey)
    return hmacProvider.doFinal(prk).copyOfRange(0, 16)
}

// based off of https://www.programmersought.com/article/17624267130/
fun AES128(rollingProximityIdentifierKey: ByteArray, ENIntervalNumber: Int): ByteArray {
    val paddedData = "EN-RPIK".toByteArray() + byteArrayOfInts(0, 0, 0, 0, 0) +
            ByteBuffer.allocate(4).putInt(ENIntervalNumber).array()
    val cipher = Cipher.getInstance("AES/CBC/NoPadding")
    val secretKey = SecretKeySpec(paddedData, "AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher.doFinal(rollingProximityIdentifierKey)
}
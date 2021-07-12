package com.pancast.dongle.gaen

import android.util.Log
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

class PacketParser(val data: ByteArray) {
    private val signature: ByteArray
    private val binary: ByteArray

    init {
        val unzippedData = unzip(data)
        signature = unzippedData[1]
        binary = unzippedData[0].copyOfRange(16, unzippedData[0].size)
    }
    // The byte array that comes from the /retrieve endpoint of the Covid Alert app takes on the
    // form of:
    // a zip file containing:
    //      - export.sig (signature to verify broadcast)
    //      - export.bin

    // export.bin is a byte array containing
    // - a 16 byte header
    // - a protocol buffer message

    private fun unzip(content: ByteArray): List<ByteArray> {
        val inputStream = ByteArrayInputStream(content)
        val zipInputStream = ZipInputStream(inputStream)
        var ze = zipInputStream.nextEntry
        val finalOutput: MutableList<ByteArray> = mutableListOf()
        while (ze != null) {
            try {
                val buf = zipInputStream.readBytes()
                finalOutput.add(buf)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            ze = zipInputStream.nextEntry
        }
        inputStream.close()
        zipInputStream.close()
        return finalOutput
    }

    fun useData() {
        val teke = Covidshield.TemporaryExposureKeyExport.newBuilder().mergeFrom(binary.inputStream()).build()
        Log.d("TEST", teke.toString())
    }

}
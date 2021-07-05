package com.pancast.dongle.cuckoo

import android.util.Log
import com.pancast.dongle.cuckoo.hash.MetroHash64
import com.pancast.dongle.fingerprintToBytes
import java.nio.ByteBuffer
import java.nio.ByteOrder


const val SEED: Long = 1337
const val FINGERPRINT_BITS: UInt = 27u
const val FINGERPRINT_SIZE_IN_BYTES: UInt = 4u
const val FINGERPRINT_BITMASK: UInt = 134217727u
const val ENTRIES_PER_BUCKET: UInt = 4u
const val HEADER_LENGTH_BYTES: UInt = 8u
const val BITS_IN_BYTE: UInt = 8u

class CuckooFilter(private val cf: ByteArray) {

    fun lookupItem(item: ByteArray): Boolean {
        val hash = MetroHash64(SEED).apply(ByteBuffer.wrap(item)).get().toULong()
        val fingerprint = getFingerprint(hash)
        val indices = getIndices(hash, fingerprint)
        return matchIndex(indices.first, fingerprint) or matchIndex(indices.second, fingerprint)
    }

    private fun getFingerprint(hash: ULong): ULong {
        return (hash % (FINGERPRINT_BITMASK - 1u)) + 1u
    }

    private fun getIndices(hash: ULong, fingerprint: ULong): Pair<ULong, ULong> {
        val secondHash = MetroHash64(SEED).apply(ByteBuffer.wrap(fingerprintToBytes(fingerprint))).get()
        val numBuckets = getNumBuckets()
        val i1 = hash and (numBuckets - 1u)
        val i2 = (i1 xor secondHash.toULong()) and (numBuckets - 1u)
        return Pair(i1, i2)
    }

    private fun getByteAndBitOffset(index: ULong, entryNum: Int): Pair<ULong, ULong> {
        val bitOffset = FINGERPRINT_BITS * (ENTRIES_PER_BUCKET * index + entryNum.toUInt()) + 8u * HEADER_LENGTH_BYTES
        return Pair(bitOffset / BITS_IN_BYTE, bitOffset % BITS_IN_BYTE)
    }

    private fun readNumBitsFromByteAndBitOffset(byteOffset: ULong, bitOffset: ULong): ULong {
        val remainingBits = FINGERPRINT_BITS % BITS_IN_BYTE
        var tempBitOffset = bitOffset
        var extraByte: UInt = 0u
        val tempBuffer = ByteBuffer.allocate(FINGERPRINT_SIZE_IN_BYTES.toInt())

        for (i in 0 until FINGERPRINT_SIZE_IN_BYTES.toInt()) {
            if (i == 0) {
                if (tempBitOffset + remainingBits > BITS_IN_BYTE) {
                    var currentContent = tempBuffer.get(i)
                    val byteAsInt = cf[(byteOffset + i.toUInt()).toInt()].toInt() and 0xFF
                    val lowerOrderBits = (byteAsInt shl tempBitOffset.toInt()) and 0xFF
                    val lowerOrderBitsShiftedRight = lowerOrderBits ushr (BITS_IN_BYTE - remainingBits).toInt()
                    currentContent = (currentContent.toInt() or lowerOrderBitsShiftedRight).toByte()

                    val upperByteAsInt = cf[(byteOffset + i.toUInt() + 1u).toInt()].toInt() and 0xFF
                    val remainingBitsToAdd = upperByteAsInt ushr (BITS_IN_BYTE - (remainingBits + tempBitOffset - BITS_IN_BYTE)).toInt()
                    currentContent = (currentContent.toInt() or remainingBitsToAdd).toByte()

//                    currentContent = (currentContent.toInt() or ((cf[(byteOffset + i.toUInt()).toInt()].toInt() shl tempBitOffset.toInt()) ushr ((BITS_IN_BYTE - remainingBits).toInt()))).toByte()
//                    currentContent = (currentContent.toInt() or ((cf[(byteOffset + i.toUInt() + 1u).toInt()].toInt() ushr ((BITS_IN_BYTE - (remainingBits + tempBitOffset - BITS_IN_BYTE)).toInt())))).toByte()
                    tempBuffer.put(i, currentContent)
                    tempBitOffset += remainingBits - BITS_IN_BYTE
                    extraByte += 1u
                } else {
                    var currentContent = tempBuffer.get(i)
                    val byteAsInt = cf[(byteOffset + i.toUInt()).toInt()].toInt() and 0xFF
                    val remainingBitsToAdd = (byteAsInt shl tempBitOffset.toInt()) and 0xFF
                    val remainingBitsToAddShiftedRight = remainingBitsToAdd ushr ((BITS_IN_BYTE - remainingBits).toInt())
                    currentContent = (currentContent.toInt() or remainingBitsToAddShiftedRight).toByte()

//                    currentContent = (currentContent.toInt() or ((cf[(byteOffset + i.toUInt()).toInt()].toInt() shl tempBitOffset.toInt()) ushr ((BITS_IN_BYTE - remainingBits).toInt()))).toByte()
                    tempBuffer.put(i, currentContent)
                    tempBitOffset += remainingBits
                    tempBitOffset %= BITS_IN_BYTE
                }
            } else {
                if (tempBitOffset.compareTo(0u) == 0) {
                    var currentContent = tempBuffer.get(i)
                    val byteAsInt =  cf[(byteOffset + i.toUInt() + extraByte).toInt()].toInt() and 0xFF
                    currentContent = (currentContent.toInt() or byteAsInt).toByte()
//                  currentContent = (currentContent.toInt() or cf[(byteOffset + i.toUInt() + extraByte).toInt()].toInt()).toByte()
                    tempBuffer.put(i, currentContent)
                } else {
                    var currentContent = tempBuffer.get(i)
                    val previousByteAsInt = cf[(byteOffset + i.toUInt() + extraByte - 1u).toInt()].toInt() and 0xFF
                    val previousByteLowerBits = (previousByteAsInt shl tempBitOffset.toInt()) and 0xFF
                    currentContent = (currentContent.toInt() or previousByteLowerBits).toByte()

                    val nextByteAsInt = cf[(byteOffset + i.toUInt() + extraByte).toInt()].toInt() and 0xFF
                    val nextByteShiftedRight = nextByteAsInt ushr ((BITS_IN_BYTE - tempBitOffset).toInt())
                    currentContent = (currentContent.toInt() or nextByteShiftedRight).toByte()

//                    currentContent = (currentContent.toInt() or (cf[(byteOffset + i.toUInt() + extraByte - 1u).toInt()].toInt() shl tempBitOffset.toInt())).toByte()
//                    currentContent = (currentContent.toInt() or (cf[(byteOffset + i.toUInt() + extraByte).toInt()].toInt() ushr ((BITS_IN_BYTE - tempBitOffset).toInt()))).toByte()
                    tempBuffer.put(i, currentContent)
                }
            }
        }
        return tempBuffer.order(ByteOrder.BIG_ENDIAN).int.toULong()
    }

    private fun matchIndex(index: ULong, fp: ULong): Boolean {
        for (i in 0 until ENTRIES_PER_BUCKET.toInt()) {
            val byteAndBitOffset = getByteAndBitOffset(index, i)
            val fpToCheck = readNumBitsFromByteAndBitOffset(byteAndBitOffset.first, byteAndBitOffset.second)
            if (fpToCheck == fp) {
                return true
            }
        }
        return false
    }

    private fun getNumBuckets(): ULong {
        val numBytes = ByteBuffer.wrap(cf.copyOfRange(0, 8)).order(ByteOrder.LITTLE_ENDIAN).long.toULong()
        return (numBytes * 8u) / (FINGERPRINT_BITS * ENTRIES_PER_BUCKET)
    }
}
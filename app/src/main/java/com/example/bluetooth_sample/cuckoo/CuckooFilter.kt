package com.example.bluetooth_sample.cuckoo

import com.example.bluetooth_sample.cuckoo.hash.MetroHash64
import com.example.bluetooth_sample.longToBytes
import com.example.bluetooth_sample.utilities.getMinutesSinceLinuxEpoch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ceil


const val SEED: Long = 1337
const val FINGERPRINT_BITS = 27
const val FINGERPRINT_SIZE_IN_BYTES = 4
const val FINGERPRINT_BITMASK = 134217727
const val ENTRIES_PER_BUCKET = 4
const val HEADER_LENGTH_BYTES = 8

class CuckooFilter(private val cf: ByteArray) {
    val length = ceil(((cf.size * 8) / (ENTRIES_PER_BUCKET * FINGERPRINT_BITS)).toDouble()).toLong()
    // function for decoding risk broadcast
    fun lookupItem(item: ByteArray): Boolean {
        val hash = MetroHash64(SEED).apply(ByteBuffer.wrap(item)).get()
        val fingerprint = getFingerprint(hash)
        val indices = getIndices(hash)

        // compute fingerprint and indices
        return true
    }
    // function for lookup on filter

    private fun getFingerprint(hash: Long): Long {
        return (hash % (FINGERPRINT_BITMASK - 1)) + 1
    }

    private fun getIndices(hash: Long): Pair<Long, Long> {
        val secondHash = MetroHash64(SEED).apply(ByteBuffer.wrap(longToBytes(hash))).get()
        val i1 = hash and (length - 1)
        val i2 = (i1 xor secondHash) and (length - 1)
        return Pair(i1, i2)
    }

    private fun getByteAndBitOffset(index: Long, entryNum: Int): Pair<Long, Long> {
        val bitOffset = FINGERPRINT_BITS * (ENTRIES_PER_BUCKET * index + entryNum) + 8 * HEADER_LENGTH_BYTES
        return Pair(bitOffset / 8, bitOffset % 8)
    }

    private fun readNumBitsFromByteAndBitOffset(byteOffset: Long, bitOffset: Long): Long {
        val remainingBits = FINGERPRINT_BITS % 8
        var tempBitOffset = bitOffset
        var extraByte = 0
        val tempBuffer = ByteBuffer.allocate(FINGERPRINT_SIZE_IN_BYTES)

        for (i in 0 .. FINGERPRINT_SIZE_IN_BYTES) {
            if (i == 0) {
                if (tempBitOffset + remainingBits > 8) {
                    var currentContent = tempBuffer.get(i)
                    currentContent = (currentContent.toInt() or ((cf[(byteOffset + i).toInt()].toInt() shl tempBitOffset.toInt()) shr (8 - remainingBits))).toByte()
                    currentContent = (currentContent.toInt() or ((cf[(byteOffset + i + 1).toInt()].toInt() shr ((8 - (remainingBits + tempBitOffset - 8)).toInt())))).toByte()
                    tempBuffer.put(i, currentContent)
                    tempBitOffset += remainingBits - 8
                    extraByte += 1
                } else {
                    var currentContent = tempBuffer.get(i)
                    currentContent = (currentContent.toInt() or ((cf[(byteOffset + i).toInt()].toInt() shl tempBitOffset.toInt()) shr (8 - remainingBits))).toByte()
                    tempBuffer.put(i, currentContent)
                    tempBitOffset += remainingBits
                    tempBitOffset %= 8
                }
            } else {
                if (tempBitOffset.compareTo(0) == 0) {
                    var currentContent = tempBuffer.get(i)
                    currentContent = (currentContent.toInt() or cf[(byteOffset + i + extraByte).toInt()].toInt()).toByte()
                    tempBuffer.put(i, currentContent)
                } else {
                    var currentContent = tempBuffer.get(i)
                    currentContent = (currentContent.toInt() or (cf[(byteOffset + i + extraByte - 1).toInt()].toInt() shl tempBitOffset.toInt())).toByte()
                    currentContent = (currentContent.toInt() or (cf[(byteOffset + i + extraByte).toInt()].toInt() shr ((8 - tempBitOffset).toInt()))).toByte()
                    tempBuffer.put(i, currentContent)
                }
            }
        }
        return tempBuffer.order(ByteOrder.BIG_ENDIAN).long
    }

    private fun matchIndex(index: Long, fp: Long): Boolean {
        for (i in 0 .. ENTRIES_PER_BUCKET) {
            val byteAndBitOffset = getByteAndBitOffset(index, i)
            val fpToCheck = readNumBitsFromByteAndBitOffset(byteAndBitOffset.first, byteAndBitOffset.second)
            if (fpToCheck == fp) {
                return true
            }
        }
        return false
    }
}
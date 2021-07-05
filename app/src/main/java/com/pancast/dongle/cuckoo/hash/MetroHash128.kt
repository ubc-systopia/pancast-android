package com.pancast.dongle.cuckoo.hash

import java.nio.ByteBuffer

/*
* Copyright (c) 2016 Marius Posta
*
* Licensed under the Apache 2 license:
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
class MetroHash128(private val seed: Long) : MetroHashImpl(),
    MetroHash<MetroHash128> {
    /**
     * First 64 bits of the current hash value.
     */
    var high: Long = 0
        private set

    /**
     * Last 64 bits of the current hash value.
     */
    var low: Long = 0
        private set
    private var v2: Long = 0
    private var v3: Long = 0
    private var nChunks: Long = 0
    override fun writeLittleEndian(output: ByteBuffer): MetroHash128 {
        writeLittleEndian(high, output)
        writeLittleEndian(low, output)
        return this
    }

    override fun writeBigEndian(output: ByteBuffer): MetroHash128 {
        output.asLongBuffer().put(low).put(high)
        return this
    }

    override fun reset(): MetroHash128 {
        high = (seed - K0) * K3
        low = (seed + K1) * K2
        v2 = (seed + K0) * K2
        v3 = (seed - K1) * K3
        nChunks = 0
        return this
    }

    override fun partialApply32ByteChunk(partialInput: ByteBuffer): MetroHash128 {
        assert(partialInput.remaining() >= 32)
        high += grab8(partialInput) * K0
        high = rotr64(high, 29) + v2
        low += grab8(partialInput) * K1
        low = rotr64(low, 29) + v3
        v2 += grab8(partialInput) * K2
        v2 = rotr64(v2, 29) + high
        v3 += grab8(partialInput) * K3
        v3 = rotr64(v3, 29) + low
        ++nChunks
        return this
    }

    override fun partialApplyRemaining(partialInput: ByteBuffer): MetroHash128 {
        assert(partialInput.remaining() < 32)
        if (nChunks > 0) {
            metroHash128_32()
        }
        if (partialInput.remaining() >= 16) {
            metroHash128_16(partialInput)
        }
        if (partialInput.remaining() >= 8) {
            metroHash128_8(partialInput)
        }
        if (partialInput.remaining() >= 4) {
            metroHash128_4(partialInput)
        }
        if (partialInput.remaining() >= 2) {
            metroHash128_2(partialInput)
        }
        if (partialInput.remaining() >= 1) {
            metroHash128_1(partialInput)
        }
        high += rotr64(high * K0 + low, 13)
        low += rotr64(low * K1 + high, 37)
        high += rotr64(high * K2 + low, 13)
        low += rotr64(low * K3 + high, 37)
        return this
    }

    private fun metroHash128_32() {
        v2 = v2 xor rotr64((high + v3) * K0 + low, 21) * K1
        v3 = v3 xor rotr64((low + v2) * K1 + high, 21) * K0
        high = high xor rotr64((high + v2) * K0 + v3, 21) * K1
        low = low xor rotr64((low + v3) * K1 + v2, 21) * K0
    }

    private fun metroHash128_16(bb: ByteBuffer) {
        high += grab8(bb) * K2
        high = rotr64(high, 33) * K3
        low += grab8(bb) * K2
        low = rotr64(low, 33) * K3
        high = high xor rotr64(
            high * K2 + low, 45
        ) * K1
        low = low xor rotr64(low * K3 + high, 45) * K0
    }

    private fun metroHash128_8(bb: ByteBuffer) {
        high += grab8(bb) * K2
        high = rotr64(high, 33) * K3
        high = high xor rotr64(
            high * K2 + low, 27
        ) * K1
    }

    private fun metroHash128_4(bb: ByteBuffer) {
        low += grab4(bb) * K2
        low = rotr64(low, 33) * K3
        low = low xor rotr64(low * K3 + high, 46) * K0
    }

    private fun metroHash128_2(bb: ByteBuffer) {
        high += grab2(bb) * K2
        high = rotr64(high, 33) * K3
        high = high xor rotr64(
            high * K2 + low, 22
        ) * K1
    }

    private fun metroHash128_1(bb: ByteBuffer) {
        low += grab1(bb) * K2
        low = rotr64(low, 33) * K3
        low = low xor rotr64(low * K3 + high, 58) * K0
    }

    companion object {
        private const val K0 = 0xC83A91E1L
        private const val K1 = 0x8648DBDBL
        private const val K2 = 0x7BDEC03BL
        private const val K3 = 0x2F5870A5L
    }

    /**
     * Initializes a MetroHash128 state with the given seed.
     */
    init {
        reset()
    }
}
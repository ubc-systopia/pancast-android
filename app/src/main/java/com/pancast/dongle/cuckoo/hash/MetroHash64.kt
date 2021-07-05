package com.pancast.dongle.cuckoo.hash

import java.nio.ByteBuffer

/*
* Copyright (c) 2016 Marius Posta
*
* Licensed under the Apache 2 license:
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
class MetroHash64(private val seed: Long) : MetroHashImpl(),
    MetroHash<MetroHash64> {
    private var v0: Long = 0
    private var v1: Long = 0
    private var v2: Long = 0
    private var v3: Long = 0
    private var nChunks: Long = 0
    private var hash: Long = 0

    /**
     * Current hash value.
     */
    fun get(): Long {
        return hash
    }

    override fun writeLittleEndian(output: ByteBuffer): MetroHash64 {
        writeLittleEndian(hash, output)
        return this
    }

    override fun writeBigEndian(output: ByteBuffer): MetroHash64 {
        output.asLongBuffer().put(hash)
        return this
    }

    override fun reset(): MetroHash64 {
        hash = (seed + K2) * K0
        v3 = hash
        v2 = v3
        v1 = v2
        v0 = v1
        nChunks = 0
        return this
    }

    override fun partialApply32ByteChunk(partialInput: ByteBuffer): MetroHash64 {
        assert(partialInput.remaining() >= 32)
        v0 += grab8(partialInput) * K0
        v0 = rotr64(v0, 29) + v2
        v1 += grab8(partialInput) * K1
        v1 = rotr64(v1, 29) + v3
        v2 += grab8(partialInput) * K2
        v2 = rotr64(v2, 29) + v0
        v3 += grab8(partialInput) * K3
        v3 = rotr64(v3, 29) + v1
        ++nChunks
        return this
    }

    override fun partialApplyRemaining(partialInput: ByteBuffer): MetroHash64 {
        assert(partialInput.remaining() < 32)
        if (nChunks > 0) {
            metroHash64_32()
        }
        if (partialInput.remaining() >= 16) {
            metroHash64_16(partialInput)
        }
        if (partialInput.remaining() >= 8) {
            metroHash64_8(partialInput)
        }
        if (partialInput.remaining() >= 4) {
            metroHash64_4(partialInput)
        }
        if (partialInput.remaining() >= 2) {
            metroHash64_2(partialInput)
        }
        if (partialInput.remaining() >= 1) {
            metroHash64_1(partialInput)
        }
        hash = hash xor rotr64(hash, 28)
        hash *= K0
        hash = hash xor rotr64(hash, 29)
        return this
    }

    private fun metroHash64_32() {
        v2 = v2 xor rotr64((v0 + v3) * K0 + v1, 37) * K1
        v3 = v3 xor rotr64((v1 + v2) * K1 + v0, 37) * K0
        v0 = v0 xor rotr64((v0 + v2) * K0 + v3, 37) * K1
        v1 = v1 xor rotr64((v1 + v3) * K1 + v2, 37) * K0
        hash += v0 xor v1
    }

    private fun metroHash64_16(bb: ByteBuffer) {
        v0 = hash + grab8(bb) * K2
        v0 = rotr64(v0, 29) * K3
        v1 = hash + grab8(bb) * K2
        v1 = rotr64(v1, 29) * K3
        v0 = v0 xor rotr64(v0 * K0, 21) + v1
        v1 = v1 xor rotr64(v1 * K3, 21) + v0
        hash += v1
    }

    private fun metroHash64_8(bb: ByteBuffer) {
        hash += grab8(bb) * K3
        hash = hash xor rotr64(hash, 55) * K1
    }

    private fun metroHash64_4(bb: ByteBuffer) {
        hash += grab4(bb) * K3
        hash = hash xor rotr64(hash, 26) * K1
    }

    private fun metroHash64_2(bb: ByteBuffer) {
        val tempVal = grab2(bb)
        hash += tempVal * K3
        hash = hash xor rotr64(hash, 48) * K1
    }

    private fun metroHash64_1(bb: ByteBuffer) {
        hash += grab1(bb) * K3
        hash = hash xor rotr64(hash, 37) * K1
    }

    companion object {
        private const val K0: Long = 0x00000000D6D018F5
        private const val K1: Long = 0x00000000A2AA033B
        private const val K2: Long = 0x0000000062992FC1
        private const val K3: Long = 0x0000000030BC5B29
    }

    /**
     * Initializes a MetroHash64 state with the given seed.
     */
    init {
        reset()
    }
}
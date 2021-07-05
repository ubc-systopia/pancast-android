package com.pancast.dongle.cuckoo.hash

import java.nio.ByteBuffer

/*
* Copyright (c) 2016 Marius Posta
*
* Licensed under the Apache 2 license:
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/   interface MetroHash<T : MetroHash<T>> {
    /**
     * Applies the instance's Metro hash function to the bytes in the given buffer.
     * This updates this instance's hashing state.
     * @return this
     */
    fun apply(input: ByteBuffer): T {
        reset()
        while (input.remaining() >= 32) {
            partialApply32ByteChunk(input)
        }
        return partialApplyRemaining(input)
    }

    /**
     * Writes the current hash to the given byte buffer in little-endian order.
     * @return this
     */
    fun writeLittleEndian(output: ByteBuffer): T

    /**
     * Writes the current hash to the given byte buffer in big-endian order.
     * @return this
     */
    fun writeBigEndian(output: ByteBuffer): T

    /**
     * Re-initializes the hashing state.
     * @return this
     */
    fun reset(): T

    /**
     * Consumes a 32-byte chunk from the byte buffer and updates the hashing state.
     *
     * @param partialInput byte buffer with at least 32 bytes remaining.
     * @return this
     */
    fun partialApply32ByteChunk(partialInput: ByteBuffer): T

    /**
     * Consumes the remaining bytes from the byte buffer and updates the hashing state.
     * @param partialInput byte buffer with less than 32 bytes remaining.
     * @return this
     */
    fun partialApplyRemaining(partialInput: ByteBuffer): T

    companion object {
        /**
         * Hashes the input using MetroHash64 with the default seed (0) and returns the resulting state.
         */
        fun hash64(input: ByteArray): MetroHash64 {
            return hash64(ByteBuffer.wrap(input))
        }

        /**
         * Hashes the input using MetroHash64 with the default seed (0) and returns the resulting state.
         */
        fun hash64(input: ByteBuffer): MetroHash64 {
            return hash64(0, input)
        }

        /**
         * Hashes the input using MetroHash64 with the given seed and returns the resulting state.
         */
        fun hash64(seed: Long, input: ByteArray): MetroHash64 {
            return hash64(seed, ByteBuffer.wrap(input))
        }

        /**
         * Hashes the input using MetroHash64 with the given seed and returns the resulting state.
         */
        fun hash64(seed: Long, input: ByteBuffer): MetroHash64 {
            return MetroHash64(seed).apply(input)
        }

        /**
         * Hashes the input using MetroHash128 with the default seed (0) and returns the resulting state.
         */
        fun hash128(input: ByteArray): MetroHash128 {
            return hash128(ByteBuffer.wrap(input))
        }

        /**
         * Hashes the input using MetroHash128 with the default seed (0) and returns the resulting state.
         */
        fun hash128(input: ByteBuffer): MetroHash128 {
            return hash128(0, input)
        }

        /**
         * Hashes the input using MetroHash128 with the given seed and returns the resulting state.
         */
        fun hash128(seed: Long, input: ByteArray): MetroHash128 {
            return hash128(seed, ByteBuffer.wrap(input))
        }

        /**
         * Hashes the input using MetroHash128 with the given seed and returns the resulting state.
         */
        fun hash128(seed: Long, input: ByteBuffer): MetroHash128 {
            return MetroHash128(seed).apply(input)
        }
    }
}
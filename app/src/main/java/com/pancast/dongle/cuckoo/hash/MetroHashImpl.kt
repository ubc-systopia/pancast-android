package com.pancast.dongle.cuckoo.hash

import java.nio.ByteBuffer

/*
* Copyright (c) 2016 Marius Posta
*
* Licensed under the Apache 2 license:
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/ open class MetroHashImpl {
    companion object {
        fun writeLittleEndian(hash: Long, output: ByteBuffer) {
            output.put(hash.toByte())
            output.put((hash ushr 8).toByte())
            output.put((hash ushr 16).toByte())
            output.put((hash ushr 24).toByte())
            output.put((hash ushr 32).toByte())
            output.put((hash ushr 40).toByte())
            output.put((hash ushr 48).toByte())
            output.put((hash ushr 56).toByte())
        }

        fun rotr64(x: Long, r: Int): Long {
            return x ushr r or (x shl 64 - r)
        }

        fun grab1(bb: ByteBuffer): Long {
            return bb.get().toLong() and 0xFFL
        }

        fun grab2(bb: ByteBuffer): Long {
            val v0 = bb.get().toLong()
            val v1 = bb.get().toLong()
            return v0 and 0xFFL or ((v1 and 0xFFL) shl 8)
        }

        fun grab4(bb: ByteBuffer): Long {
            val v0 = bb.get().toLong()
            val v1 = bb.get().toLong()
            val v2 = bb.get().toLong()
            val v3 = bb.get().toLong()
            return v0 and 0xFFL or (v1 and 0xFFL shl 8) or (v2 and 0xFFL shl 16) or (v3 and 0xFFL shl 24)
        }

        fun grab8(bb: ByteBuffer): Long {
            val v0 = bb.get().toLong()
            val v1 = bb.get().toLong()
            val v2 = bb.get().toLong()
            val v3 = bb.get().toLong()
            val v4 = bb.get().toLong()
            val v5 = bb.get().toLong()
            val v6 = bb.get().toLong()
            val v7 = bb.get().toLong()
            return v0 and 0xFFL or (v1 and 0xFFL shl 8) or (v2 and 0xFFL shl 16) or (v3 and 0xFFL shl 24
                    ) or (v4 and 0xFFL shl 32) or (v5 and 0xFFL shl 40) or (v6 and 0xFFL shl 48) or (v7 and 0xFFL shl 56)
        }
    }
}
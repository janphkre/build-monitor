package de.janphkre.buildmonitor.util

import java.io.Writer
import java.lang.StringBuilder

class EscapingJsonWriter: Writer() {
    private val buffer = StringBuilder()

    override fun write(cbuf: CharArray, off: Int, len: Int) {
        var lastIndex = 0
        var normalCount = 0
        for(i in 0 until len) {
            when(cbuf[off+i]) {
                '\t' -> {
                    buffer.append(cbuf, off+lastIndex, normalCount)
                    buffer.append('\\')
                    buffer.append('t')
                    normalCount = 0
                    lastIndex = i+1
                }
                '\"' -> {
                    buffer.append(cbuf, off+lastIndex, normalCount)
                    buffer.append('\\')
                    buffer.append('"')
                    normalCount = 0
                    lastIndex = i+1
                }
                '\n' -> {
                    buffer.append(cbuf, off+lastIndex, normalCount)
                    buffer.append('\\')
                    buffer.append('n')
                    normalCount = 0
                    lastIndex = i+1
                }
                else -> {
                    normalCount++
                }
            }
        }
        if(lastIndex < len) {
            buffer.append(cbuf, off+lastIndex, normalCount)
        }
    }

    override fun flush() { }

    override fun close() { }

    override fun toString(): String {
        return buffer.toString()
    }
}
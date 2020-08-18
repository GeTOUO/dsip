package com.dxp.sip.util

import java.nio.charset.Charset
import io.netty.util.CharsetUtil
import java.nio.charset.CodingErrorAction
import java.nio.charset.CharsetEncoder
import io.netty.util.internal.ObjectUtil
import io.netty.util.internal.InternalThreadLocalMap
import java.nio.charset.CharsetDecoder

/**
 * @author carzy
 * @date 2020/8/12
 */
object CharsetUtils {
    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is identified by
     * an optional byte-order mark
     */
    @JvmField
    val GB_2313: Charset = Charset.forName("gb2312")

    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is identified by
     * an optional byte-order mark
     */
    @JvmField
    val UTF_16: Charset = CharsetUtil.UTF_16

    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is big-endian
     */
    @JvmField
    val UTF_16BE: Charset = CharsetUtil.UTF_16BE

    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is little-endian
     */
    @JvmField
    val UTF_16LE: Charset = CharsetUtil.UTF_16LE

    /**
     * 8-bit UTF (UCS Transformation Format)
     */
    @JvmField
    val UTF_8: Charset = CharsetUtil.UTF_8

    /**
     * ISO Latin Alphabet No. 1, as known as <tt>ISO-LATIN-1</tt>
     */
    @JvmField
    val ISO_8859_1: Charset = CharsetUtil.ISO_8859_1

    /**
     * 7-bit ASCII, as known as ISO646-US or the Basic Latin block of the
     * Unicode character set
     */
    @JvmField
    val US_ASCII = CharsetUtil.US_ASCII

    private val CHARSETS = arrayOf(UTF_16, UTF_16BE, UTF_16LE, UTF_8, ISO_8859_1, US_ASCII)
    fun values(): Array<Charset> {
        return CHARSETS
    }

    /**
     * Returns a new [CharsetEncoder] for the [Charset] with specified error actions.
     *
     * @param charset                   The specified charset
     * @param malformedInputAction      The encoder's action for malformed-input errors
     * @param unmappableCharacterAction The encoder's action for unmappable-character errors
     * @return The encoder for the specified `charset`
     */
    private fun encoder(charset: Charset, malformedInputAction: CodingErrorAction?,
                        unmappableCharacterAction: CodingErrorAction?): CharsetEncoder {
        ObjectUtil.checkNotNull(charset, "charset")
        val e = charset.newEncoder()
        e.onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction)
        return e
    }

    /**
     * Returns a new [CharsetEncoder] for the [Charset] with the specified error action.
     *
     * @param charset           The specified charset
     * @param codingErrorAction The encoder's action for malformed-input and unmappable-character errors
     * @return The encoder for the specified `charset`
     */
    private fun encoder(charset: Charset, codingErrorAction: CodingErrorAction?): CharsetEncoder {
        return encoder(charset, codingErrorAction, codingErrorAction)
    }

    /**
     * Returns a cached thread-local [CharsetEncoder] for the specified [Charset].
     *
     * @param charset The specified charset
     * @return The encoder for the specified `charset`
     */
    private fun encoder(charset: Charset): CharsetEncoder? {
        ObjectUtil.checkNotNull(charset, "charset")
        val map = InternalThreadLocalMap.get().charsetEncoderCache()
        var e = map[charset]
        if (e != null) {
            e.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE)
            return e
        }
        e = encoder(charset, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE)
        map[charset] = e
        return e
    }

    /**
     * Returns a new [CharsetDecoder] for the [Charset] with specified error actions.
     *
     * @param charset                   The specified charset
     * @param malformedInputAction      The decoder's action for malformed-input errors
     * @param unmappableCharacterAction The decoder's action for unmappable-character errors
     * @return The decoder for the specified `charset`
     */
    private fun decoder(charset: Charset, malformedInputAction: CodingErrorAction?,
                        unmappableCharacterAction: CodingErrorAction?): CharsetDecoder {
        ObjectUtil.checkNotNull(charset, "charset")
        val d = charset.newDecoder()
        d.onMalformedInput(malformedInputAction).onUnmappableCharacter(unmappableCharacterAction)
        return d
    }

    /**
     * Returns a new [CharsetDecoder] for the [Charset] with the specified error action.
     *
     * @param charset           The specified charset
     * @param codingErrorAction The decoder's action for malformed-input and unmappable-character errors
     * @return The decoder for the specified `charset`
     */
    private fun decoder(charset: Charset, codingErrorAction: CodingErrorAction?): CharsetDecoder {
        return decoder(charset, codingErrorAction, codingErrorAction)
    }

    /**
     * Returns a cached thread-local [CharsetDecoder] for the specified [Charset].
     *
     * @param charset The specified charset
     * @return The decoder for the specified `charset`
     */
    private fun decoder(charset: Charset): CharsetDecoder? {
        ObjectUtil.checkNotNull(charset, "charset")
        val map = InternalThreadLocalMap.get().charsetDecoderCache()
        var d = map[charset]
        if (d != null) {
            d.reset().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE)
            return d
        }
        d = decoder(charset, CodingErrorAction.REPLACE, CodingErrorAction.REPLACE)
        map[charset] = d
        return d
    }
}
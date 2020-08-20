package com.dxp.sip.bus.handler

import com.dxp.sip.util.CharsetUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufHolder
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.logging.ByteBufFormat
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.internal.StringUtil

/**
 * 编码问题
 *
 * @author carzy
 * @date 2020/8/14
 */
class GbLoggingHandler(level: LogLevel?) : LoggingHandler(level, ByteBufFormat.HEX_DUMP) {

    override fun format(ctx: ChannelHandlerContext, eventName: String, arg: Any): String {
        return when (arg) {
            is ByteBuf -> {
                formatByteBuf(ctx, eventName, arg)
            }
            is ByteBufHolder -> {
                formatByteBufHolder(ctx, eventName, arg)
            }
            else -> {
                formatSimple(ctx, eventName, arg)
            }
        }
    }

    override fun format(ctx: ChannelHandlerContext, eventName: String): String {
        val chStr = ctx.channel().id().asShortText()
        return "$chStr $eventName"
    }

    private fun formatByteBuf(ctx: ChannelHandlerContext, eventName: String, msg: ByteBuf): String {
        val chStr = ctx.channel().id().asShortText()
        val length = msg.readableBytes()
        return if (length == 0) {
            "$chStr $eventName: 0B"
        } else {
            var outputLength = chStr.length + 1 + eventName.length + 2 + 10 + 1
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                val rows = length / 16 + (if (length % 15 == 0) 0 else 1) + 4
                val hexDumpLength = 2 + rows * 80
                outputLength += hexDumpLength
            }
            val buf = StringBuilder(outputLength)
            buf.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B')
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                buf.append(StringUtil.NEWLINE)
                appendPrettyHexDump(buf, msg)
            }
            buf.toString()
        }
    }

    /**
     * Generates the default log message of the specified event whose argument is a [ByteBufHolder].
     */
    private fun formatByteBufHolder(ctx: ChannelHandlerContext, eventName: String, msg: ByteBufHolder): String {
        val chStr = ctx.channel().id().asShortText()
        val msgStr = msg.toString()
        val content = msg.content()
        val length = content.readableBytes()
        return if (length == 0) {
            "$chStr $eventName, $msgStr, 0B"
        } else {
            var outputLength = chStr.length + 1 + eventName.length + 2 + msgStr.length + 2 + 10 + 1
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                val rows = length / 16 + (if (length % 15 == 0) 0 else 1) + 4
                val hexDumpLength = 2 + rows * 80
                outputLength += hexDumpLength
            }
            val buf = StringBuilder(outputLength)
            buf.append(chStr).append(' ').append(eventName).append(": ")
                    .append(msgStr).append(", ").append(length).append('B')
            if (byteBufFormat() == ByteBufFormat.HEX_DUMP) {
                buf.append(StringUtil.NEWLINE)
                appendPrettyHexDump(buf, content)
            }
            buf.toString()
        }
    }

    companion object {
        /**
         * Generates the default log message of the specified event whose argument is an arbitrary object.
         */
        private fun formatSimple(ctx: ChannelHandlerContext, eventName: String, msg: Any): String {
            val chStr = ctx.channel().id().asShortText()
            val msgStr = msg.toString()
            return "$chStr $eventName: $msgStr"
        }

        fun appendPrettyHexDump(dump: StringBuilder, buf: ByteBuf) {
            dump.append(buf.toString(CharsetUtils.GB_2313))
        }
    }
}
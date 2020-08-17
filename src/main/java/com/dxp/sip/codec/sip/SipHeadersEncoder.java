package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import static com.dxp.sip.codec.sip.SipConstants.COLON;
import static com.dxp.sip.codec.sip.SipConstants.SP;
import static com.dxp.sip.codec.sip.AbstractSipObjectEncoder.CRLF_SHORT;

/**
 * @author carzy
 */
public final class SipHeadersEncoder {
    private static final int COLON_AND_SPACE_SHORT = (COLON << 8) | SP;

    private SipHeadersEncoder() {
    }

    public static void encoderHeader(CharSequence name, CharSequence value, ByteBuf buf) {
        final int nameLen = name.length();
        final int valueLen = value.length();
        final int entryLen = nameLen + valueLen + 4;
        buf.ensureWritable(entryLen);
        int offset = buf.writerIndex();
        writeAscii(buf, offset, name);
        offset += nameLen;
        ByteBufUtil.setShortBE(buf, offset, COLON_AND_SPACE_SHORT);
        offset += 2;
        writeAscii(buf, offset, value);
        offset += valueLen;
        ByteBufUtil.setShortBE(buf, offset, CRLF_SHORT);
        offset += 2;
        buf.writerIndex(offset);
    }

    private static void writeAscii(ByteBuf buf, int offset, CharSequence value) {
        if (value instanceof AsciiString) {
            ByteBufUtil.copy((AsciiString) value, 0, buf, offset, value.length());
        } else {
            buf.setCharSequence(offset, value, CharsetUtil.US_ASCII);
        }
    }
}

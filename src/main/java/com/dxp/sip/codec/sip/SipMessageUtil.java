package com.dxp.sip.codec.sip;

import io.netty.util.internal.StringUtil;

import java.util.Map;

/**
 * @author carzy
 * @date 2020/8/11
 */
public final class SipMessageUtil {

    public static StringBuilder appendFullRequest(StringBuilder buf, FullSipRequest req) {
        appendFullCommon(buf, req);
        appendInitialLine(buf, req);
        appendHeaders(buf, req.headers());
        removeLastNewLine(buf);
        return buf;
    }

    public static StringBuilder appendFullResponse(StringBuilder buf, FullSipResponse res) {
        appendFullCommon(buf, res);
        appendInitialLine(buf, res);
        appendHeaders(buf, res.headers());
        removeLastNewLine(buf);
        return buf;
    }

    public static StringBuilder appendRequest(StringBuilder buf, SipRequest req) {
        appendCommon(buf, req);
        appendInitialLine(buf, req);
        appendHeaders(buf, req.headers());
        removeLastNewLine(buf);
        return buf;
    }

    public static StringBuilder appendResponse(StringBuilder buf, SipResponse res) {
        appendCommon(buf, res);
        appendInitialLine(buf, res);
        appendHeaders(buf, res.headers());
        removeLastNewLine(buf);
        return buf;
    }

    private static void appendFullCommon(StringBuilder buf, FullSipMessage msg) {
        buf.append(StringUtil.simpleClassName(msg));
        buf.append("(decodeResult: ");
        buf.append(msg.decoderResult());
        buf.append(", version: ");
        buf.append(msg.protocolVersion());
        buf.append(", content: ");
        buf.append(msg.content());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendCommon(StringBuilder buf, SipMessage msg) {
        buf.append(StringUtil.simpleClassName(msg));
        buf.append("(decodeResult: ");
        buf.append(msg.decoderResult());
        buf.append(", version: ");
        buf.append(msg.protocolVersion());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf, SipRequest req) {
        buf.append(req.method());
        buf.append(' ');
        buf.append(req.uri());
        buf.append(' ');
        buf.append(req.protocolVersion());
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf, SipResponse res) {
        buf.append(res.protocolVersion());
        buf.append(' ');
        buf.append(res.status());
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf, AbstractSipHeaders headers) {
        for (Map.Entry<String, String> e : headers) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }

    private static void removeLastNewLine(StringBuilder buf) {
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
    }

    private SipMessageUtil() {
    }

}

package com.dxp.sip.codec.sip;

import io.netty.handler.codec.http.HttpHeaderNames;

import java.net.InetSocketAddress;

/**
 * sip解析帮助类
 *
 * @author carzy
 * @date 2020/8/10
 */
public class SipUtil {

    public static long getContentLength(SipMessage message) {
        String value = message.headers().get(SipHeaderNames.CONTENT_LENGTH);
        if (value != null) {
            return Long.parseLong(value);
        }
        throw new NumberFormatException("header not found: " + HttpHeaderNames.CONTENT_LENGTH);
    }

    public static long getContentLength(SipMessage message, long defaultValue) {
        String value = message.headers().get(HttpHeaderNames.CONTENT_LENGTH);
        if (value != null) {
            return Long.parseLong(value);
        }
        return defaultValue;
    }

    public static boolean isContentLengthSet(SipMessage m) {
        return m.headers().contains(SipHeaderNames.CONTENT_LENGTH);
    }

    public static boolean is100ContinueExpected(SipMessage message) {
        return isExpectHeaderValid(message)
                && message.headers().contains(SipHeaderNames.EXPECT, SipHeaderValues.CONTINUE, true);
    }

    private static boolean isExpectHeaderValid(final SipMessage message) {
        return message instanceof SipRequest &&
                message.protocolVersion().compareTo(SipVersion.SIP_2_0) >= 0;
    }

    /**
     * 获取 InetSocketAddress 的IP和端口.
     *
     * @return 长度为2的字符串数组, [0] 表示IP,  [1] 表示端口.
     */
    public static String[] getAddr(InetSocketAddress address) {
        return address.toString().substring(1).split(":");
    }

}

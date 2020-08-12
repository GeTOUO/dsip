package com.dxp.sip.codec.sip;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author carzy
 * @date 2020/8/11
 */
public class DefaultSipRequestUdpDecoderTest {

    String CRLF = "\r\n";

    @Test
    public void testReceiveRegister() {
        byte[] data1 = ("MESSAGE sip:35010401402007000000@3501040140 SIP/2.0" + CRLF +
                "Via: SIP/2.0/UDP 172.16.0.108:5060;rport;branch=z9hG4bK176613160" + CRLF +
                "From: <sip:35010401401327000000@3501040140>;tag=853610686" + CRLF +
                "To: <sip:35010401402007000000@3501040140>" + CRLF +
                "Call-ID: 1073043680" + CRLF +
                "CSeq: 20 MESSAGE" + CRLF +
                "Content-Type: Application/MANSCDP+xml" + CRLF +
                "Max-Forwards: 70" + CRLF +
                "User-Agent: IP Camera" + CRLF +
                "Content-Length:   177" + CRLF +
                "" + CRLF +
                "<?xml version=\"1.0\" encoding=\"GB2312\"?>" + CRLF +
                "<Notify>" + CRLF +
                "<CmdType>Keepalive</CmdType>" + CRLF +
                "<SN>17</SN>" + CRLF +
                "<DeviceID>35010401401327000000</DeviceID>" + CRLF +
                "<Status>OK</Status>" + CRLF +
                "<Info>" + CRLF +
                "</Info>" + CRLF +
                "</Notify>").getBytes();

        EmbeddedChannel ch = new EmbeddedChannel(
                new SipObjectUdpDecoder(),
                new SipObjectAggregator(8192),
                new LoggingHandler(LogLevel.INFO)
        );
        ch.writeInbound(new DatagramPacket(Unpooled.wrappedBuffer(data1), new InetSocketAddress("127.0.0.1", 5858)));

        SipMessage res1 = ch.readInbound();
        assertNotNull(res1);
        assertTrue(res1 instanceof FullSipRequest);
        final FullSipRequest request = (FullSipRequest) res1;
        (request).release();
    }

}

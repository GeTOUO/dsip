package com.dxp.sip.codec.sip;

import com.dxp.sip.bus.handler.GbLoggingHandler;
import com.dxp.sip.util.CharsetUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author carzy
 * @date 2020/8/11
 */
public class DefaultSipRequestDecoderTest {

    String CRLF = "\r\n";

    @Test
    public void testReceiveRegister() {

        byte[] data2 = (
                "<?xml version=\"1.0\" encoding=\"GB2312\"?>" + CRLF +
                        "<Notify>" + CRLF +
                        "<CmdType>Keepalive</CmdType>" + CRLF +
                        "<SN>17</SN>" + CRLF +
                        "<DeviceID>35010401401327000000</DeviceID>" + CRLF +
                        "<Address>中文测试</Address>" + CRLF +
                        "<Status>OK</Status>" + CRLF +
                        "<Info>" + CRLF +
                        "</Info>" + CRLF +
                        "</Notify>"
        ).getBytes(CharsetUtils.GB_2313);

        byte[] data1 = ("MESSAGE sip:35010401402007000000@3501040140 SIP/2.0" + CRLF +
                "Via: SIP/2.0/UDP 172.16.0.108:5060;rport;branch=z9hG4bK176613160" + CRLF +
                "Via: SIP/2.0/UDP 172.16.0.109:5061;rport;branch=z9hG4bK176613161" + CRLF +
                "From: <sip:35010401401327000000@3501040140>;tag=853610686" + CRLF +
                "To: <sip:35010401402007000000@3501040140>" + CRLF +
                "Call-ID: 1073043680" + CRLF +
                "CSeq: 20 MESSAGE" + CRLF +
                "Content-Type: Application/MANSCDP+xml" + CRLF +
                "Max-Forwards: 70" + CRLF +
                "User-Agent: IP Camera" + CRLF +
                "Content-Length:   " + data2.length + CRLF + CRLF).getBytes();

        EmbeddedChannel ch = new EmbeddedChannel(
                new SipObjectDecoder(),
                new SipObjectAggregator(8192),
                new GbLoggingHandler(LogLevel.INFO)
        );
        ch.writeInbound(Unpooled.wrappedBuffer(data1, data2));

        SipMessage res1 = ch.readInbound();
        assertNotNull(res1);
        assertTrue(res1 instanceof FullSipRequest);
        final FullSipRequest request = (FullSipRequest) res1;
        final List<String> list = request.headers().getAll("via");
        list.forEach(System.out::println);
        System.out.println("request.headers().get(\"via\"):  " + request.headers().get("via"));
        (request).release();
    }

}

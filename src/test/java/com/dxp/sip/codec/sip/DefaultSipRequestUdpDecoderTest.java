package com.dxp.sip.codec.sip;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
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
        byte[] data1 = ("REGISTER sip:172.16.0.102:5060 SIP/2.0" + CRLF +
                "Via: SIP/2.0/UDP 172.16.0.102:5061;rport;branch=z9hG4bK2995420819" + CRLF +
                "From: <sip:35010401401127000000@3501040140>;tag=4112720925" + CRLF +
                "To: <sip:35010401401127000000@3501040140>" + CRLF +
                "Call-ID: 667210238" + CRLF +
                "CSeq: 1 REGISTER" + CRLF +
                "Contact: <sip:35010401401127000000@172.16.0.102:5061>" + CRLF +
                "Max-Forwards: 70" + CRLF +
                "User-Agent: HIC PUSHER" + CRLF +
                "Expires: 30" + CRLF +
                "Content-Length: 0\r\n\r\n").getBytes();

        EmbeddedChannel ch = new EmbeddedChannel(
                new SipObjectUdpDecoder(),
                new SipObjectAggregator(8192)
        );
        ch.writeInbound(new DatagramPacket(Unpooled.wrappedBuffer(data1), new InetSocketAddress("127.0.0.1", 5858)));

        SipMessage res1 = ch.readInbound();
        assertNotNull(res1);
        assertTrue(res1 instanceof FullSipRequest);
        final FullSipRequest request = (FullSipRequest) res1;
        final AbstractSipHeaders headers = request.headers();
        System.out.println(headers.contains(SipHeaderNames.TO));
        System.out.println(headers.contains(SipHeaderNames.FROM));
        headers.entries().forEach(e -> {
            System.out.println(e.getKey() + ":" + e.getValue());
        });
        System.out.println("URL ===> " + request.uri());
        System.out.println("method ===> " + request.method().name());
        System.out.println("headers ===> " + headers.toString());
        System.out.println("content ===> " + request.content().readCharSequence(request.content().readableBytes(), StandardCharsets.UTF_8));

        (request).release();
    }

}

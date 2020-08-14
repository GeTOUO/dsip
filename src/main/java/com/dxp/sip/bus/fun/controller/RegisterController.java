package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.HandlerController;
import com.dxp.sip.codec.sip.*;
import io.netty.channel.Channel;

/**
 * @author carzy
 * @date 2020/8/14
 */
public class RegisterController implements HandlerController {

    @Override
    public SipMethod method() {
        return SipMethod.REGISTER;
    }

    @Override
    public void handler(FullSipRequest request, Channel channel) {
        final AbstractSipHeaders headers = request.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(SipResponseStatus.UNAUTHORIZED);
        response.setRecipient(request.recipient());
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.USER_AGENT, "d-sip");
        if (!headers.contains(SipHeaderNames.AUTHORIZATION)) {
            String wwwAuth = "Digest realm=\"31011000002001234567\", nonce=\"" +
                    "b700dc7cb094478503a21148184a3731\", opaque=\"5b279c2efd18d123d1f4a2182527a281\", algorithm=MD5";
            h.set(SipHeaderNames.WWW_AUTHENTICATE, wwwAuth);
        } else {
            response.setStatus(SipResponseStatus.OK);
        }
        channel.writeAndFlush(response);
    }
}

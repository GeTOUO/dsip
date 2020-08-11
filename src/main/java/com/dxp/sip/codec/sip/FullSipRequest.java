package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;

/**
 * @author carzy
 * @date 2020/8/11
 */
public interface FullSipRequest extends SipRequest, FullSipMessage {

    @Override
    FullSipRequest copy();

    @Override
    FullSipRequest duplicate();

    @Override
    FullSipRequest retainedDuplicate();

    @Override
    FullSipRequest replace(ByteBuf content);

    @Override
    FullSipRequest retain(int increment);

    @Override
    FullSipRequest retain();

    @Override
    FullSipRequest touch();

    @Override
    FullSipRequest touch(Object hint);

    @Override
    FullSipRequest setProtocolVersion(SipVersion version);

    @Override
    FullSipRequest setMethod(SipMethod method);

    @Override
    FullSipRequest setUri(String uri);

}

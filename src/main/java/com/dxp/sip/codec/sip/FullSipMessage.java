package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;

/**
 * @author carzy
 * @date 2020/8/11
 */
public interface FullSipMessage extends SipMessage, LastSipContent {

    @Override
    FullSipMessage copy();

    @Override
    FullSipMessage duplicate();

    @Override
    FullSipMessage retainedDuplicate();

    @Override
    FullSipMessage replace(ByteBuf content);

    @Override
    FullSipMessage retain(int increment);

    @Override
    FullSipMessage retain();

    @Override
    FullSipMessage touch();

    @Override
    FullSipMessage touch(Object hint);

}

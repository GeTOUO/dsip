package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

/**
 * @author carzy
 */
public interface SipContent extends SipObject, ByteBufHolder {

    /**
     * <code>copy</code>
     *
     * @return SipContent
     */
    @Override
    SipContent copy();

    /**
     * <code>copy</code>
     *
     * @return SipContent
     */
    @Override
    SipContent duplicate();

    /**
     * <code>retainedDuplicate</code>
     *
     * @return SipContent
     */
    @Override
    SipContent retainedDuplicate();

    /**
     * <code>replace</code>
     *
     * @return SipContent
     */
    @Override
    SipContent replace(ByteBuf content);

    /**
     * <code>retain</code>
     *
     * @return SipContent
     */
    @Override
    SipContent retain();

    /**
     * <code>retain</code>
     *
     * @param increment 保持次数
     * @return SipContent
     */
    @Override
    SipContent retain(int increment);

    /**
     * <code>touch</code>
     *
     * @return SipContent
     */
    @Override
    SipContent touch();

    /**
     * <code>touch</code>
     *
     * @param hint 消费次数
     * @return SipContent
     */
    @Override
    SipContent touch(Object hint);
}
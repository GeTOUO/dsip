package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * LastSipContent 默认实现
 *
 * The default {@link LastSipContent} implementation.
 *
 * @author carzy
 * @date 2020/8/10
 */
public class DefaultLastSipContent extends DefaultSipContent implements LastSipContent {

    private final boolean validateHeaders;

    public DefaultLastSipContent() {
        this(Unpooled.buffer(0));
    }

    public DefaultLastSipContent(ByteBuf content) {
        this(content, true);
    }

    public DefaultLastSipContent(ByteBuf content, boolean validateHeaders) {
        super(content);
        this.validateHeaders = validateHeaders;
    }

    @Override
    public LastSipContent copy() {
        return replace(content().copy());
    }

    @Override
    public LastSipContent duplicate() {
        return replace(content().duplicate());
    }

    @Override
    public LastSipContent retainedDuplicate() {
        return replace(content().retainedDuplicate());
    }

    @Override
    public LastSipContent replace(ByteBuf content) {
        return new DefaultLastSipContent(content, validateHeaders);
    }

    @Override
    public LastSipContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public LastSipContent retain() {
        super.retain();
        return this;
    }

    @Override
    public LastSipContent touch() {
        super.touch();
        return this;
    }

    @Override
    public LastSipContent touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
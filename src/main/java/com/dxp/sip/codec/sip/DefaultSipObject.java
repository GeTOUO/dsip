package com.dxp.sip.codec.sip;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;

import java.net.InetSocketAddress;

/**
 * @author carzy
 * @date 2020/8/10
 */
public class DefaultSipObject implements SipObject {

    private static final int HASH_CODE_PRIME = 31;
    private DecoderResult decoderResult = DecoderResult.SUCCESS;

    protected DefaultSipObject() {
        // Disallow direct instantiation
    }

    @Override
    public DecoderResult decoderResult() {
        return decoderResult;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {
        this.decoderResult = ObjectUtil.checkNotNull(decoderResult, "decoderResult");
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = HASH_CODE_PRIME * result + decoderResult.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultSipObject)) {
            return false;
        }

        DefaultSipObject other = (DefaultSipObject) o;

        return decoderResult().equals(other.decoderResult());
    }
}
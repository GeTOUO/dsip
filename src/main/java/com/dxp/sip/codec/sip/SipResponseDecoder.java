//package com.dxp.sip.codec.sip;
//
///**
// * sip请求
// *
// * @author carzy
// * @date 2020/8/11
// */
//public class SipResponseDecoder extends SipObjectDecoder {
//
//    private static final SipResponseStatus UNKNOWN_STATUS = new SipResponseStatus(999, "Unknown");
//
//    public SipResponseDecoder() {
//    }
//
//    public SipResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
//        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
//    }
//
//    public SipResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders,
//                              int initialBufferSize) {
//        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize);
//    }
//
//    @Override
//    protected boolean isDecodingRequest() {
//        return false;
//    }
//
//    @Override
//    protected SipMessage createMessage(String[] initialLine) {
//        return new DefaultSipResponse(
//                SipVersion.valueOf(initialLine[0]),
//                SipResponseStatus.valueOf(Integer.parseInt(initialLine[1]), initialLine[2]), validateHeaders);
//    }
//
//    @Override
//    protected SipMessage createInvalidMessage() {
//        return new DefaultFullSipResponse(SipVersion.SIP_2_0, UNKNOWN_STATUS, validateHeaders);
//    }
//}

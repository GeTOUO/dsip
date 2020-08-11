//package com.dxp.sip.codec.sip;
//
///**
// * @author carzy
// */
//public class SipRequestDecoder extends SipObjectDecoder {
//
//    /**
//     * Creates a new instance with the default
//     * {@code maxInitialLineLength (4096)}, {@code maxHeaderSize (8192)}, and
//     * {@code maxChunkSize (8192)}.
//     */
//    public SipRequestDecoder() {
//    }
//
//    /**
//     * Creates a new instance with the specified parameters.
//     */
//    public SipRequestDecoder(
//            int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
//        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
//    }
//
//    public SipRequestDecoder(
//            int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
//        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
//    }
//
//    public SipRequestDecoder(
//            int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders,
//            int initialBufferSize) {
//        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders,
//                initialBufferSize);
//    }
//
//    public SipRequestDecoder(
//            int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders,
//            int initialBufferSize, boolean allowDuplicateContentLengths) {
//        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize);
//    }
//
//    @Override
//    protected SipMessage createMessage(String[] initialLine) throws Exception {
//        return new DefaultSipRequest(
//                SipVersion.valueOf(initialLine[2]),
//                SipMethod.valueOf(initialLine[0]),
//                initialLine[1],
//                validateHeaders);
//    }
//
//    @Override
//    protected SipMessage createInvalidMessage() {
//        return new DefaultFullSipRequest(SipVersion.SIP_2_0, SipMethod.NOTIFY, "/bad-request", validateHeaders);
//    }
//
//    @Override
//    protected boolean isDecodingRequest() {
//        return true;
//    }
//}
package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

import static io.netty.handler.codec.http.HttpConstants.SP;
import static io.netty.util.ByteProcessor.FIND_ASCII_SPACE;
import static io.netty.util.internal.ObjectUtil.checkPositiveOrZero;
import static java.lang.Integer.parseInt;

/**
 * sip响应码
 *
 * @author carzy
 * @date 2020/8/11
 */
public class SipResponseStatus implements Comparable<SipResponseStatus> {

    /**
     * 1xx.
     */
    public static final SipResponseStatus TRYING = newStatus(100, "Trying");
    public static final SipResponseStatus RINGING = newStatus(180, "Ringing");
    public static final SipResponseStatus CALL_BEING_FORWARD = newStatus(181, "CallBeingForward");
    public static final SipResponseStatus QUEUED = newStatus(182, "Queued");
    public static final SipResponseStatus SESSION_PROGRESS = newStatus(183, "SessionProgress");

    /**
     * 2xx
     */
    public static final SipResponseStatus OK = newStatus(200, "OK");
    public static final SipResponseStatus ACCEPTED = newStatus(202, "Accepted");

    /**
     * 3xx
     */
    public static final SipResponseStatus MULTIPLE_CHOICES = newStatus(300, "Multiple Choices");
    public static final SipResponseStatus MOVED_PERMANENTLY = newStatus(301, "Moved Permanently");
    public static final SipResponseStatus MOVED_TEMPORARILY = newStatus(302, "Moved Temporarily");
    public static final SipResponseStatus USE_PROXY = newStatus(305, "Use Proxy");
    public static final SipResponseStatus AlternativeService = newStatus(380, "AlternativeService");


    /**
     * 4xx
     */
    public static final SipResponseStatus BAD_REQUEST = newStatus(400, "Bad Request");
    public static final SipResponseStatus UNAUTHORIZED = newStatus(401, "Unauthorized");
    public static final SipResponseStatus PAYMENT_REQUIRED = newStatus(402, "Payment Required");
    public static final SipResponseStatus FORBIDDEN = newStatus(403, "Forbidden");
    public static final SipResponseStatus NOT_FOUND = newStatus(404, "Not Found");
    public static final SipResponseStatus METHOD_NOT_ALLOWED = newStatus(405, "Method Not Allowed");
    public static final SipResponseStatus NOT_ACCEPTABLE = newStatus(406, "Not Acceptable");
    public static final SipResponseStatus PROXY_AUTHENTICATION_REQUIRED =
            newStatus(407, "Proxy Authentication Required");
    public static final SipResponseStatus REQUEST_TIMEOUT = newStatus(408, "Request Timeout");
    public static final SipResponseStatus CONFLICT = newStatus(409, "Conflict");
    public static final SipResponseStatus GONE = newStatus(410, "Gone");
    public static final SipResponseStatus LENGTH_REQUIRED = newStatus(411, "Length Required");
    public static final SipResponseStatus PRECONDITION_FAILED = newStatus(412, "Precondition Failed");
    public static final SipResponseStatus REQUEST_ENTITY_TOO_LARGE =
            newStatus(413, "Request Entity Too Large");
    public static final SipResponseStatus REQUEST_URI_TOO_LONG = newStatus(414, "Request-URI Too Long");
    public static final SipResponseStatus UNSUPPORTED_MEDIA_TYPE = newStatus(415, "Unsupported Media Type");
    public static final SipResponseStatus UNSUPPORTED_URI_SCHEME = newStatus(416, "Unsupported URIScheme");
    public static final SipResponseStatus EXPECTATION_FAILED = newStatus(417, "Expectation Failed");
    public static final SipResponseStatus BAD_EXTENSION = newStatus(420, "Bad Extension");
    public static final SipResponseStatus EXTENSION_REQUIRED = newStatus(421, "Extension Required");
    public static final SipResponseStatus SESSION_TIMER_TOO_SMALL = newStatus(422, "Session Timer Too Small");
    public static final SipResponseStatus INTERVAL_TOO_BRIEF = newStatus(423, "IntervalTooBrief");
    public static final SipResponseStatus TEMPORARILY_UNAVAILABLE = newStatus(480, "Temporarily Unavailable");
    public static final SipResponseStatus CALL_OR_TRANSACTION_DOES_NOT_EXIST = newStatus(481, "CallORTransaction Does Not Exist");
    public static final SipResponseStatus LOOP_DETECTED = newStatus(482, "LoopDetected");
    public static final SipResponseStatus TOO_MANY_HOPS = newStatus(483, "TooManyHops");
    public static final SipResponseStatus ADDRESS_INCOMPLETE = newStatus(484, "AddressIncomplete");
    public static final SipResponseStatus AMBIGUOUS = newStatus(485, "Ambiguous");
    public static final SipResponseStatus BUSY_HERE = newStatus(486, "BusyHere");
    public static final SipResponseStatus REQUEST_TERMINATED = newStatus(487, "RequestTerminated");
    public static final SipResponseStatus NOT_ACCEPTABLE_HERE = newStatus(488, "NotAcceptableHere");
    public static final SipResponseStatus BAD_EVENT = newStatus(489, "BadEvent");
    public static final SipResponseStatus REQUEST_UPDATED = newStatus(490, "RequestUpdated");
    public static final SipResponseStatus REQUEST_PENDING = newStatus(491, "RequestPending");
    public static final SipResponseStatus UNDECIPHERABLE = newStatus(493, "Undecipherable");

    /**
     * 5xx
     */
    public static final SipResponseStatus INTERNAL_SERVER_ERROR = newStatus(500, "Internal Server Error");
    public static final SipResponseStatus NOT_IMPLEMENTED = newStatus(501, "Not Implemented");
    public static final SipResponseStatus BAD_GATEWAY = newStatus(502, "Bad Gateway");
    public static final SipResponseStatus SERVICE_UNAVAILABLE = newStatus(503, "Service Unavailable");
    public static final SipResponseStatus SERVER_TIMEOUT = newStatus(504, "Server Timeout");
    public static final SipResponseStatus SIP_VERSION_NOT_SUPPORTED =
            newStatus(505, "Sip Version Not Supported");
    public static final SipResponseStatus MESSAGE_TOO_LARGE = newStatus(513, "Message Too Large");
    public static final SipResponseStatus PRECONDITION_FAILURE = newStatus(580, "PreconditionFailure");

    /**
     * 6xx
     */
    public static final SipResponseStatus BUSY_EVERYWHERE = newStatus(600, "Busy Everywhere");
    public static final SipResponseStatus DECLINE = newStatus(603, "Decline");
    public static final SipResponseStatus DOES_NOT_EXIST_ANYWHERE = newStatus(604, "DoesNotExistAnywhere");
    public static final SipResponseStatus NOT_ACCEPTABLE_606 = newStatus(604, "NotAcceptable6");

    /**
     * 7xx
     */
    public static final SipResponseStatus NO_RESPONSE_FROM_DESTINATION_SERVER = newStatus(701, "NoResponseFromDestinationServer");
    public static final SipResponseStatus UNABLE_TO_RESOLVE_DESTINATION_SERVER = newStatus(702, "UnableToResolveDestinationServer");
    public static final SipResponseStatus ERROR_SENDING_MESSAGE_TO_DESTINATION_SERVER = newStatus(703, "ErrorSendingMessageToDestinationServer");

    private static SipResponseStatus newStatus(int statusCode, String reasonPhrase) {
        return new SipResponseStatus(statusCode, reasonPhrase, true);
    }

    public static SipResponseStatus valueOf(int code, String reasonPhrase) {
        SipResponseStatus responseStatus = valueOf0(code);
        return responseStatus != null && responseStatus.reasonPhrase().contentEquals(reasonPhrase) ? responseStatus :
                new SipResponseStatus(code, reasonPhrase);
    }

    public static SipResponseStatus parseLine(CharSequence line) {
        return (line instanceof AsciiString) ? parseLine((AsciiString) line) : parseLine(line.toString());
    }

    public static SipResponseStatus parseLine(String line) {
        try {
            int space = line.indexOf(' ');
            return space == -1 ? valueOf(parseInt(line)) :
                    valueOf(parseInt(line.substring(0, space)), line.substring(space + 1));
        } catch (Exception e) {
            throw new IllegalArgumentException("malformed status line: " + line, e);
        }
    }

    public static SipResponseStatus parseLine(AsciiString line) {
        try {
            int space = line.forEachByte(FIND_ASCII_SPACE);
            return space == -1 ? valueOf(line.parseInt()) : valueOf(line.parseInt(0, space), line.toString(space + 1));
        } catch (Exception e) {
            throw new IllegalArgumentException("malformed status line: " + line, e);
        }
    }

    private final int code;
    private final AsciiString codeAsText;
    private HttpStatusClass codeClass;

    private final String reasonPhrase;
    private final byte[] bytes;

    public static SipResponseStatus valueOf(int code) {
        SipResponseStatus status = valueOf0(code);
        return status != null ? status : new SipResponseStatus(code);
    }

    private static SipResponseStatus valueOf0(int code) {
        switch (code) {
            case 100:
                return TRYING;
            case 180:
                return RINGING;
            case 181:
                return CALL_BEING_FORWARD;
            case 182:
                return QUEUED;
            case 183:
                return SESSION_PROGRESS;
            case 200:
                return OK;
            case 202:
                return ACCEPTED;
            case 300:
                return MULTIPLE_CHOICES;
            case 301:
                return MOVED_PERMANENTLY;
            case 302:
                return MOVED_TEMPORARILY;
            case 305:
                return USE_PROXY;
            case 380:
                return AlternativeService;
            case 400:
                return BAD_REQUEST;
            case 401:
                return UNAUTHORIZED;
            case 402:
                return PAYMENT_REQUIRED;
            case 403:
                return FORBIDDEN;
            case 404:
                return NOT_FOUND;
            case 405:
                return METHOD_NOT_ALLOWED;
            case 406:
                return NOT_ACCEPTABLE;
            case 407:
                return PROXY_AUTHENTICATION_REQUIRED;
            case 408:
                return REQUEST_TIMEOUT;
            case 409:
                return CONFLICT;
            case 410:
                return GONE;
            case 411:
                return LENGTH_REQUIRED;
            case 412:
                return PRECONDITION_FAILED;
            case 413:
                return REQUEST_ENTITY_TOO_LARGE;
            case 414:
                return REQUEST_URI_TOO_LONG;
            case 415:
                return UNSUPPORTED_MEDIA_TYPE;
            case 416:
                return UNSUPPORTED_URI_SCHEME;
            case 417:
                return EXPECTATION_FAILED;
            case 420:
                return BAD_EXTENSION;
            case 421:
                return EXTENSION_REQUIRED;
            case 422:
                return SESSION_TIMER_TOO_SMALL;
            case 423:
                return INTERVAL_TOO_BRIEF;
            case 480:
                return TEMPORARILY_UNAVAILABLE;
            case 481:
                return CALL_OR_TRANSACTION_DOES_NOT_EXIST;
            case 482:
                return LOOP_DETECTED;
            case 483:
                return TOO_MANY_HOPS;
            case 484:
                return ADDRESS_INCOMPLETE;
            case 485:
                return AMBIGUOUS;
            case 486:
                return BUSY_HERE;
            case 487:
                return REQUEST_TERMINATED;
            case 488:
                return NOT_ACCEPTABLE_HERE;
            case 489:
                return BAD_EVENT;
            case 490:
                return REQUEST_UPDATED;
            case 491:
                return REQUEST_PENDING;
            case 493:
                return UNDECIPHERABLE;
            case 500:
                return INTERNAL_SERVER_ERROR;
            case 501:
                return NOT_IMPLEMENTED;
            case 502:
                return BAD_GATEWAY;
            case 503:
                return SERVICE_UNAVAILABLE;
            case 504:
                return SERVER_TIMEOUT;
            case 505:
                return SIP_VERSION_NOT_SUPPORTED;
            case 513:
                return MESSAGE_TOO_LARGE;
            case 580:
                return PRECONDITION_FAILURE;
            case 600:
                return BUSY_EVERYWHERE;
            case 603:
                return DECLINE;
            case 604:
                return DOES_NOT_EXIST_ANYWHERE;
            case 606:
                return NOT_ACCEPTABLE_606;
            case 701:
                return NO_RESPONSE_FROM_DESTINATION_SERVER;
            case 702:
                return UNABLE_TO_RESOLVE_DESTINATION_SERVER;
            case 703:
                return ERROR_SENDING_MESSAGE_TO_DESTINATION_SERVER;
            default:
                return null;
        }
    }

    private SipResponseStatus(int code) {
        this(code, SipStatusClass.valueOf(code).defaultReasonPhrase() + " (" + code + ')', false);
    }

    public SipResponseStatus(int code, String reasonPhrase) {
        this(code, reasonPhrase, false);
    }

    private SipResponseStatus(int code, String reasonPhrase, boolean bytes) {
        checkPositiveOrZero(code, "code");
        ObjectUtil.checkNotNull(reasonPhrase, "reasonPhrase");

        for (int i = 0; i < reasonPhrase.length(); i++) {
            char c = reasonPhrase.charAt(i);
            // Check prohibited characters.
            if (c == '\n' || c == '\r') {
                throw new IllegalArgumentException(
                        "reasonPhrase contains one of the following prohibited characters: " +
                                "\\r\\n: " + reasonPhrase);
            }
        }

        this.code = code;
        String codeString = Integer.toString(code);
        codeAsText = new AsciiString(codeString);
        this.reasonPhrase = reasonPhrase;
        if (bytes) {
            this.bytes = (codeString + ' ' + reasonPhrase).getBytes(CharsetUtil.US_ASCII);
        } else {
            this.bytes = null;
        }
    }

    public int code() {
        return code;
    }

    public AsciiString codeAsText() {
        return codeAsText;
    }

    public String reasonPhrase() {
        return reasonPhrase;
    }

    public HttpStatusClass codeClass() {
        HttpStatusClass type = this.codeClass;
        if (type == null) {
            this.codeClass = type = HttpStatusClass.valueOf(code);
        }
        return type;
    }

    @Override
    public int hashCode() {
        return code();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SipResponseStatus)) {
            return false;
        }

        return code() == ((SipResponseStatus) o).code();
    }


    @Override
    public int compareTo(SipResponseStatus o) {
        return code() - o.code();
    }

    @Override
    public String toString() {
        return String.valueOf(codeAsText) +
                ' ' +
                reasonPhrase;
    }

    void encode(ByteBuf buf) {
        if (bytes == null) {
            ByteBufUtil.copy(codeAsText, buf);
            buf.writeByte(SP);
            buf.writeCharSequence(reasonPhrase, CharsetUtil.US_ASCII);
        } else {
            buf.writeBytes(bytes);
        }
    }
}

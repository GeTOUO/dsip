package com.dxp.sip.codec.sip;

import io.netty.handler.codec.*;
import io.netty.handler.codec.DefaultHeaders.NameValidator;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

import java.util.*;

import static io.netty.util.AsciiString.CASE_SENSITIVE_HASHER;

/**
 * 默认的sip头信息集合
 *
 * @author carzy
 * @date 2020/8/10
 */
public class DefaultSipHeaders extends AbstractSipHeaders {

    private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = ~16;
    private static final int ASCII_MAX_INT = 127;
    private static final ByteProcessor HEADER_NAME_VALIDATOR = value -> {
        DefaultSipHeaders.validateHeaderNameElement(value);
        return true;
    };
    /**
     * 头信息的名称校验器. 按字节检测.
     */
    static final NameValidator<CharSequence> SIP_NAME_VALIDATOR = name -> {
        if (name != null && name.length() != 0) {
            if (name instanceof AsciiString) {
                try {
                    ((AsciiString) name).forEachByte(DefaultSipHeaders.HEADER_NAME_VALIDATOR);
                } catch (Exception var3) {
                    PlatformDependent.throwException(var3);
                }
            } else {
                for (int index = 0; index < name.length(); ++index) {
                    DefaultSipHeaders.validateHeaderNameElement(name.charAt(index));
                }
            }
        } else {
            throw new IllegalArgumentException("empty headers are not allowed [" + name + "]");
        }
    };

    private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

    public DefaultSipHeaders() {
        this(true);
    }

    public DefaultSipHeaders(boolean validate) {
        this(validate, nameValidator(validate));
    }

    protected DefaultSipHeaders(boolean validate, NameValidator<CharSequence> nameValidator) {
        this(new DefaultHeadersImpl<>(AsciiString.CASE_INSENSITIVE_HASHER, valueConverter(validate), nameValidator));
    }

    protected DefaultSipHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
        this.headers = headers;
    }

    @Override
    public AbstractSipHeaders add(AbstractSipHeaders headers) {
        if (headers instanceof DefaultSipHeaders) {
            this.headers.add(((DefaultSipHeaders) headers).headers);
            return this;
        } else {
            return super.add(headers);
        }
    }

    @Override
    public AbstractSipHeaders set(AbstractSipHeaders headers) {
        if (headers instanceof DefaultSipHeaders) {
            this.headers.set(((DefaultSipHeaders) headers).headers);
            return this;
        } else {
            return super.set(headers);
        }
    }

    /**
     * add 方法.
     */
    @Override
    public AbstractSipHeaders add(String name, Object value) {
        this.headers.addObject(name, value);
        return this;
    }

    @Override
    public AbstractSipHeaders add(String name, Iterable<?> values) {
        this.headers.addObject(name, values);
        return this;
    }

    @Override
    public AbstractSipHeaders addInt(CharSequence name, int value) {
        this.headers.addObject(name, value);
        return this;
    }

    @Override
    public AbstractSipHeaders addShort(CharSequence name, short value) {
        this.headers.addObject(name, value);
        return this;
    }

    /**
     * remove 方法区
     */
    @Override
    public AbstractSipHeaders remove(String name) {
        this.headers.remove(name);
        return this;
    }

    @Override
    public AbstractSipHeaders remove(CharSequence name) {
        this.headers.remove(name);
        return this;
    }

    /**
     * set 方法区
     */
    @Override
    public AbstractSipHeaders set(String name, Object value) {
        this.headers.setObject(name, value);
        return this;
    }

    @Override
    public AbstractSipHeaders set(CharSequence name, Object value) {
        this.headers.setObject(name, value);
        return this;
    }

    @Override
    public AbstractSipHeaders set(String name, Iterable<?> values) {
        this.headers.setObject(name, values);
        return this;
    }

    @Override
    public AbstractSipHeaders set(CharSequence name, Iterable<?> values) {
        this.headers.setObject(name, values);
        return this;
    }

    @Override
    public AbstractSipHeaders setInt(CharSequence name, int value) {
        this.headers.setObject(name, value);
        return this;
    }

    @Override
    public AbstractSipHeaders setShort(CharSequence name, short value) {
        this.headers.setObject(name, value);
        return this;
    }

    /**
     * 清空现有缓存的头信息.
     */
    @Override
    public AbstractSipHeaders clear() {
        this.headers.clear();
        return this;
    }

    /**
     * 获取头信息
     */
    @Override
    public String get(String name) {
        return this.get((CharSequence) name);
    }

    @Override
    public String get(CharSequence name) {
        return HeadersUtils.getAsString(this.headers, name);
    }

    @Override
    public Integer getInt(CharSequence name) {
        return this.headers.getInt(name);
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        return this.headers.getInt(name, defaultValue);
    }

    @Override
    public Short getShort(CharSequence name) {
        return this.headers.getShort(name);
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        return this.headers.getShort(name, defaultValue);
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        return this.headers.getTimeMillis(name);
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        return this.headers.getTimeMillis(name, defaultValue);
    }

    @Override
    public List<String> getAll(String name) {
        return this.getAll((CharSequence) name);
    }

    @Override
    public List<String> getAll(CharSequence name) {
        return HeadersUtils.getAllAsString(this.headers, name);
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Map.Entry<String, String>> entriesConverted = new ArrayList<>(this.headers.size());

            for (Map.Entry<String, String> entry : this) {
                entriesConverted.add(entry);
            }

            return entriesConverted;
        }
    }

    /**
     * 判断是否包含某个头信息.
     */
    @Override
    public boolean contains(String name) {
        return this.contains((CharSequence) name);
    }

    @Override
    public boolean contains(CharSequence name) {
        return headers.contains(name);
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCase) {
        return this.contains((CharSequence) name, value, ignoreCase);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        return this.headers.contains(name, value, ignoreCase ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public Set<String> names() {
        return HeadersUtils.namesAsString(headers);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DefaultSipHeaders
                && headers.equals(((DefaultSipHeaders) o).headers, CASE_SENSITIVE_HASHER);
    }

    @Override
    public int hashCode() {
        return headers.hashCode(CASE_SENSITIVE_HASHER);
    }

    @Override
    public AbstractSipHeaders copy() {
        return new DefaultSipHeaders(headers.copy());
    }

    /**
     * 迭代器
     */
    @Override
    @Deprecated
    public Iterator<Map.Entry<String, String>> iterator() {
        return HeadersUtils.iteratorAsString(headers);
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return headers.iterator();
    }

    @Override
    public Iterator<String> valueStringIterator(CharSequence name) {
        final Iterator<CharSequence> itr = valueCharSequenceIterator(name);
        return new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public String next() {
                return itr.next().toString();
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }

    @Override
    public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
        return headers.valueIterator(name);
    }


    /**
     * 校验头名称.
     */
    private static void validateHeaderNameElement(byte value) {
        switch (value) {
            case 0x00:
            case '\t':
            case '\n':
            case 0x0b:
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case ';':
            case '=':
                throw new IllegalArgumentException(
                        "a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " +
                                value);
            default:
                if (value < 0) {
                    throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
                }
        }
    }

    /**
     * 校验头名称.
     */
    private static void validateHeaderNameElement(char value) {
        switch (value) {
            case '\u0000':
            case '\t':
            case '\n':
            case '\u000b':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case ';':
            case '=':
                throw new IllegalArgumentException("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + value);
            default:
                if (value > ASCII_MAX_INT) {
                    throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
                }
        }
    }

    static ValueConverter<CharSequence> valueConverter(boolean validate) {
        return (validate ? DefaultSipHeaders.HeaderValueConverterAndValidator.INSTANCE : DefaultSipHeaders.HeaderValueConverter.INSTANCE);
    }

    /**
     * 名称校验器.
     * 默认只要不为null就可以.
     * 通过 validate 控制, 通过构造器传入,构造器默认值为 true.
     */
    static NameValidator<CharSequence> nameValidator(boolean validate) {
        return validate ? SIP_NAME_VALIDATOR : name -> ObjectUtil.checkNotNull(name, "name");
    }

    /**
     * 默认的值转换器.
     */
    private static class HeaderValueConverter extends CharSequenceValueConverter {
        static final DefaultSipHeaders.HeaderValueConverter INSTANCE = new DefaultSipHeaders.HeaderValueConverter();

        private HeaderValueConverter() {
        }

        @Override
        public CharSequence convertObject(Object value) {
            if (value instanceof CharSequence) {
                return (CharSequence) value;
            } else if (value instanceof Date) {
                return DateFormatter.format((Date) value);
            } else {
                return value instanceof Calendar ? DateFormatter.format(((Calendar) value).getTime()) : value.toString();
            }
        }
    }

    /**
     * 消息头值转换器. 扩展 HeaderValueConverter. 并对值进行校验.
     */
    private static final class HeaderValueConverterAndValidator extends DefaultSipHeaders.HeaderValueConverter {
        static final DefaultSipHeaders.HeaderValueConverterAndValidator INSTANCE = new DefaultSipHeaders.HeaderValueConverterAndValidator();

        private HeaderValueConverterAndValidator() {
        }

        @Override
        public CharSequence convertObject(Object value) {
            CharSequence seq = super.convertObject(value);
            int state = 0;

            for (int index = 0; index < seq.length(); ++index) {
                state = validateValueChar(seq, state, seq.charAt(index));
            }

            if (state != 0) {
                throw new IllegalArgumentException("a header value must not end with '\\r' or '\\n':" + seq);
            } else {
                return seq;
            }
        }

        private static int validateValueChar(CharSequence seq, int state, char character) {

            if ((character & HIGHEST_INVALID_VALUE_CHAR_MASK) == 0) {
                switch (character) {
                    case '\u0000':
                        throw new IllegalArgumentException("a header value contains a prohibited character '\u0000': " + seq);
                    case '\u000b':
                        throw new IllegalArgumentException("a header value contains a prohibited character '\\v': " + seq);
                    case '\f':
                        throw new IllegalArgumentException("a header value contains a prohibited character '\\f': " + seq);
                    default:
                }
            }

            switch (state) {
                case 0:
                    switch (character) {
                        case '\n':
                            return 2;
                        case '\r':
                            return 1;
                        default:
                            return state;
                    }
                case 1:
                    if (character == '\n') {
                        return 2;
                    }
                    throw new IllegalArgumentException("only '\\n' is allowed after '\\r': " + seq);
                case 2:
                    switch (character) {
                        case '\t':
                        case ' ':
                            return 0;
                        default:
                            throw new IllegalArgumentException("only ' ' and '\\t' are allowed after '\\n': " + seq);
                    }
                default:
                    return state;
            }
        }
    }

}

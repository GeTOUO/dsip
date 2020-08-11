package com.dxp.sip.codec.sip;

import io.netty.handler.codec.Headers;
import io.netty.handler.codec.HeadersUtils;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;

import java.util.*;
import java.util.Map.Entry;

import static io.netty.util.AsciiString.*;
import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * Sip 消息头. 参照 AbstractSipHeaders. 地下的实现类需要提供给外部的一些获取方法.
 *
 * @author carzy
 * @see io.netty.handler.codec.http.HttpHeaders
 * @see HashMap
 */
public abstract class AbstractSipHeaders implements Iterable<Entry<String, String>> {

    protected AbstractSipHeaders() {
    }

    /**
     * @see #get(CharSequence)
     */
    public abstract String get(String name);

    /**
     * 返回对应的头信息,如果没有则返回null.
     */
    public String get(CharSequence name) {
        return get(name.toString());
    }

    /**
     * 返回对应的头信息,如果没有则返回defaultValue.
     */
    public String get(CharSequence name, String defaultValue) {
        String value = get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 返回对应的头信息,如果没有则返回null.
     */
    public abstract Integer getInt(CharSequence name);

    /**
     * 返回对应的头信息,如果没有则返回默认值
     */
    public abstract int getInt(CharSequence name, int defaultValue);

    /**
     * 返回对应的头信息,如果没有则返回null.
     */
    public abstract Short getShort(CharSequence name);

    /**
     * 返回对应的头信息,如果没有则返回默认值
     */
    public abstract short getShort(CharSequence name, short defaultValue);

    /**
     * 返回对应的头信息,如果没有则返回null
     */
    public abstract Long getTimeMillis(CharSequence name);

    /**
     * 返回对应的头信息,如果没有则返回默认值
     */
    public abstract long getTimeMillis(CharSequence name, long defaultValue);

    /**
     * @see #getAll(CharSequence)
     */
    public abstract List<String> getAll(String name);

    /**
     * 返回对应的头信息,如果没有则返回null
     */
    public List<String> getAll(CharSequence name) {
        return getAll(name.toString());
    }

    /**
     * Returns a new {@link List} that contains all headers in this object.  Note that modifying the
     * returned {@link List} will not affect the state of this object.  If you intend to enumerate over the header
     * entries only, use {@link #iterator()} instead, which has much less overhead.
     *
     * @see #iteratorCharSequence()
     */
    public abstract List<Entry<String, String>> entries();

    /**
     * @see #contains(CharSequence)
     */
    public abstract boolean contains(String name);

    /**
     * @return Iterator over the name/value header pairs.
     */
    public abstract Iterator<Entry<CharSequence, CharSequence>> iteratorCharSequence();

    /**
     * Equivalent to {@link #getAll(String)} but it is possible that no intermediate list is generated.
     *
     * @param name the name of the header to retrieve
     * @return an {@link Iterator} of header values corresponding to {@code name}.
     */
    public Iterator<String> valueStringIterator(CharSequence name) {
        return getAll(name).iterator();
    }

    /**
     * Equivalent to {@link #getAll(String)} but it is possible that no intermediate list is generated.
     *
     * @param name the name of the header to retrieve
     * @return an {@link Iterator} of header values corresponding to {@code name}.
     */
    public Iterator<? extends CharSequence> valueCharSequenceIterator(CharSequence name) {
        return valueStringIterator(name);
    }

    /**
     * Checks to see if there is a header with the specified name
     *
     * @param name The name of the header to search for
     * @return True if at least one header is found
     */
    public boolean contains(CharSequence name) {
        return contains(name.toString());
    }

    /**
     * Checks if no header exists.
     */
    public abstract boolean isEmpty();

    /**
     * Returns the number of headers in this object.
     */
    public abstract int size();

    /**
     * Returns a new {@link Set} that contains the names of all headers in this object.  Note that modifying the
     * returned {@link Set} will not affect the state of this object.  If you intend to enumerate over the header
     * entries only, use {@link #iterator()} instead, which has much less overhead.
     */
    public abstract Set<String> names();

    /**
     * @see #add(CharSequence, Object)
     */
    public abstract AbstractSipHeaders add(String name, Object value);

    /**
     * Adds a new header with the specified name and value.
     * <p>
     * If the specified value is not a {@link String}, it is converted
     * into a {@link String} by {@link Object#toString()}, except in the cases
     * of {@link Date} and {@link Calendar}, which are formatted to the date
     * format defined in <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.3.1">RFC2616</a>.
     *
     * @param name  The name of the header being added
     * @param value The value of the header being added
     * @return {@code this}
     */
    public AbstractSipHeaders add(CharSequence name, Object value) {
        return add(name.toString(), value);
    }

    /**
     * @see #add(CharSequence, Iterable)
     */
    public abstract AbstractSipHeaders add(String name, Iterable<?> values);

    /**
     * Adds a new header with the specified name and values.
     * <p>
     * This getMethod can be represented approximately as the following code:
     * <pre>
     * for (Object v: values) {
     *     if (v == null) {
     *         break;
     *     }
     *     headers.add(name, v);
     * }
     * </pre>
     *
     * @param name   The name of the headers being set
     * @param values The values of the headers being set
     * @return {@code this}
     */
    public AbstractSipHeaders add(CharSequence name, Iterable<?> values) {
        return add(name.toString(), values);
    }

    /**
     * Adds all header entries of the specified {@code headers}.
     *
     * @return {@code this}
     */
    public AbstractSipHeaders add(AbstractSipHeaders headers) {
        ObjectUtil.checkNotNull(headers, "headers");
        for (Entry<String, String> e : headers) {
            add(e.getKey(), e.getValue());
        }
        return this;
    }

    /**
     * Add the {@code name} to {@code value}.
     *
     * @param name  The name to modify
     * @param value The value
     * @return {@code this}
     */
    public abstract AbstractSipHeaders addInt(CharSequence name, int value);

    /**
     * Add the {@code name} to {@code value}.
     *
     * @param name  The name to modify
     * @param value The value
     * @return {@code this}
     */
    public abstract AbstractSipHeaders addShort(CharSequence name, short value);

    /**
     * @see #set(CharSequence, Object)
     */
    public abstract AbstractSipHeaders set(String name, Object value);

    /**
     * Sets a header with the specified name and value.
     * <p>
     * If there is an existing header with the same name, it is removed.
     * If the specified value is not a {@link String}, it is converted into a
     * {@link String} by {@link Object#toString()}, except for {@link Date}
     * and {@link Calendar}, which are formatted to the date format defined in
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.3.1">RFC2616</a>.
     *
     * @param name  The name of the header being set
     * @param value The value of the header being set
     * @return {@code this}
     */
    public AbstractSipHeaders set(CharSequence name, Object value) {
        return set(name.toString(), value);
    }

    /**
     * @see #set(CharSequence, Iterable)
     */
    public abstract AbstractSipHeaders set(String name, Iterable<?> values);

    /**
     * Sets a header with the specified name and values.
     * <p>
     * If there is an existing header with the same name, it is removed.
     * This getMethod can be represented approximately as the following code:
     * <pre>
     * headers.remove(name);
     * for (Object v: values) {
     *     if (v == null) {
     *         break;
     *     }
     *     headers.add(name, v);
     * }
     * </pre>
     *
     * @param name   The name of the headers being set
     * @param values The values of the headers being set
     * @return {@code this}
     */
    public AbstractSipHeaders set(CharSequence name, Iterable<?> values) {
        return set(name.toString(), values);
    }

    /**
     * Cleans the current header entries and copies all header entries of the specified {@code headers}.
     *
     * @return {@code this}
     */
    public AbstractSipHeaders set(AbstractSipHeaders headers) {
        checkNotNull(headers, "headers");

        clear();

        if (headers.isEmpty()) {
            return this;
        }

        for (Entry<String, String> entry : headers) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Retains all current headers but calls {@link #set(String, Object)} for each entry in {@code headers}
     *
     * @param headers The headers used to {@link #set(String, Object)} values in this instance
     * @return {@code this}
     */
    public AbstractSipHeaders setAll(AbstractSipHeaders headers) {
        checkNotNull(headers, "headers");

        if (headers.isEmpty()) {
            return this;
        }

        for (Entry<String, String> entry : headers) {
            set(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Set the {@code name} to {@code value}. This will remove all previous values associated with {@code name}.
     *
     * @param name  The name to modify
     * @param value The value
     * @return {@code this}
     */
    public abstract AbstractSipHeaders setInt(CharSequence name, int value);

    /**
     * Set the {@code name} to {@code value}. This will remove all previous values associated with {@code name}.
     *
     * @param name  The name to modify
     * @param value The value
     * @return {@code this}
     */
    public abstract AbstractSipHeaders setShort(CharSequence name, short value);

    /**
     * @see #remove(CharSequence)
     */
    public abstract AbstractSipHeaders remove(String name);

    /**
     * Removes the header with the specified name.
     *
     * @param name The name of the header to remove
     * @return {@code this}
     */
    public AbstractSipHeaders remove(CharSequence name) {
        return remove(name.toString());
    }

    /**
     * Removes all headers from this {@link SipMessage}.
     *
     * @return {@code this}
     */
    public abstract AbstractSipHeaders clear();

    /**
     * @see #contains(CharSequence, CharSequence, boolean)
     */
    public boolean contains(String name, String value, boolean ignoreCase) {
        Iterator<String> valueIterator = valueStringIterator(name);
        if (ignoreCase) {
            while (valueIterator.hasNext()) {
                if (valueIterator.next().equalsIgnoreCase(value)) {
                    return true;
                }
            }
        } else {
            while (valueIterator.hasNext()) {
                if (valueIterator.next().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if a header with the {@code name} and {@code value} exists, {@code false} otherwise.
     * This also handles multiple values that are separated with a {@code ,}.
     * <p>
     * If {@code ignoreCase} is {@code true} then a case insensitive compare is done on the value.
     *
     * @param name       the name of the header to find
     * @param value      the value of the header to find
     * @param ignoreCase {@code true} then a case insensitive compare is run to compare values.
     *                   otherwise a case sensitive compare is run to compare values.
     */
    public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
        Iterator<? extends CharSequence> itr = valueCharSequenceIterator(name);
        while (itr.hasNext()) {
            if (containsCommaSeparatedTrimmed(itr.next(), value, ignoreCase)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsCommaSeparatedTrimmed(CharSequence rawNext, CharSequence expected,
                                                         boolean ignoreCase) {
        int begin = 0;
        int end;
        if (ignoreCase) {
            if ((end = AsciiString.indexOf(rawNext, ',', begin)) == -1) {
                return contentEqualsIgnoreCase(trim(rawNext), expected);
            } else {
                do {
                    if (contentEqualsIgnoreCase(trim(rawNext.subSequence(begin, end)), expected)) {
                        return true;
                    }
                    begin = end + 1;
                } while ((end = AsciiString.indexOf(rawNext, ',', begin)) != -1);

                if (begin < rawNext.length()) {
                    return contentEqualsIgnoreCase(trim(rawNext.subSequence(begin, rawNext.length())), expected);
                }
            }
        } else {
            if ((end = AsciiString.indexOf(rawNext, ',', begin)) == -1) {
                return contentEquals(trim(rawNext), expected);
            } else {
                do {
                    if (contentEquals(trim(rawNext.subSequence(begin, end)), expected)) {
                        return true;
                    }
                    begin = end + 1;
                } while ((end = AsciiString.indexOf(rawNext, ',', begin)) != -1);

                if (begin < rawNext.length()) {
                    return contentEquals(trim(rawNext.subSequence(begin, rawNext.length())), expected);
                }
            }
        }
        return false;
    }

    /**
     * {@link Headers#get(Object)} and convert the result to a {@link String}.
     *
     * @param name the name of the header to retrieve
     * @return the first header value if the header is found. {@code null} if there's no such header.
     */
    public final String getAsString(CharSequence name) {
        return get(name);
    }

    /**
     * {@link Headers#getAll(Object)} and convert each element of {@link List} to a {@link String}.
     *
     * @param name the name of the header to retrieve
     * @return a {@link List} of header values or an empty {@link List} if no values are found.
     */
    public final List<String> getAllAsString(CharSequence name) {
        return getAll(name);
    }

    /**
     * {@link Iterator} that converts each {@link Entry}'s key and value to a {@link String}.
     */
    public final Iterator<Entry<String, String>> iteratorAsString() {
        return iterator();
    }

    /**
     * Returns {@code true} if a header with the {@code name} and {@code value} exists, {@code false} otherwise.
     * <p>
     * If {@code ignoreCase} is {@code true} then a case insensitive compare is done on the value.
     *
     * @param name       the name of the header to find
     * @param value      the value of the header to find
     * @param ignoreCase {@code true} then a case insensitive compare is run to compare values.
     *                   otherwise a case sensitive compare is run to compare values.
     */
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        return contains(name.toString(), value.toString(), ignoreCase);
    }

    @Override
    public String toString() {
        return HeadersUtils.toString(getClass(), iteratorCharSequence(), size());
    }

    /**
     * Returns a deep copy of the passed in {@link AbstractSipHeaders}.
     */
    public AbstractSipHeaders copy() {
        return new DefaultSipHeaders().set(this);
    }
}

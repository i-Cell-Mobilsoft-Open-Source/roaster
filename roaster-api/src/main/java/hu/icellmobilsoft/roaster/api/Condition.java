/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.roaster.api;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.roaster.api.reflect.Getter;

/**
 * Condition checks for the test framework.
 *
 */
public class Condition {

    private Condition() {
    }

    /**
     * Check that the {@code} is equals with any ot the {@code static int} fields of the {@code ifClass}
     *
     * @param code
     *            the value to search for
     * @param ifClass
     *            the interface class
     * @param errorMessage
     *            the error message
     * @param <T>
     *            the type of the interface class
     */
    public static <T> void contains(int code, final Class<T> ifClass, final String errorMessage) {
        contains(code, ifClass, () -> errorMessage);
    }

    /**
     * Check that the {@code} is equals with any ot the {@code static int} fields of the {@code ifClass}
     *
     * @param code
     *            the value to search for
     * @param ifClass
     *            the interface class
     * @param errorMessageSupplier
     *            the error message supplier.
     * @param <T>
     *            the type of the interface class
     */
    public static <T> void contains(int code, final Class<T> ifClass, final Supplier<String> errorMessageSupplier) {
        notNull(ifClass, "ifClass should not be null");
        expected(ifClass.isInterface(), "ifClass should be an interface");
        List<Integer> valueList = Getter.getFieldsValue(ifClass, int.class);
        if (!valueList.contains(code)) {
            throw new PreconditionFailException(errorMessageSupplier.get());
        }
    }

    /**
     * Check that the {@code} is equals with any ot the {@code static String} fields of the {@code ifClass}
     *
     * @param code
     *            the value to search for
     * @param ifClass
     *            the interface class
     * @param errorMessage
     *            the error message.
     * @param <T>
     *            the type of the interface class
     */
    public static <T> void contains(final String code, final Class<T> ifClass, final String errorMessage) {
        contains(code, ifClass, () -> errorMessage);
    }

    /**
     * Check that the {@code} is equals with any ot the {@code static String} fields of the {@code ifClass}
     *
     * @param code
     *            the value to search for
     * @param ifClass
     *            the interface class
     * @param errorMessageSupplier
     *            the error message supplier.
     * @param <T>
     *            the type of the interface class
     */
    public static <T> void contains(final String code, final Class<T> ifClass, final Supplier<String> errorMessageSupplier) {
        notNull(ifClass, "ifClass should not be null");
        expected(ifClass.isInterface(), "ifClass should be an interface");
        List<String> valueList = Getter.getFieldsValue(ifClass, String.class);
        for (String value : valueList) {
            if (StringUtils.equals(code, value)) {
                return;
            }
        }
        throw new PreconditionFailException(errorMessageSupplier.get());
    }

    /**
     * Throws an PreconditionFailException with the message parameter if the object is null.
     *
     * @param <T>
     *            genetic type
     * @param object
     *            object to check conditon.
     * @param errorMessage
     *            the error message.
     * @return the checked object
     */
    public static <T> T notNull(T object, final String errorMessage) {
        return notNull(object, () -> errorMessage);
    }

    /**
     * Throws an PreconditionFailException with the error message supplied by the message supplier if the object is null.
     *
     * @param <T>
     *            genetic type
     * @param object
     *            object to check conditon.
     * @param errorMessageSupplier
     *            the error message supplier.
     * @return the checked object
     */
    public static <T> T notNull(T object, Supplier<String> errorMessageSupplier) {
        expected(object != null, errorMessageSupplier);
        return object;
    }

    /**
     * Throws an PreconditionFailException with the message parameter if any of the provided objects is null.
     *
     * @param <T>
     *            genetic type
     * @param objects
     *            variadic list of the objects to check conditon.
     * @param errorMessage
     *            the error message.
     */
    @SafeVarargs
    public static <T> void notNullAll(String errorMessage, @SuppressWarnings("unchecked") T... objects) {
        notNullAll(() -> errorMessage, objects);
    }

    /**
     * Throws an PreconditionFailException with the error message supplied by the message supplier if any of the provided objects is null.
     *
     * @param <T>
     *            genetic type
     * @param objects
     *            variadic list of the objects to check conditon.
     * @param errorMessageSupplier
     *            the error message supplier.
     */
    public static <T> void notNullAll(Supplier<String> errorMessageSupplier, @SuppressWarnings("unchecked") T... objects) {
        List<T> objectList = Arrays.asList(objects);
        notEmpty(objectList, "variadic argument list should not be empty");
        objectList.forEach(o -> expected(o != null, errorMessageSupplier));
    }

    /**
     * Throws an PreconditionFailException with the error message supplied by the message supplier if the list contains null element.
     *
     * @param <T>
     *            genetic type
     * @param list
     *            the list to check for null element.
     * @param errorMessage
     *            the error message supplier.
     * @return the input list
     */
    public static <T> List<T> notEmpty(List<T> list, String errorMessage) {
        return notEmpty(list, () -> errorMessage);
    }

    /**
     * Throws an PreconditionFailException with the message parameter if the list contains null element.
     *
     * @param <T>
     *            genetic type
     * @param list
     *            the list to check for null element.
     * @param errorMessageSupplier
     *            the error message supplier.
     * @return the input list
     */
    public static <T> List<T> notEmpty(List<T> list, Supplier<String> errorMessageSupplier) {
        expected(list != null && !list.isEmpty(), errorMessageSupplier);
        return list;
    }

    /**
     * Throws an PreconditionFailException with the error message supplied by the message supplier if the list contains null element.
     *
     * @param <T>
     *            genetic type
     * @param list
     *            the list to check for null element.
     * @param errorMessage
     *            the error message.
     * @return the input list
     */
    public static <T> T[] notEmpty(T[] list, String errorMessage) {
        return notEmpty(list, () -> errorMessage);
    }

    /**
     * Throws an PreconditionFailException with the message parameter if the list contains null element.
     *
     * @param <T>
     *            genetic type
     * @param list
     *            the list to check for null element.
     * @param errorMessageSupplier
     *            the error message supplier.
     * @return the input list
     */
    public static <T> T[] notEmpty(T[] list, Supplier<String> errorMessageSupplier) {
        expected(list != null && list.length > 0, errorMessageSupplier);
        return list;
    }

    /**
     * Throws an PreconditionFailException with the message parameter if the text is empty.
     *
     * @param text
     *            text to check emptiness.
     * @param errorMessage
     *            the error message.
     * @return the checked text
     */
    public static String notEmpty(String text, final String errorMessage) {
        return notEmpty(text, () -> errorMessage);
    }

    /**
     * Throws an PreconditionFailException with the error message supplied by the message supplier if the text is empty.
     *
     * @param text
     *            text to check emptiness.
     * @param errorMessageSupplier
     *            the error message supplier.
     * @return the checked text
     */
    public static String notEmpty(String text, Supplier<String> errorMessageSupplier) {
        expected(StringUtils.isNotEmpty(text), errorMessageSupplier);
        return text;
    }

    /**
     * Throws an PreconditionFailException with the message parameter if the text is blank.
     *
     * @param text
     *            text to check emptiness.
     * @param errorMessage
     *            the error message.
     * @return the checked text
     */
    public static String notBlank(String text, String errorMessage) {
        return notBlank(text, () -> errorMessage);
    }

    /**
     * Throws an PreconditionFailException with the error message supplied by the message supplier if the text is blank.
     *
     * @param text
     *            text to check emptiness.
     * @param errorMessageSupplier
     *            the error message supplier.
     * @return the checked text
     */
    public static String notBlank(String text, Supplier<String> errorMessageSupplier) {
        expected(StringUtils.isNotBlank(text), errorMessageSupplier);
        return text;
    }

    /**
     * Throws an PreconditionFailException with the message parameter if the condition is false.
     *
     * @param condition
     *            the conditon of the precondition chec
     * @param errorMessage
     *            the error message.
     */
    public static void expected(boolean condition, String errorMessage) {
        expected(condition, () -> errorMessage);
    }

    /**
     * Throws an {@code PreconditionFailException} with the message parameter if the condition is false.
     *
     * @param condition
     *            the conditon of the precondition chec
     * @param errorMessage
     *            the error message.
     * @param params
     *            variadic param list for the {@link MessageFormat#format(Object)}
     */
    public static void expected(boolean condition, String errorMessage, Object... params) {
        expected(condition, () -> MessageFormat.format(errorMessage, params));
    }

    /**
     * Throws an PreconditionFailException with the error message supplied by the message supplier if the condition is false.
     *
     * @param condition
     *            the condition of the precondition check
     * @param errorMessageSupplier
     *            the error message supplier
     */
    public static void expected(boolean condition, Supplier<String> errorMessageSupplier) {
        check(condition, errorMessageSupplier, PreconditionFailException::new);
    }

    /**
     * Throws an {@code PostconditionFailException} with the message parameter if the condition is false.
     *
     * @param condition
     *            the condition of the precondition check
     * @param errorMessage
     *            the error message.
     */
    public static void ensure(boolean condition, String errorMessage) {
        ensure(condition, () -> errorMessage);
    }

    /**
     * Throws an {@code PostconditionFailException} with the message parameter if the condition is false.
     *
     * @param condition
     *            the conditon of the precondition chec
     * @param errorMessage
     *            the error message.
     * @param params
     *            variadic param list for the {@link MessageFormat#format(Object)}
     */
    public static void ensure(boolean condition, String errorMessage, Object... params) {
        ensure(condition, () -> MessageFormat.format(errorMessage, params));
    }

    /**
     * Throws an {@code PostConditionFailException} with the error message supplied by the message supplier if the condition is false.
     *
     * @param condition
     *            the condition of the precondition check
     * @param errorMessageSupplier
     *            the error message supplier
     */
    public static void ensure(boolean condition, Supplier<String> errorMessageSupplier) {
        check(condition, errorMessageSupplier, PostconditionFailException::new);
    }

    /**
     * Condition check build method.
     *
     * @param condition
     *            the condition to check
     * @param errorMessageSupplier
     *            the error message supplier
     * @param builder
     *            method to build the concrete exception object with the message parameter
     */
    private static <E extends TestException> void check(boolean condition, Supplier<String> errorMessageSupplier, Function<String, E> builder) {
        if (!condition) {
            throw builder.apply(errorMessageSupplier.get());
        }
    }

    /**
     * Sometimes we must catch some exception in the test fw what should not be thrown in the test environment. This method can handle the cases if
     * the never imaged occur.
     *
     * @param cause
     *            the exception object
     */
    public static void shouldNeverThrown(Throwable cause) {
        shouldNeverThrown("Exception occurred what should never be thrown.", cause);
    }

    /**
     * This method is the same as {@link Condition#shouldNeverThrown(Throwable)}, only it has a string parameter to explain the context of the error.
     *
     * @param message
     *            the error message
     * @param cause
     *            the cause exception object
     */
    public static void shouldNeverThrown(final String message, Throwable cause) {
        throw new UnexpectedException(message, cause);
    }
}

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
package hu.icellmobilsoft.roaster.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.roaster.api.Any;
import hu.icellmobilsoft.roaster.api.Condition;

/**
 * Class to hold any XSD related Any method (like random request id).
 *
 */
public abstract class AnyDto {

    private static final int MAXIMUM_ARRAY_ELEMENT_NUMBER = 1000;

    /**
     * Alphabetic characters ({@literal a} - {@literal z})
     */
    public static final String ALPHABETIC_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Uppercase alphabetic characters ({@literal A} - {@literal Z})
     */
    public static final String UPPERCASE_ALPHABETIC_CHARACTERS = ALPHABETIC_CHARACTERS.toUpperCase();

    /**
     * Numeric characters ({@literal 0} - {@literal 9})
     */
    public static final String NUMERIC_CHARACTERS = "0123456789";

    private static final String ERR_MSG_COUNT_MUST_BE_GREATER_THAN_ONE = "count should be greater than zero!";

    private AnyDto() {
    }

    /**
     * Length of the request id for random generation
     */
    public static final int REQUEST_ID_TYPE_SIZE = 32;

    /**
     * Length of the session id for random generation
     */
    public static final int SESSION_ID_SIZE = 50;

    /**
     * @return random XSD valid request id.
     */
    public static String requestId() {
        return Any.string(REQUEST_ID_TYPE_SIZE);
    }

    /**
     * @return random XSD valid session id.
     */
    public static String sessionId() {
        return Any.string(SESSION_ID_SIZE);
    }

    /**
     * @param count
     *            the size of the random string
     * @param characters
     *            the set of the characters to to use for generation
     * @return new random string
     */
    public static String string(int count, String characters) {
        Condition.ensure(count > 0, ERR_MSG_COUNT_MUST_BE_GREATER_THAN_ONE);
        return RandomStringUtils.random(count, characters);
    }

    /**
     * @param minCountInclusive
     *            the minimum size of the string (inclusive)
     * @param maxCountExclusive
     *            the possible maximum size-1 of the string (exclusive)
     * @param characters
     *            the set of the characters to to use for generation
     * @return new random string
     */
    public static String string(int minCountInclusive, int maxCountExclusive, String characters) {
        Condition.ensure(minCountInclusive > 0, "minCountInclusive should not be negative!");
        Condition.ensure(maxCountExclusive > minCountInclusive, "maxCountExclusive should be greater than minCountInclusive!");
        return string(minCountInclusive, characters) + string(Any.aPositiveInt(maxCountExclusive - minCountInclusive), characters);
    }

    /**
     * @return a new xsd valid randdom zipcode
     */
    public static String zipCode() {
        return string(4, 11, UPPERCASE_ALPHABETIC_CHARACTERS + NUMERIC_CHARACTERS + StringUtils.SPACE + "-");
    }

    /**
     * @return a new xsd valid random country code
     */
    public static String countryCode() {
        return alphabetic(3).toUpperCase();
    }

    /**
     * @param count
     *            the size of the new string
     * @return new random string that contains only alphabetic characters.
     */
    public static String alphabetic(int count) {
        Condition.ensure(count > 0, ERR_MSG_COUNT_MUST_BE_GREATER_THAN_ONE);
        return RandomStringUtils.randomAlphabetic(count);
    }

    /**
     * @param minCountInclusive
     *            the minimum size of the string (inclusive)
     * @param maxCountExclusive
     *            the possible maximum size-1 of the string (exclusive)
     * @return new random string that contains alphabetic and numeric characters
     */
    public static String alphaNumeric(int minCountInclusive, int maxCountExclusive) {
        Condition.ensure(minCountInclusive > -1, "minCountInclusive should not be negative!");
        Condition.ensure(maxCountExclusive >= minCountInclusive, "maxCountExclusive should be equal or greater than minCountInclusive!");
        return RandomStringUtils.randomAlphanumeric(minCountInclusive)
                + RandomStringUtils.randomAlphanumeric(ThreadLocalRandom.current().nextInt(maxCountExclusive - minCountInclusive));
    }

    /**
     * @param count
     *            the size of the new string
     * @return new random string that contains alphabetic and numeric characters
     */
    public static String alphaNumeric(int count) {
        Condition.ensure(count > 0, ERR_MSG_COUNT_MUST_BE_GREATER_THAN_ONE);
        return RandomStringUtils.randomAlphanumeric(count);
    }

    /**
     * build an array with maximum {@value #MAXIMUM_ARRAY_ELEMENT_NUMBER} element
     *
     * @param <T>
     *            generic type
     * @param clazz
     *            the base type of the elements
     * @return an newly created list.
     */
    public static <T> List<T> array(Class<T> clazz) {
        return array(clazz, MAXIMUM_ARRAY_ELEMENT_NUMBER);
    }

    /**
     * Build a distinct array with maximum {@code maxSize} element.
     *
     * @param clazz
     *            the base type of the elements
     * @param predicate
     *            function that returns the value to sort the items
     * @param maxSize
     *            the maximum number of the elements in the result list
     * @param <T>
     *            the type of the list elements
     * @param <R>
     *            type to filter the data (String, etc.)
     * @return unique list of the elements
     */
    public static <T, R> List<T> array(Class<T> clazz, Function<? super T, ? extends R> predicate, int maxSize) {
        return new ArrayList<>(array(clazz, maxSize).stream().collect(Collectors.toMap(predicate, i -> i, (lhs, rhs) -> lhs)).values());
    }

    /**
     * build a distinct array with maximum {@code #maxSize} element.
     * <p>
     * The last parameter an method the provide the uniques of an element.
     *
     * @param clazz
     *            the base type of the elements
     * @param predicate
     *            function that returns the value to sort the items
     * @param <T>
     *            the type of the list elements
     * @param <R>
     *            type to filter the data (String, etc.)
     * @return unique list of the elements
     */
    public static <T, R> List<T> array(Class<T> clazz, Function<? super T, ? extends R> predicate) {
        return array(clazz, predicate, MAXIMUM_ARRAY_ELEMENT_NUMBER);
    }

    /**
     * Build an random list of elements. The maximum size of the list is {@code maxSize}
     *
     * @param clazz
     *            the class of the element type
     * @param maxSize
     *            the number of the elements
     * @param <T>
     *            the type of the list elements
     * @return new random array with the max length {@code maxSize}
     */
    public static <T> List<T> array(Class<T> clazz, int maxSize) {
        Condition.notNull(clazz, "Element class type should not be null.");
        Condition.expected(maxSize > 0, "Maximum size of the array should be least one.");

        List<T> result = new ArrayList<>();
        if (clazz.isEnum()) {
            Any.fillToSize(result, clazz, Any::enumItem, maxSize);
        } else if (String.class.isAssignableFrom(clazz)) {
            Any.fillToSize(result, clazz, t -> (T) Any.string(), maxSize);
        } else if (Builder.has(clazz)) {
            IBaseBuilder<T> builder = Builder.get(clazz);
            Any.fillToSize(result, builder::build, maxSize);
        } else if (clazz.getAnnotation(XmlType.class) != null) {
            IBaseBuilder<T> builder = GenericTypeBuilder.create(clazz, Any.createEntity(clazz));
            Any.fillToSize(result, builder::build, maxSize);
        } else {
            Any.fillToSize(result, Any.createEntity(clazz), maxSize);
        }

        return result;
    }

    /**
     * generate a random list that contains random elements.
     *
     * @param clazz
     *            the class definition of the object
     * @param <T>
     *            the type of the object
     * @return a new list with random elements
     */
    public static <T> List<T> list(Class<T> clazz) {
        return list(clazz, MAXIMUM_ARRAY_ELEMENT_NUMBER);
    }

    /**
     * generate a random list that contains random elements.
     *
     * @param clazz
     *            the class definition of the object
     * @param size
     *            the size of the list
     * @param <T>
     *            the type of the object
     * @return a new list with random elements
     */
    public static <T> List<T> list(Class<T> clazz, int size) {
        if (clazz.isEnum()) {
            return list(clazz, () -> Any.enumItem(clazz), size);
        } else if (String.class.isAssignableFrom(clazz)) {
            return list(clazz, () -> (T) Any.string(), size);
        } else if (Builder.has(clazz)) {
            IBaseBuilder<T> builder = Builder.get(clazz);
            return list(clazz, builder::build, size);
        } else if (clazz.getAnnotation(XmlType.class) != null) {
            IBaseBuilder<T> builder = GenericTypeBuilder.create(clazz, Any.createEntity(clazz));
            return list(clazz, builder::build, size);
        }

        return list(clazz, Any.createEntity(clazz), size);
    }

    /**
     * generate a random list that contains random elements supplied by {@code supplier}.
     *
     * @param clazz
     *            the class definition of the object
     * @param supplier
     *            the object supplier
     * @param <T>
     *            the type of the object
     * @return a new list with random elements
     */
    public static <T> List<T> list(Class<T> clazz, Supplier<T> supplier) {
        return list(clazz, supplier, MAXIMUM_ARRAY_ELEMENT_NUMBER);
    }

    /**
     * generate a random list that contains random elements supplied by {@code supplier}.
     *
     * @param clazz
     *            the class definition of the object
     * @param supplier
     *            the object supplier
     * @param size
     *            the size of the list
     * @param <T>
     *            the type of the object
     * @return a new list with random elements
     */
    public static <T> List<T> list(Class<T> clazz, Supplier<T> supplier, int size) {
        Condition.notNull(clazz, "Element class type should not be null.");
        Condition.notNull(supplier, "Element supplier should not be null.");
        Condition.expected(size > 0, "Size of the list should be least one.");

        List<T> result = new ArrayList<>();
        Any.fillToSize(result, clazz, t -> supplier.get(), size);
        return result;
    }
}

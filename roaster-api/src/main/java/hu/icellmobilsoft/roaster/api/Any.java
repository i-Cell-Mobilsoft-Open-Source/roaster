/*-
 * #%L
 * Roaster
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;

import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateXmlUtil;
import hu.icellmobilsoft.roaster.api.reflect.Getter;

/**
 * Collector class to hold all generic method to generate entity field values and test data.
 *
 */
public abstract class Any {

    private static final int DEFAULT_MIN_STR_LENGTH = 1;
    private static final int DEFAULT_MAX_STR_LENGTH = 32;

    private static final String ERROR_MSG_ENUMERATION_TYPE_NULL = "The input enumeration type should not be null.";

    private static final String ERROR_MSG_ENUMERATION_TYPE_EMPTY = "The enumeration type should contain at least one element.";

    private static final String ERROR_MSG_MAX_SIZE_AT_LEAST_ONE = "Maximum size of the array should be least one.";

    /**
     * Enum for the random string character sets
     */
    public enum RandomStringType {
        /**
         * Characters will be chosen from the set of all characters.
         */
        RANDOM,
        /**
         * Characters will be chosen from the set of characters whose ASCII value is between 32 and 126 (inclusive).
         */
        ASCII,
        /**
         * Characters will be chosen from the set of Latin alphabetic characters (a-z, A-Z).
         */
        ONLY_ALPHABETIC,
        /**
         * Characters will be chosen from the set of Latin alphabetic characters (a-z, A-Z) and the digits 0-9.
         */
        ONLY_ALPHANUMERIC,
        /**
         * Characters will be chosen from the set of numeric characters.
         */
        ONLY_NUMERIC
    }

    private Any() {
    }

    /**
     * generate a random X__ID using the default EntityIdGenerator.
     *
     * @return a random X__ID
     */
    public static String xId() {
        return RandomUtil.generateId();
    }

    /**
     * Generate a random string that contains only whitespaces. The length will be between {@value DEFAULT_MIN_STR_LENGTH} and
     * {@value DEFAULT_MAX_STR_LENGTH}
     *
     * @return a random only whitespace string
     */
    public static String whitespace() {
        return whitespace(DEFAULT_MIN_STR_LENGTH, DEFAULT_MAX_STR_LENGTH);
    }

    /**
     * Generate random string what is null, empty or contains only whitespaces.
     *
     * @return the random string
     */
    public static String emptyString() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return (rand.nextBoolean()) ? whitespace(0, DEFAULT_MAX_STR_LENGTH) : null;
    }

    /**
     * Generate random email address
     *
     * @return the random email address
     */
    public static String emailAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(string(30, false, RandomStringType.ONLY_ALPHANUMERIC));
        sb.append("@");
        sb.append(string(10, false, RandomStringType.ONLY_ALPHANUMERIC));
        sb.append(".");
        sb.append(string(3, false, RandomStringType.ONLY_ALPHANUMERIC));
        return sb.toString();
    }

    /**
     * Generate an random string that contains only whitespaces. The length will be between {@code min} and {@code max}
     *
     * @param min
     *            minimum number of characters
     * @param max
     *            maximum number of characters
     * @return an random only whitespace string
     */
    private static String whitespace(int min, int max) {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder().filteredBy(Character::isWhitespace).build();
        return randomStringGenerator.generate(min, max);
    }

    /**
     * Generate an random boolean
     *
     * @return random true or false
     */
    public static boolean bool() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return rand.nextBoolean();
    }

    /**
     * Generate an random String
     *
     * @return a fully random UTF-8 encoded string with length between {@value #DEFAULT_MIN_STR_LENGTH} and {@value #DEFAULT_MAX_STR_LENGTH}
     */
    public static String string() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder().build();
        return randomStringGenerator.generate(DEFAULT_MIN_STR_LENGTH, DEFAULT_MAX_STR_LENGTH);
    }

    /**
     * Generate a printable string with the maximum of {@code count} characters.
     *
     * @param count
     *            the max number of characters in the result string;
     * @return a printable random string
     */
    public static String string(int count) {
        return string(count, false);
    }

    /**
     * Generate a printable string with the maximum of {@code count} characters containing only {@link RandomStringType} characters
     *
     * @param count
     *            the max number of characters in the result string;
     * @param randomStringType
     *            random string type
     * @return a printable random string
     */
    public static String string(int count, RandomStringType randomStringType) {
        return string(count, false, randomStringType);
    }

    /**
     * Generate a printable string with the maximum of {@code count} characters.
     *
     * @param count
     *            the max number of characters in the result string;
     * @param nonBlank
     *            should not begin and end with a whitespace character
     * @return a printable random string
     */
    public static String string(int count, boolean nonBlank) {
        return string(count, nonBlank, RandomStringType.ASCII);
    }

    /**
     * Generate a printable string with the maximum of {@code count} characters containing only {@link RandomStringType} characters
     *
     * @param count
     *            the max number of characters in the result string;
     * @param nonBlank
     *            should not begin and end with a whitespace character
     * @param randomStringType
     *            random string type
     * @return a printable random string
     */
    public static String string(int count, boolean nonBlank, RandomStringType randomStringType) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int randCount = rand.nextInt(count - 1) + 1;
        String result;
        switch (randomStringType) {
        case ASCII:
            result = RandomStringUtils.randomAscii(randCount);
            break;
        case ONLY_ALPHABETIC:
            result = RandomStringUtils.randomAlphabetic(randCount);
            break;
        case ONLY_ALPHANUMERIC:
            result = RandomStringUtils.randomAlphanumeric(randCount);
            break;
        case ONLY_NUMERIC:
            result = RandomStringUtils.randomNumeric(randCount);
            break;
        default:
            result = RandomStringUtils.random(randCount);
        }
        if (nonBlank) {
            if (Character.isWhitespace(result.charAt(0))) {
                result = RandomStringUtils.randomAscii(1) + result.substring(1);
            }
            if (Character.isWhitespace(result.charAt(result.length() - 1))) {
                result = result.substring(0, result.length() - 1) + RandomStringUtils.randomAscii(1);
            }
        }
        return result;
    }

    /**
     * Generate an random Date
     *
     * @return an random date after 1970-01-01T00:00:00Z
     */
    public static Date time() {
        return time(0, Instant.ofEpochSecond(Integer.MAX_VALUE).atOffset(ZoneOffset.UTC).toEpochSecond(), 0, LocalDateTime.MAX.getNano());
    }

    /**
     * Generate an random Date before input date
     *
     * @param time
     *            bound time
     * @return a new time point before the input time
     */
    public static Date timeBefore(Date time) {
        Condition.notNull(time, "time should not be null.");
        Instant limit = time.toInstant();
        return time(0, limit.getEpochSecond(), 0, limit.getNano());
    }

    /**
     * Generate an random Date after input date
     *
     * @param time
     *            origin time
     * @return a new time point after the input time
     */
    public static Date timeAfter(Date time) {
        Condition.notNull(time, "time should not be null.");
        Instant limit = time.toInstant();
        return time(limit.getEpochSecond(), Instant.ofEpochSecond(Integer.MAX_VALUE).atOffset(ZoneOffset.UTC).toEpochSecond(), limit.getNano(),
                LocalDateTime.MAX.getNano());
    }

    /**
     * Generate an random Date between input dates
     *
     * @param origin
     *            origin time
     * @param bound
     *            bound time
     * @return an Date object between origin and bound
     */
    public static Date time(Date origin, Date bound) {
        Condition.notNull(origin, "origin should not be null.");
        Condition.notNull(bound, "origin should not be null.");
        Instant lhs = origin.toInstant();
        Instant rhs = bound.toInstant();
        return time(lhs.getEpochSecond(), rhs.getEpochSecond(), lhs.getNano(), rhs.getNano());
    }

    /**
     * Generate an random Date in the given boundaries
     *
     * @param originEpoch
     *            the origin epoch seconds
     * @param boundEpoch
     *            the bound epoch seconds
     * @param originNano
     *            the origin nano
     * @param boundNano
     *            the bound nano
     * @return a random time in the given boundaries
     */
    private static Date time(long originEpoch, long boundEpoch, int originNano, int boundNano) {
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        LocalDateTime time = LocalDateTime.ofEpochSecond(Any.aLong(originEpoch, boundEpoch), Any.anInt(originNano, boundNano), offset);
        return Date.from(time.toInstant(offset));
    }

    /**
     * Generate an random XMLGregorianCalendar
     *
     * @return random {@code XMLGregorianCalendar}
     */
    public static XMLGregorianCalendar timestamp() {
        return DateXmlUtil.toXMLGregorianCalendar(Any.time());
    }

    /**
     * Generate an random XMLGregorianCalendar before input date
     *
     * @param bound
     *            the bound date
     * @return a timestamp before bound
     */
    public static XMLGregorianCalendar timestampBefore(Date bound) {
        return DateXmlUtil.toXMLGregorianCalendar(Any.timeBefore(bound));
    }

    /**
     * Generate an random XMLGregorianCalendar after input date
     *
     * @param origin
     *            the origin date
     * @return an timestamp after origin
     */
    public static XMLGregorianCalendar timestampAfter(Date origin) {
        return DateXmlUtil.toXMLGregorianCalendar(Any.timeAfter(origin));
    }

    /**
     * Generate an random XMLGregorianCalendar between input dates
     *
     * @param origin
     *            the origin date
     * @param bound
     *            the bound date
     * @return a timestamp between origin and bound
     */
    public static XMLGregorianCalendar timestamp(Date origin, Date bound) {
        return DateXmlUtil.toXMLGregorianCalendar(Any.time(origin, bound));
    }

    /**
     * Pick random item from input
     *
     * @param <T>
     *            generic type
     * @param items
     *            variadic list of the possible items
     * @return one of the item from the input
     */
    @SafeVarargs
    public static <T> T of(T... items) {
        Condition.notNullAll("Variadic item list should not have any null element.", items);
        List<T> itemList = Arrays.asList(items);
        return of(itemList);
    }

    /**
     * Pick random item from input
     *
     * @param itemList
     *            the list of the possible items
     * @param <T>
     *            the type of possible item
     * @return one random item from the {@code itemList}
     */
    public static <T> T of(List<T> itemList) {
        Condition.notEmpty(itemList, "should provide at least one possible item.");
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return itemList.get(rand.nextInt(itemList.size()));
    }

    /**
     * Generate an random integer
     *
     * @return an random integer
     */
    public static int anInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    /**
     * Generate an random integer between inputs
     *
     * @param origin
     *            the origin
     * @param bound
     *            the bound
     * @return an random integer between {@code origin} (inclusive) and {@code bound} (exclusive)
     */
    public static int anInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    /**
     * Generate an random positive integer
     *
     * @return a random positive integer
     */
    public static int aPositiveInt() {
        return nextNotZero(() -> ThreadLocalRandom.current().nextInt());
    }

    /**
     * Generate an random positive integer less than input
     *
     * @param bound
     *            the upper bound (exclusive)
     * @return a random positive integer that is less than {@code bound}
     */
    public static int aPositiveInt(int bound) {
        Condition.expected(bound > 0, "Origin must me be greater than zero!");
        return nextNotZero(() -> ThreadLocalRandom.current().nextInt(bound));
    }

    /**
     * Generate an random positive integer between inputs
     *
     * @param origin
     *            the least value returned
     * @param bound
     *            the upper bound (exclusive)
     * @return a random positive integer that is between {@code origin} {@code bound}
     */
    public static int aPositiveInt(int origin, int bound) {
        Condition.expected(origin > 0, "Origin must me be greater than zero!");
        Condition.expected(bound > origin, "Bound must me be greater than origin!");
        return nextNotZero(() -> ThreadLocalRandom.current().nextInt(origin, bound));
    }

    private static int nextNotZero(IntSupplier nextSupplier) {
        int result;
        do {
            result = nextSupplier.getAsInt();
        } while (result == 0);
        return result;
    }

    /**
     * Generate an random boolean
     *
     * @return true ot false randomly
     */
    public static boolean aBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * Generate an random byte
     *
     * @return a random byte
     */
    public static byte aByte() {
        return (byte) ThreadLocalRandom.current().nextInt(Byte.MAX_VALUE + 1);
    }

    /**
     * Generate an random byte between inputs
     *
     * @param origin
     *            the origin
     * @param bound
     *            the bound
     * @return an random byte between {@code origin} (inclusive) and {@code bound} (exclusive)
     */
    public static byte aByte(byte origin, byte bound) {
        return (byte) ThreadLocalRandom.current().nextInt(origin, bound);
    }

    /**
     * Generate an random short
     *
     * @return a random short
     */
    public static short aShort() {
        return (short) ThreadLocalRandom.current().nextInt(Short.MAX_VALUE + 1);
    }

    /**
     * Generate an random short between inputs
     *
     * @param origin
     *            the origin
     * @param bound
     *            the bound
     * @return an random short between {@code origin} (inclusive) and {@code bound} (exclusive)
     */
    public static short aShort(short origin, short bound) {
        return (short) ThreadLocalRandom.current().nextInt(origin, bound);
    }

    /**
     * Generate an random long
     *
     * @return a random long
     */
    public static long aLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    /**
     * Generate an random long between inputs
     *
     * @param origin
     *            the origin
     * @param bound
     *            the bound
     * @return an random long between {@code origin} (inclusive) and {@code bound} (exclusive)
     */
    public static long aLong(long origin, long bound) {
        return ThreadLocalRandom.current().nextLong(origin, bound);
    }

    /**
     * Generate an random float
     *
     * @return a random float
     */
    public static float aFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    /**
     * Generate an random double
     *
     * @return a random double
     */
    public static double aDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * Generate an random double between inputs
     *
     * @param origin
     *            the origin
     * @param bound
     *            the bound
     * @return an random double between {@code origin} (inclusive) and {@code bound} (exclusive)
     */
    public static double aDouble(double origin, double bound) {
        return ThreadLocalRandom.current().nextDouble(origin, bound);
    }

    /**
     * Pick random Enum item from input
     *
     * @param <T>
     *            generic type
     * @param enums
     *            variadic list of the enum values from an given enum type.
     * @return one random chosen enum object from the list.
     */
    public static <T extends Enum<T>> T object(@SuppressWarnings("unchecked") T... enums) {
        List<T> items = Arrays.asList(enums);
        return object(items);
    }

    /**
     * Pick random Enum item from input
     *
     * @param items
     *            the enumeration value list
     * @param <T>
     *            the type of the enumeration
     * @return one random chosen enum object from the list.
     */
    public static <T extends Enum<T>> T object(List<T> items) {
        Condition.notEmpty(items, "should provide at least one possible item.");
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return items.get(rand.nextInt(items.size()));
    }

    /**
     * Pick random Enum item from input
     *
     * @param enumClass
     *            the class of the enumeration
     * @param <T>
     *            the type of the enumeration
     * @return one random chosen enum object from all of the possible items.
     */
    public static <T extends Enum<T>> T of(Class<T> enumClass) {
        Condition.notNull(enumClass, ERROR_MSG_ENUMERATION_TYPE_NULL);
        T[] items = enumClass.getEnumConstants();
        Condition.notEmpty(items, ERROR_MSG_ENUMERATION_TYPE_EMPTY);
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return items[rand.nextInt(items.length)];
    }

    /**
     * More generic way to handle enumerations. The T type must not be implement the Enum interface, but the {@code Class<T>} must have enum nature
     * ({@code enumClass.isEnum() == true}). This way is possible more flexible work with reflection.
     *
     * @param enumClass
     *            the class of the enumeration
     * @param <T>
     *            the type of the enumeration
     * @return one random chosen enum object from all of the possible items.
     */
    public static <T> T enumItem(Class<T> enumClass) {
        Condition.notNull(enumClass, ERROR_MSG_ENUMERATION_TYPE_NULL);
        Condition.expected(enumClass.isEnum(), "The input type should be an enumeration.");
        T[] items = enumClass.getEnumConstants();
        Condition.notEmpty(items, ERROR_MSG_ENUMERATION_TYPE_EMPTY);
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return items[rand.nextInt(items.length)];
    }

    /**
     * Returns the value of a random static field with a given type from the given class
     *
     * @param ifaceClass
     *            the interface class
     * @param fieldType
     *            the type of the fields to use
     * @return the value of a random static field with a given type from the given class
     * @param <I>
     *            the type of the interface
     * @param <T>
     *            the type of the field
     */
    public static <I, T> T fieldValue(Class<I> ifaceClass, Class<T> fieldType) {
        Condition.notNull(ifaceClass, "IfaceClass should not be null.");
        Condition.expected(ifaceClass.isInterface(), "the input type must be an interface.");
        List<T> items = Getter.getFieldsValue(ifaceClass, fieldType);
        Condition.notEmpty(items, "Field value list should not be empty.");
        return items.get(ThreadLocalRandom.current().nextInt(items.size()));
    }

    /**
     * Exclude enums from input and return in new object
     *
     * @param enumClass
     *            the class of the enumeration
     * @param excludeItems
     *            the list of elements to exclude
     * @param <T>
     *            the type of the enumeration
     * @return the enum object list of T without the excluded elements.
     */
    public static <T extends Enum<T>> T[] exclude(Class<T> enumClass, @SuppressWarnings("unchecked") T... excludeItems) {
        Condition.notNull(enumClass, ERROR_MSG_ENUMERATION_TYPE_NULL);
        List<T> items = new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
        Arrays.stream(excludeItems).forEach(items::remove);
        Condition.notEmpty(items, ERROR_MSG_ENUMERATION_TYPE_EMPTY);
        @SuppressWarnings("unchecked")
        T[] result = (T[]) java.lang.reflect.Array.newInstance(items.get(0).getClass(), items.size());
        for (int i = 0; i < items.size(); i++) {
            result[i] = items.get(i);
        }
        return result;
    }

    /**
     * Random value what is not equals with any of the static field
     *
     * @param ifaceClass
     *            the interface class
     * @param fieldType
     *            the type of the fields to use
     * @param randomSupplier
     *            the random value supplier to generate new random value
     * @param <T>
     *            the type of the field
     * @param <I>
     *            the type of the interface
     * @return a random value what is not equals with any of the static field of {@code ifaceClass} with type {@code fieldType}
     */
    public static <T, I> T butValue(Class<I> ifaceClass, Class<T> fieldType, Supplier<T> randomSupplier) {
        Condition.notNull(ifaceClass, "IfaceClass should not be null.");
        Condition.expected(ifaceClass.isInterface(), "the input type must be an interface.");
        List<T> items = Getter.getFieldsValue(ifaceClass, fieldType);
        Condition.notEmpty(items, "Field value list should not be empty.");
        T result = randomSupplier.get();
        while (items.contains(result)) {
            result = randomSupplier.get();
        }
        return result;
    }

    /**
     * Random integer that is not equals with any of the static fields
     *
     * @param ifaceClass
     *            the interface class
     * @param <I>
     *            the type of the interface class
     * @return a random integer that is not equals with any of the static fields of the {@code ifaceClass}
     */
    public static <I> int butInt(Class<I> ifaceClass) {
        return butValue(ifaceClass, int.class, () -> ThreadLocalRandom.current().nextInt());
    }

    /**
     * Generate random String like input
     *
     * @param text
     *            the string object
     * @return any text but the input param
     */
    public static String but(String text) {
        return but(text, () -> Any.string(text.length()));
    }

    /**
     * Generate random String like input
     *
     * @param text
     *            the string object
     * @param randomSupplier
     *            the supplier of the random string
     * @return any text but the input param
     */
    public static String but(String text, Supplier<String> randomSupplier) {
        Condition.notEmpty(text, "The input text should not be empty.");
        String result = randomSupplier.get();
        while (StringUtils.equals(result, text)) {
            result = randomSupplier.get();
        }
        return result;
    }

    /**
     * Create an array with random content.
     *
     * @param size
     *            the number of the elements
     * @return a boolean array with {@code size}
     */
    public static boolean[] booleanArray(int size) {
        Condition.expected(size > 0, ERROR_MSG_MAX_SIZE_AT_LEAST_ONE);
        boolean[] result = new boolean[size];
        for (int i = 0; i < size; ++i) {
            result[i] = aBoolean();
        }
        return result;
    }

    /**
     * Create an array with random content.
     *
     * @param size
     *            the number of the elements
     * @return a byte array with {@code size}
     */
    public static byte[] byteArray(int size) {
        Condition.expected(size > 0, ERROR_MSG_MAX_SIZE_AT_LEAST_ONE);
        byte[] result = new byte[size];
        for (int i = 0; i < size; ++i) {
            result[i] = aByte();
        }
        return result;
    }

    /**
     * Create an array with random content.
     *
     * @param size
     *            the number of the elements
     * @return a short array with {@code size}
     */
    public static short[] shortArray(int size) {
        Condition.expected(size > 0, ERROR_MSG_MAX_SIZE_AT_LEAST_ONE);
        short[] result = new short[size];
        for (int i = 0; i < size; ++i) {
            result[i] = aShort();
        }
        return result;
    }

    /**
     * Create an array with random content.
     *
     * @param size
     *            the number of the elements
     * @return a int array with {@code size}
     */
    public static int[] intArray(int size) {
        Condition.expected(size > 0, ERROR_MSG_MAX_SIZE_AT_LEAST_ONE);
        int[] result = new int[size];
        for (int i = 0; i < size; ++i) {
            result[i] = anInt();
        }
        return result;
    }

    /**
     * Create an array with random content.
     *
     * @param size
     *            the number of the elements
     * @return a long array with {@code size}
     */
    public static long[] longArray(int size) {
        Condition.expected(size > 0, ERROR_MSG_MAX_SIZE_AT_LEAST_ONE);
        long[] result = new long[size];
        for (int i = 0; i < size; ++i) {
            result[i] = aLong();
        }
        return result;
    }

    /**
     * Create an array with random content.
     *
     * @param size
     *            the number of the elements
     * @return a float array with {@code size}
     */
    public static float[] floatArray(int size) {
        Condition.expected(size > 0, ERROR_MSG_MAX_SIZE_AT_LEAST_ONE);
        float[] result = new float[size];
        for (int i = 0; i < size; ++i) {
            result[i] = aFloat();
        }
        return result;
    }

    /**
     * Create an array with random content.
     *
     * @param size
     *            the number of the elements
     * @return a double array with {@code size}
     */
    public static double[] doubleArray(int size) {
        Condition.expected(size > 0, ERROR_MSG_MAX_SIZE_AT_LEAST_ONE);
        double[] result = new double[size];
        for (int i = 0; i < size; ++i) {
            result[i] = aDouble();
        }
        return result;
    }

    /**
     * Add to the {@code list} new elements using the {@code randomSupplier} method.
     *
     * @param list
     *            the target list
     * @param itemType
     *            the class of the list elements
     * @param randomSupplier
     *            a supplier that provide a pseudo random new element (has one parameter the {@code itemType})
     * @param size
     *            the desired number of element to add to the list
     * @param <T>
     *            the type of the list elements
     */
    public static <T> void fillToSize(List<T> list, Class<T> itemType, Function<Class<T>, T> randomSupplier, int size) {
        for (int i = 0; i < size; ++i) {
            list.add(randomSupplier.apply(itemType));
        }
    }

    /**
     * Add to the {@code list} new elements using the {@code randomSupplier} method.
     *
     * @param list
     *            the target list
     * @param randomSupplier
     *            a supplier that provide a pseudo random new element
     * @param size
     *            the desired number of element to add to the list
     * @param <T>
     *            the type of the list elements
     */
    public static <T> void fillToSize(List<T> list, Supplier<T> randomSupplier, int size) {
        for (int i = 0; i < size; ++i) {
            list.add(randomSupplier.get());
        }
    }

    /**
     * It will call the {@code newInstance} method through the {@code targetClass} and create a new object. If something goes wrong, it will throw an
     * {@code UnexpectedException}.
     *
     * @param targetClass
     *            the class of the target type
     * @param <C>
     *            the target type
     * @return a new element
     */
    // TODO: move seperated class
    public static <C> Supplier<C> createEntity(final Class<C> targetClass) {
        return () -> {
            C instance = null;
            try {
                instance = targetClass.getDeclaredConstructor().newInstance();
            } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
                Condition.shouldNeverThrown("Failed to create the target class", e);
            }
            return instance;
        };
    }
}

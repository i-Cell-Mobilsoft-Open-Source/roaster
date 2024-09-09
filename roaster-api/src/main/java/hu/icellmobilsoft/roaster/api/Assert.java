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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionConsumer;

/**
 * Class to extend the interface of {@link Assertions} class.
 *
 */
public final class Assert {

    private static final String ERROR_MSG_CONSUMER_NULL = "Consumer should not be null.";

    private Assert() {
    }

    /**
     * Aggregated assertion to assert the BaseException type of the company
     *
     * @param <T>
     *            BaseException type
     * @param <E>
     *            Enum type
     * @param expectedType
     *            the type of the exception
     * @param faultCode
     *            the business error code
     * @param errorMsg
     *            the error message
     * @param executable
     *            the target of the assertion
     */
    public static <T extends BaseException, E extends Enum<?>> void assertBaseException(final Class<T> expectedType, final E faultCode,
            final String errorMsg, final Executable executable) {
        Condition.notNullAll("expectedType, faultCode and executable should not be null.", expectedType, faultCode, executable);
        Condition.notBlank(errorMsg, "Error message should not be blank.");
        T exception = Assertions.assertThrows(expectedType, executable);
        Assertions.assertAll(
                () -> Assertions.assertEquals(faultCode, exception.getFaultTypeEnum(), "exception is thrown but the fault code is wrong"),
                () -> Assertions.assertEquals(errorMsg, exception.getMessage(), "exception is thrown but the cause is wrong"));
    }

    /**
     * Aggregated assertion to assert the BaseException type of the company
     *
     * @param <T>
     *            BaseException type
     * @param <E>
     *            Enum type
     * @param expectedType
     *            the type of the exception
     * @param faultCode
     *            the business error code
     * @param executable
     *            the target of the assertion
     */
    public static <T extends BaseException, E extends Enum<?>> void assertBaseException(final Class<T> expectedType, final E faultCode,
            final Executable executable) {
        Condition.notNullAll("expectedType, faultCode and executable should not be null.", expectedType, faultCode, executable);
        T exception = Assertions.assertThrows(expectedType, executable);
        Assertions.assertEquals(faultCode, exception.getFaultTypeEnum(), "exception is thrown but the fault code is wrong");
    }

    /**
     * Assert to check invalid incoming parameter
     *
     * @param errorMessage
     *            the expected error message
     * @param executable
     *            the target of the assertion
     */
    public static void assertInvalidParameter(final String errorMessage, final Executable executable) {
        Condition.notBlank(errorMessage, "Error message should not be blank.");
        Condition.notNull(executable, "Executable should not be null.");
        Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, errorMessage, executable);
    }

    /**
     * Assert to check invalid incoming parameter
     *
     * @param executable
     *            the target of the assertion
     */
    public static void assertInvalidParameter(final Executable executable) {
        Condition.notNull(executable, "Executable should not be null.");
        Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, executable);
    }

    /**
     * @param errorMessages
     *            list of the error messages
     * @param index
     *            the index of the error message
     * @return the errorMessages.get(index) or errorMessages.get(0) if the index is too large
     */
    private static String getErrorMessage(List<String> errorMessages, int index) {
        Condition.expected(index >= 1, "Index should be equal or greater than 1");
        return errorMessages.size() > index ? errorMessages.get(index) : errorMessages.get(0);
    }

    /**
     * Aggregated assertion to assert any company specific id string
     *
     * @param heading
     *            the heading of the {@link Assertions#assertAll(Executable...)}
     * @param errorMessages
     *            the expected error messages (null, empty, blank)
     * @param consumer
     *            the target of the assertion (the generated id is the incoming parameter)
     */
    public static void assertInvalidIdParameter(final String heading, final List<String> errorMessages,
            final BaseExceptionConsumer<String> consumer) {
        Condition.notBlank(heading, "Heading should not be blank.");
        Condition.notNull(errorMessages, "Error messages list should not be null.");
        Condition.notEmpty(errorMessages, "Error messages should have least one element.");
        Condition.notNull(consumer, ERROR_MSG_CONSUMER_NULL);
        Assertions.assertAll(heading,
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, errorMessages.get(0),
                        () -> consumer.accept(null)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, getErrorMessage(errorMessages, 1),
                        () -> consumer.accept(StringUtils.EMPTY)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, getErrorMessage(errorMessages, 2),
                        () -> consumer.accept(Any.whitespace())));
    }

    /**
     * Aggregated assertion to assert any company specific id string
     *
     * @param errorMessages
     *            the expected error messages (null, empty, blank)
     * @param consumer
     *            the target of the assertion (the generated id is the incoming parameter)
     */
    public static void assertInvalidIdParameter(final List<String> errorMessages, final BaseExceptionConsumer<String> consumer) {
        Condition.notNull(errorMessages, "Error messages list should not be null.");
        Condition.notEmpty(errorMessages, "Error messages should have least one element.");
        Condition.notNull(consumer, ERROR_MSG_CONSUMER_NULL);
        Assertions.assertAll(
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, errorMessages.get(0),
                        () -> consumer.accept(null)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, getErrorMessage(errorMessages, 1),
                        () -> consumer.accept(StringUtils.EMPTY)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, getErrorMessage(errorMessages, 2),
                        () -> consumer.accept(Any.whitespace())));
    }

    /**
     * Aggregated assertion to assert any company specific id string
     *
     * @param heading
     *            the heading of the {@link Assertions#assertAll(Executable...)}
     * @param consumer
     *            the target of the assertion (the generated id is the incoming parameter)
     */
    public static void assertInvalidIdParameter(final String heading, final BaseExceptionConsumer<String> consumer) {
        Condition.notBlank(heading, "Heading should not be blank.");
        Condition.notNull(consumer, ERROR_MSG_CONSUMER_NULL);
        Assertions.assertAll(heading,
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, () -> consumer.accept(null)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, () -> consumer.accept(StringUtils.EMPTY)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, () -> consumer.accept(Any.whitespace())));
    }

    /**
     * Aggregated assertion to assert any company specific id string
     *
     * @param consumer
     *            the target of the assertion (the generated id is the incoming parameter)
     */
    public static void assertInvalidIdParameter(final BaseExceptionConsumer<String> consumer) {
        Condition.notNull(consumer, ERROR_MSG_CONSUMER_NULL);
        Assertions.assertAll(() -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, () -> consumer.accept(null)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, () -> consumer.accept(StringUtils.EMPTY)),
                () -> Assert.assertBaseException(BaseException.class, CoffeeFaultType.INVALID_INPUT, () -> consumer.accept(Any.whitespace())));
    }

}

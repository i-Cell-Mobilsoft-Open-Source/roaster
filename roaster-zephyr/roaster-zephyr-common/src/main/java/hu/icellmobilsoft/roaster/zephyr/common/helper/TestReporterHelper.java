/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.zephyr.common.helper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.inject.Vetoed;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Helper class containing common methods used by test reporters.
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@Vetoed
public class TestReporterHelper {

    private static final String BR = "<br>";
    
    private TestReporterHelper(){}

    /**
     * Creates base comment for the current test execution.
     *
     * @param uniqueId A unique identifier for the method. Usually the unique ID of the current test or container.
     * @return Base comment
     */
    public static String createCommentBase(String uniqueId) {
        return "Test method: " + uniqueId;
    }

    /**
     * Creates a failure comment for the current test execution.
     *
     * @param cause Reason of failure for the current test execution
     * @return Failure comment
     */
    public static String createFailureComment(Throwable cause) {
        if (Objects.nonNull(cause)) {
            return BR + BR + "Reason of failure: " + htmlEscape(cause.toString());
        }
        return BR + BR + "No reason of failure given by JUnit";
    }

    /**
     * Creates a disabled test comment for the current test execution.
     *
     * @param reason Reason listed for the current test being disabled.
     * @return Disabled comment.
     */
    public static String createDisabledTestComment(Optional<String> reason) {
        return reason.map(s -> BR + BR + "Test case has been skipped by: " + s)
                .orElse("");
    }

    /**
     * Converts a {@link LocalDateTime} instance to {@link OffsetDateTime}
     *
     * @param localDateTime non null
     * @return {@link OffsetDateTime}
     */
    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime cannot be null!");
        return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    /**
     * Calculates the execution time of the test in milliseconds.
     *
     * @param startTime non null
     * @param endTime non null
     * @return Execution time in milliseconds
     */
    public static long getDurationInMillis(LocalDateTime startTime, LocalDateTime endTime) {
        Objects.requireNonNull(startTime, "startTime cannot be null!");
        Objects.requireNonNull(endTime, "endTime cannot be null!");
        return startTime.until(endTime, ChronoUnit.MILLIS);
    }

    /**
     * Escapes HTML characters in the string.
     *
     * @param string input string
     * @return input string with html characters escaped
     */
    public static String htmlEscape(String string) {
        return StringEscapeUtils.escapeHtml4(string);
    }
}

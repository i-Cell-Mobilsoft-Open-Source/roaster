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
package hu.icellmobilsoft.roaster.tm4j.common;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.dto.tm4j.test_execution.Execution;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestResultReporter;
import hu.icellmobilsoft.roaster.tm4j.common.client.RestTm4jService;
import hu.icellmobilsoft.roaster.tm4j.common.config.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import org.apache.commons.text.StringEscapeUtils;

import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@code TestResultReporter}.
 * It published test result data to the configured TM4J server
 * for every test method which annotated with a valid {@code TestCaseId}.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Rest
public class RestTm4jReporter implements TestResultReporter {
    private static final String PASS = "Pass";
    private static final String FAIL = "Fail";
    private static final String BLOCKED = "Blocked";
    private static final String BR = "<br>";

    private final Logger log = Logger.getLogger(RestTm4jReporter.class);

    private final Tm4jReporterConfig config;
    private final RestTm4jService restTm4JService;

    /**
     * Creates an instance using the given configuration and TM4J service.
     *
     * @param config          configuration used for creating this class
     * @param restTm4JService TM4J services used for rest calls
     */
    @Inject
    public RestTm4jReporter(Tm4jReporterConfig config, RestTm4jService restTm4JService) {
        this.config = Objects.requireNonNull(config);
        this.restTm4JService = Objects.requireNonNull(restTm4JService);
        validateConfig();
    }

    private void validateConfig() {
        if (config.getProjectKey() == null) {
            throw new InvalidConfigException("projectKey parameter is missing");
        }
        if (config.getTestCycleKey() == null) {
            throw new InvalidConfigException("testCycleKey parameter is missing");
        }
        if (!restTm4JService.isTestRunExist(config.getTestCycleKey())) {
            throw new InvalidConfigException("supplied testCycleKey not found: " + config.getTestCycleKey());
        }
    }

    @Override
    public void reportSuccess(TestCaseData testCaseData) {
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatus(PASS);
            execution.setComment(createCommentBase(testCaseData.getId()));
            publishResult(execution);
        }
    }

    @Override
    public void reportFail(TestCaseData testCaseData, Throwable cause) {
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatus(FAIL);
            execution.setComment(
                    createCommentBase(testCaseData.getId()) +
                            createFailureComment(cause)
            );
            publishResult(execution);
        }
    }

    @Override
    public void reportDisabled(TestCaseData testCaseData, Optional<String> reason) {
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatus(BLOCKED);
            execution.setComment(
                    createCommentBase(testCaseData.getId()) +
                            createDisabledTestComment(reason)
            );
            publishResult(execution);
        }
    }

    private void publishResult(Execution execution) {
        restTm4JService.postResult(config.getTestCycleKey(), execution);
        log.info("Test result published to TM4J: [{0}]", execution.getTestCaseKey());
    }

    private List<String> getTestCaseIds(TestCaseData testCaseData) {
        return Arrays.stream(testCaseData.getTestMethod().getAnnotationsByType(TestCaseId.class))
                .map(TestCaseId::value)
                .map(testCaseId -> {
                    if (restTm4JService.isTestCaseExist(testCaseId)) {
                        return testCaseId;
                    } else {
                        log.warn("Test case ID not found: [{0}]", testCaseId);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Execution createExecution(TestCaseData testCaseData, String testCaseKey) {
        Execution execution = new Execution();
        execution.setProjectKey(config.getProjectKey());
        execution.setTestCaseKey(testCaseKey);
        execution.setActualStartDate(testCaseData.getStartTime().atOffset(ZoneOffset.UTC));
        execution.setActualEndDate(testCaseData.getEndTime().atOffset(ZoneOffset.UTC));
        execution.setExecutionTime(getDurationInMillis(testCaseData));
        return execution;
    }

    private long getDurationInMillis(TestCaseData testCaseData) {
        return testCaseData.getStartTime().until(testCaseData.getEndTime(), ChronoUnit.MILLIS);
    }

    private String createCommentBase(String uniqueId) {
        return "Environment: " + config.getEnvironment().toUpperCase() +
                BR + BR +
                "Test method: " + uniqueId;
    }

    private String createFailureComment(Throwable cause) {
        return BR + BR + "Reason of failure: " + htmlEscape(cause.toString());
    }

    private String htmlEscape(String string) {
        return StringEscapeUtils.escapeHtml4(string);
    }

    private String createDisabledTestComment(Optional<String> reason) {
        return reason.map(s -> BR + BR + "Test case has been skipped by: " + s)
                .orElse("");
    }

}

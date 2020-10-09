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
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestResultReporter;
import hu.icellmobilsoft.roaster.tm4j.common.client.Tm4jService;
import hu.icellmobilsoft.roaster.tm4j.common.client.model.Execution;
import hu.icellmobilsoft.roaster.tm4j.common.config.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseData;

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
public class DefaultTm4jReporter implements TestResultReporter {
    private static final String PASS = "Pass";
    private static final String FAIL = "Fail";
    private static final String BLOCKED = "Blocked";
    private static final String BR = "<br>";

    private final Logger log = Logger.getLogger(DefaultTm4jReporter.class);

    private final Tm4jReporterConfig config;
    private final Tm4jService tm4JService;

    /**
     * Creates an instance using the given configuration and the default TM4J service
     *
     * @param config configuration used for creating this class and the TM4J service
     */
    public DefaultTm4jReporter(Tm4jReporterConfig config) {
        this(config, new Tm4jService(config.getServer()));
    }

    /**
     * Creates an instance using the given configuration and TM4J service.
     *
     * @param config      configuration used for creating this class
     * @param tm4JService TM4J services used for rest calls
     */
    public DefaultTm4jReporter(Tm4jReporterConfig config, Tm4jService tm4JService) {
        this.config = Objects.requireNonNull(config);
        this.tm4JService = Objects.requireNonNull(tm4JService);
        validateConfig();
    }

    private void validateConfig() {
        if (config.getProjectKey() == null) {
            throw new InvalidConfigException("projectKey parameter is missing");
        }
        if (config.getTestCycleKey() == null) {
            throw new InvalidConfigException("testCycleKey parameter is missing");
        }
        if (!tm4JService.isTestRunExist(config.getTestCycleKey())) {
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
        tm4JService.postResult(config.getTestCycleKey(), execution);
        log.info("Test result published to TM4J: [{0}]", execution.getTestCaseKey());
    }

    private List<String> getTestCaseIds(TestCaseData testCaseData) {
        return Arrays.stream(testCaseData.getTestMethod().getAnnotationsByType(TestCaseId.class))
                .map(TestCaseId::value)
                .map(testCaseId -> {
                    if (tm4JService.isTestCaseExist(testCaseId)) {
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
        execution.setActualStartDate(testCaseData.getStartTime());
        execution.setActualEndDate(testCaseData.getEndTime());
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
        return string
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String createDisabledTestComment(Optional<String> reason) {
        return reason.map(s -> BR + BR + "Test case has been skipped by: " + s)
                .orElse("");
    }

}

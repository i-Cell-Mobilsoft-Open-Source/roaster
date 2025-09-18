/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.zephyr.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.zephyr.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestResultReporter;
import hu.icellmobilsoft.roaster.zephyr.common.client.RestZephyrService;
import hu.icellmobilsoft.roaster.zephyr.common.config.IZephyrReporterConfig;
import hu.icellmobilsoft.roaster.zephyr.common.config.ZephyrReporterConfig;
import hu.icellmobilsoft.roaster.zephyr.common.helper.TestReporterHelper;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.Execution;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.TestScriptResultType;

/**
 * Implementation of the {@code TestResultReporter} used with Zephyr Cloud. It published test result data to the configured project in the cloud for
 * every test method which annotated with a valid {@code TestCaseId}.
 *
 * @author mark.vituska
 * @since 0.11.0
 */
public class RestZephyrReporter implements TestResultReporter {
    private static final String PASS = "Pass";
    private static final String FAIL = "Fail";
    private static final String BLOCKED = "Blocked";

    private final Logger log = Logger.getLogger(RestZephyrReporter.class);

    private final IZephyrReporterConfig config = new ZephyrReporterConfig();
    private final RestZephyrService restZephyrService;

    /**
     * Default constructor, constructs a new object.
     */
    public RestZephyrReporter() {
        this(new RestZephyrService());
    }

    /**
     * Constructor with {@code RestZephyrService} parameter.
     * 
     * @param restZephyrService
     *            {@code RestZephyrService} instance
     */
    public RestZephyrReporter(RestZephyrService restZephyrService) {
        this.restZephyrService = restZephyrService;
    }

    @Override
    public void reportSuccess(TestCaseData testCaseData) {
        Objects.requireNonNull(testCaseData, "testCaseData cannot be null!");
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatusName(PASS);
            execution.setComment(TestReporterHelper.createCommentBase(testCaseData.getId()));
            reportTestSteps(testCaseId, execution, PASS, testCaseData);
            publishZephyrResult(execution, testCaseData.getTags());
        }
    }

    @Override
    public void reportFail(TestCaseData testCaseData, Throwable cause) {
        Objects.requireNonNull(testCaseData, "testCaseData cannot be null!");
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatusName(FAIL);
            execution.setComment(TestReporterHelper.createCommentBase(testCaseData.getId()) + TestReporterHelper.createFailureComment(cause));
            reportTestSteps(testCaseId, execution, FAIL, testCaseData);
            publishZephyrResult(execution, testCaseData.getTags());
        }
    }

    @Override
    public void reportDisabled(TestCaseData testCaseData, Optional<String> reason) {
        Objects.requireNonNull(testCaseData, "testCaseData cannot be null!");
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatusName(BLOCKED);
            execution.setComment(TestReporterHelper.createCommentBase(testCaseData.getId()) + TestReporterHelper.createDisabledTestComment(reason));
            publishZephyrResult(execution, testCaseData.getTags());
        }
    }

    private void reportTestSteps(String testCaseId, Execution execution, String status, TestCaseData testCaseData) {
        int numberOfTestSteps = restZephyrService.numberOfTestSteps(testCaseId, 1);
        long calculatedTestExecutionCount = numberOfTestSteps * testCaseData.getTestDataCount();
        for (int i = 0; i < calculatedTestExecutionCount; i++) {
            TestScriptResultType result = new TestScriptResultType();
            result.setStatusName(status);
            execution.getTestScriptResults().add(result);
        }
    }

    private void publishZephyrResult(Execution execution, Collection<String> tags) {
        List<String> testCycleKeys = tags.stream()
                .map(config::getTestCycleKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (testCycleKeys.isEmpty()) {
            execution.setTestCycleKey(config.getDefaultTestCycleKey());
            publishZephyrResult(execution);
            return;
        }
        for (String testCycleKey : testCycleKeys) {
            execution.setTestCycleKey(testCycleKey);
            publishZephyrResult(execution);
        }
    }

    private void publishZephyrResult(Execution execution) {
        restZephyrService.postResult(execution);
        log.info(
                "Test result published to Zephyr Cloud. Test case: [{0}], test cycle: [{1}], status: [{2}]",
                execution.getTestCaseKey(),
                execution.getTestCycleKey(),
                execution.getStatusName());
    }

    private List<String> getTestCaseIds(TestCaseData testCaseData) {
        return Arrays.stream(testCaseData.getTestMethod().getAnnotationsByType(TestCaseId.class)).map(TestCaseId::value).map(testCaseId -> {
            if (restZephyrService.isTestCaseExist(testCaseId)) {
                return testCaseId;
            } else {
                log.warn("Test case ID not found: [{0}]", testCaseId);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Execution createExecution(TestCaseData testCaseData, String testCaseKey) {
        Execution execution = new Execution();
        execution.setProjectKey(config.getProjectKey());
        execution.setTestCaseKey(testCaseKey);
        execution.setEnvironmentName(config.getEnvironment().orElse(null));
        execution.setActualEndDate(TestReporterHelper.toOffsetDateTime(testCaseData.getEndTime()));
        execution.setExecutionTime(TestReporterHelper.getDurationInMillis(testCaseData.getStartTime(), testCaseData.getEndTime()));
        execution.setExecutedById(restZephyrService.getAccountId());
        return execution;
    }
}

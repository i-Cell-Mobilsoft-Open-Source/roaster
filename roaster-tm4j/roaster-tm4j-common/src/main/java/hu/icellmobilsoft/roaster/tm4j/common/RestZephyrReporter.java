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
package hu.icellmobilsoft.roaster.tm4j.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestResultReporter;
import hu.icellmobilsoft.roaster.tm4j.common.client.RestZephyrService;
import hu.icellmobilsoft.roaster.tm4j.common.config.ITm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.helper.TestReporterHelper;
import hu.icellmobilsoft.roaster.tm4j.dto.domain.test_execution.ZephyrExecution;

/**
 * Implementation of the {@code TestResultReporter} used with Zephyr Cloud.
 * It published test result data to the configured project in the cloud
 * for every test method which annotated with a valid {@code TestCaseId}.
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@ZephyrRest
@Dependent
public class RestZephyrReporter implements TestResultReporter {

    private static final String PASS = "Pass";
    private static final String FAIL = "Fail";
    private static final String BLOCKED = "Blocked";

    private final Logger log = Logger.getLogger(RestTm4jReporter.class);

    @Inject
    private ITm4jReporterConfig config;

    @Inject
    private RestZephyrService restZephyrService;

    @Override
    public void reportSuccess(TestCaseData testCaseData) {
        List<String> zephyrTestCaseIds = getTestCaseIds(testCaseData);
        for (String testCaseId : zephyrTestCaseIds) {
            ZephyrExecution execution = createZephyrExecution(testCaseData, testCaseId);
            execution.setStatusName(PASS);
            execution.setComment(TestReporterHelper.createCommentBase(testCaseData.getId()));
            publishZephyrResult(execution, testCaseData.getTags());
        }
    }

    @Override
    public void reportFail(TestCaseData testCaseData, Throwable cause) {
        List<String> zephyrTestCaseIds = getTestCaseIds(testCaseData);
        for (String testCaseId : zephyrTestCaseIds) {
            ZephyrExecution execution = createZephyrExecution(testCaseData, testCaseId);
            execution.setStatusName(FAIL);
            execution.setComment(
                    TestReporterHelper.createCommentBase(testCaseData.getId()) +
                            TestReporterHelper.createFailureComment(cause)
            );
            publishZephyrResult(execution, testCaseData.getTags());
        }
    }

    @Override
    public void reportDisabled(TestCaseData testCaseData, Optional<String> reason) {
        List<String> zephyrTestCaseIds = getTestCaseIds(testCaseData);
        for (String testCaseId : zephyrTestCaseIds) {
            ZephyrExecution execution = createZephyrExecution(testCaseData, testCaseId);
            execution.setStatusName(BLOCKED);
            execution.setComment(
                    TestReporterHelper.createCommentBase(testCaseData.getId()) +
                            TestReporterHelper.createDisabledTestComment(reason)
            );
            publishZephyrResult(execution, testCaseData.getTags());
        }
    }

    private void publishZephyrResult(ZephyrExecution execution, Collection<String> tags) {
        List<String> testCycleKeys = tags.stream().map(config::getTestCycleKey).filter(Optional::isPresent).map(Optional::get)
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

    private void publishZephyrResult(ZephyrExecution execution) {
        restZephyrService.postResult(execution);
        log.info("Test result published to Zephyr Cloud. Test case: [{0}], test cycle: [{1}]", execution.getTestCaseKey(), execution.getTestCycleKey());
    }

    private List<String> getTestCaseIds(TestCaseData testCaseData) {
        return Arrays.stream(testCaseData.getTestMethod().getAnnotationsByType(TestCaseId.class))
                .map(TestCaseId::value)
                .map(testCaseId -> {
                    if (restZephyrService.isTestCaseExist(testCaseId)) {
                        return testCaseId;
                    } else {
                        log.warn("Test case ID not found: [{0}]", testCaseId);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ZephyrExecution createZephyrExecution(TestCaseData testCaseData, String testCaseKey) {
        ZephyrExecution zephyrExecution = new ZephyrExecution();
        zephyrExecution.setProjectKey(config.getProjectKey());
        zephyrExecution.setTestCaseKey(testCaseKey);
        zephyrExecution.setEnvironmentName(config.getEnvironment().orElse(null));
        zephyrExecution.setActualEndDate(TestReporterHelper.toOffsetDateTime(testCaseData.getEndTime()));
        zephyrExecution.setExecutionTime(TestReporterHelper.getDurationInMillis(testCaseData));
        zephyrExecution.setExecutedById(restZephyrService.getAccountId());
        return zephyrExecution;
    }
}

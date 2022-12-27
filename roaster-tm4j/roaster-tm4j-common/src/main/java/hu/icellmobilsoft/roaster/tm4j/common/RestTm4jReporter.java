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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestResultReporter;
import hu.icellmobilsoft.roaster.tm4j.common.client.RestTm4jService;
import hu.icellmobilsoft.roaster.tm4j.common.config.ITm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.helper.TestReporterHelper;
import hu.icellmobilsoft.roaster.tm4j.dto.domain.test_execution.Execution;

/**
 * Default implementation of the {@code TestResultReporter}.
 * It published test result data to the configured TM4J server
 * for every test method which annotated with a valid {@code TestCaseId}.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Tm4jRest
@Dependent
public class RestTm4jReporter implements TestResultReporter {
    private static final String PASS = "Pass";
    private static final String FAIL = "Fail";
    private static final String BLOCKED = "Blocked";

    private final Logger log = Logger.getLogger(RestTm4jReporter.class);

    @Inject
    private ITm4jReporterConfig config;

    @Inject
    private RestTm4jService restTm4JService;

    @Override
    public void reportSuccess(TestCaseData testCaseData) {
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatus(PASS);
            execution.setComment(TestReporterHelper.createCommentBase(testCaseData.getId()));
            publishResult(execution, testCaseData.getTags());
        }
    }

    @Override
    public void reportFail(TestCaseData testCaseData, Throwable cause) {
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatus(FAIL);
            execution.setComment(
                    TestReporterHelper.createCommentBase(testCaseData.getId()) +
                            TestReporterHelper.createFailureComment(cause)
            );
            publishResult(execution, testCaseData.getTags());
        }
    }

    @Override
    public void reportDisabled(TestCaseData testCaseData, Optional<String> reason) {
        for (String testCaseId : getTestCaseIds(testCaseData)) {
            Execution execution = createExecution(testCaseData, testCaseId);
            execution.setStatus(BLOCKED);
            execution.setComment(
                    TestReporterHelper.createCommentBase(testCaseData.getId()) +
                            TestReporterHelper.createDisabledTestComment(reason)
            );
            publishResult(execution, testCaseData.getTags());
        }
    }

    private void publishResult(Execution execution, Collection<String> tags) {
        List<String> testCycleKeys = tags.stream().map(config::getTestCycleKey).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());

        if (testCycleKeys.isEmpty()) {
            publishResult(execution, config.getDefaultTestCycleKey());
            return;
        }
        for (String testCycleKey : testCycleKeys) {
            publishResult(execution, testCycleKey);
        }
    }

    private void publishResult(Execution execution, String testCycleKey) {
        restTm4JService.postResult(testCycleKey, execution);
        log.info("Test result published to TM4J. Test case: [{0}], test cycle: [{1}]", execution.getTestCaseKey(), testCycleKey);
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
        execution.setEnvironment(config.getEnvironment().orElse(null));
        execution.setExecutedBy(restTm4JService.getUserKey());
        execution.setActualStartDate(TestReporterHelper.toOffsetDateTime(testCaseData.getStartTime()));
        execution.setActualEndDate(TestReporterHelper.toOffsetDateTime(testCaseData.getEndTime()));
        execution.setExecutionTime(TestReporterHelper.getDurationInMillis(testCaseData));
        return execution;
    }

}

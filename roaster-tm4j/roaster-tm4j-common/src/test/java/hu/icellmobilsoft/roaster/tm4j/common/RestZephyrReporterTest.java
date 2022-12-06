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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.tm4j.common.client.RestZephyrService;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.dto.domain.test_execution.Execution;
import hu.icellmobilsoft.roaster.tm4j.dto.domain.test_execution.ZephyrExecution;

public class RestZephyrReporterTest {

    @Captor
    private ArgumentCaptor<ZephyrExecution> executionArgumentCaptor;

    @Mock
    private Tm4jReporterConfig config;

    @Mock
    private RestZephyrService restZephyrService;

    @InjectMocks
    private RestZephyrReporter testObj;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldNotCallZephyrServiceWhenTestCaseKeyMissing() throws Exception {
        // given
        initMockConfig();
        when(restZephyrService.isTestCycleExist("test_cycle"))
                .thenReturn(true);
        when(restZephyrService.isTestCaseExist("ABC-T1"))
                .thenReturn(false);
        TestCaseData record = createRecord();

        // when
        testObj.reportSuccess(record);

        // then
        verify(restZephyrService, never()).postResult(any());
    }

    @Test
    void shouldCallZephyrServiceProperlyOnSuccessReport() throws Exception {
        // given
        initMockConfig();
        when(restZephyrService.isTestCycleExist("test_cycle"))
                .thenReturn(true);
        when(restZephyrService.isTestCaseExist("ABC-T1"))
                .thenReturn(true);
        TestCaseData record = createRecord();

        // when
        testObj.reportSuccess(record);

        // then
        verify(restZephyrService).postResult(executionArgumentCaptor.capture());

        ZephyrExecution execution = executionArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals("pk", execution.getProjectKey()),
                () -> assertEquals("ABC-T1", execution.getTestCaseKey()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 4, 20, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualEndDate()),
                () -> assertEquals((4 * 60 + 20) * 1000, execution.getExecutionTime()),
                () -> assertEquals("Pass", execution.getStatusName()),
                () -> assertNotNull(execution.getComment())

        );
    }

    @Test
    void shouldCallTm4jServiceProperlyOnFailReport() throws Exception {
        // given
        initMockConfig();
        when(restZephyrService.isTestCycleExist("test_cycle"))
                .thenReturn(true);
        when(restZephyrService.isTestCaseExist("ABC-T1"))
                .thenReturn(true);
        TestCaseData record = createRecord();
        AssertionError error = new AssertionError("error foo bar <x>");

        // when
        testObj.reportFail(record, error);

        // then
        verify(restZephyrService).postResult(executionArgumentCaptor.capture());

        ZephyrExecution execution = executionArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals("pk", execution.getProjectKey()),
                () -> assertEquals("ABC-T1", execution.getTestCaseKey()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 4, 20, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualEndDate()),
                () -> assertEquals((4 * 60 + 20) * 1000, execution.getExecutionTime()),
                () -> assertEquals("Fail", execution.getStatusName()),
                () -> assertTrue(execution.getComment().contains("error foo bar &lt;x&gt;"))
        );
    }

    @Test
    void shouldCallTm4jServiceProperlyOnDisabledReport() throws Exception {
        // given
        initMockConfig();
        when(restZephyrService.isTestCycleExist("test_cycle"))
                .thenReturn(true);
        when(restZephyrService.isTestCaseExist("ABC-T1"))
                .thenReturn(true);
        TestCaseData record = createRecord();
        Optional<String> reason = Optional.of("xxx");

        // when
        testObj.reportDisabled(record, reason);

        // then
        verify(restZephyrService).postResult(executionArgumentCaptor.capture());

        ZephyrExecution execution = executionArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals("pk", execution.getProjectKey()),
                () -> assertEquals("ABC-T1", execution.getTestCaseKey()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 4, 20, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualEndDate()),
                () -> assertEquals((4 * 60 + 20) * 1000, execution.getExecutionTime()),
                () -> assertEquals("Blocked", execution.getStatusName()),
                () -> assertTrue(execution.getComment().contains("skipped by: xxx"))
        );
    }

    private TestCaseData createRecord() throws NoSuchMethodException {
        TestCaseData record = new TestCaseData();
        record.setId("uid");
        record.setStartTime(LocalDateTime.of(1970, Month.JANUARY, 1, 10, 0, 0));
        record.setEndTime(LocalDateTime.of(1970, Month.JANUARY, 1, 10, 4, 20));
        record.setTestMethod(RestTm4JReporterTest.TestClass.class.getMethod("foo"));
        record.setTags(Collections.emptySet());
        return record;
    }

    private void initMockConfig() {
        when(config.getEnvironment()).thenReturn(Optional.of("dev"));
        when(config.getProjectKey()).thenReturn("pk");
        when(config.getDefaultTestCycleKey()).thenReturn("test_cycle");
    }

    static class TestClass {
        @TestCaseId("ABC-T1")
        public void foo() {
        }
    }
}

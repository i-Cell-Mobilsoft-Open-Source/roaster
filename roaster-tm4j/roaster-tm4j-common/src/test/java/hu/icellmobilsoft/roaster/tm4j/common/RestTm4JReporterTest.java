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

import hu.icellmobilsoft.roaster.dto.tm4j.test_execution.Execution;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.client.RestTm4jService;
import hu.icellmobilsoft.roaster.tm4j.common.config.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestCaseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestTm4JReporterTest {

    private RestTm4jService restTm4JService;
    private ArgumentCaptor<Execution> executionArgumentCaptor;

    @BeforeEach
    void setUp() {
        restTm4JService = mock(RestTm4jService.class);
        executionArgumentCaptor = ArgumentCaptor.forClass(Execution.class);
    }

    @Test
    void shouldThrowExceptionOnMissingProjectKeyConfig() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();

        // when
        Executable executable = () -> new RestTm4jReporter(config, restTm4JService);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldThrowExceptionOnMissingTestCycleKeyConfig() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setProjectKey("pk");

        // when
        Executable executable = () -> new RestTm4jReporter(config, restTm4JService);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldThrowExceptionOnInvalidTestCycleKeyConfig() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setProjectKey("pk");
        config.setTestCycleKey("test_cycle");

        // when
        Executable executable = () -> new RestTm4jReporter(config, restTm4JService);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldNotCallTm4jServiceWhenTestCaseKeyMissing() throws Exception {
        // given
        Tm4jReporterConfig config = createValidConfig();
        when(restTm4JService.isTestRunExist("test_cycle"))
                .thenReturn(true);
        when(restTm4JService.isTestCaseExist("ABC-T1"))
                .thenReturn(false);
        RestTm4jReporter testObj = new RestTm4jReporter(config, restTm4JService);
        TestCaseData record = createRecord();

        // when
        testObj.reportSuccess(record);

        // then
        verify(restTm4JService, never()).postResult(any(), any());
    }

    @Test
    void shouldCallTm4jServiceProperlyOnSuccessReport() throws Exception {
        // given
        Tm4jReporterConfig config = createValidConfig();
        when(restTm4JService.isTestRunExist("test_cycle"))
                .thenReturn(true);
        when(restTm4JService.isTestCaseExist("ABC-T1"))
                .thenReturn(true);
        RestTm4jReporter testObj = new RestTm4jReporter(config, restTm4JService);
        TestCaseData record = createRecord();

        // when
        testObj.reportSuccess(record);

        // then
        verify(restTm4JService).postResult(eq("test_cycle"), executionArgumentCaptor.capture());

        Execution execution = executionArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals("pk", execution.getProjectKey()),
                () -> assertEquals("ABC-T1", execution.getTestCaseKey()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 0, 0, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualStartDate()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 4, 20, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualEndDate()),
                () -> assertEquals((4 * 60 + 20) * 1000, execution.getExecutionTime()),
                () -> assertEquals("Pass", execution.getStatus()),
                () -> assertNotNull(execution.getComment())

        );
    }

    @Test
    void shouldCallTm4jServiceProperlyOnFailReport() throws Exception {
        // given
        Tm4jReporterConfig config = createValidConfig();
        when(restTm4JService.isTestRunExist("test_cycle"))
                .thenReturn(true);
        when(restTm4JService.isTestCaseExist("ABC-T1"))
                .thenReturn(true);
        RestTm4jReporter testObj = new RestTm4jReporter(config, restTm4JService);
        TestCaseData record = createRecord();
        AssertionError error = new AssertionError("error foo bar <x>");

        // when
        testObj.reportFail(record, error);

        // then
        verify(restTm4JService).postResult(eq("test_cycle"), executionArgumentCaptor.capture());

        Execution execution = executionArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals("pk", execution.getProjectKey()),
                () -> assertEquals("ABC-T1", execution.getTestCaseKey()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 0, 0, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualStartDate()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 4, 20, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualEndDate()),
                () -> assertEquals((4 * 60 + 20) * 1000, execution.getExecutionTime()),
                () -> assertEquals("Fail", execution.getStatus()),
                () -> assertTrue(execution.getComment().contains("error foo bar &lt;x&gt;"))
        );
    }

    @Test
    void shouldCallTm4jServiceProperlyOnDisabledReport() throws Exception {
        // given
        Tm4jReporterConfig config = createValidConfig();
        when(restTm4JService.isTestRunExist("test_cycle"))
                .thenReturn(true);
        when(restTm4JService.isTestCaseExist("ABC-T1"))
                .thenReturn(true);
        RestTm4jReporter testObj = new RestTm4jReporter(config, restTm4JService);
        TestCaseData record = createRecord();
        Optional<String> reason = Optional.of("xxx");

        // when
        testObj.reportDisabled(record, reason);

        // then
        verify(restTm4JService).postResult(eq("test_cycle"), executionArgumentCaptor.capture());

        Execution execution = executionArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals("pk", execution.getProjectKey()),
                () -> assertEquals("ABC-T1", execution.getTestCaseKey()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 0, 0, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualStartDate()),
                () -> assertEquals(ZonedDateTime.of(1970, 1, 1, 10, 4, 20, 0, ZoneOffset.systemDefault()).toOffsetDateTime(), execution.getActualEndDate()),
                () -> assertEquals((4 * 60 + 20) * 1000, execution.getExecutionTime()),
                () -> assertEquals("Blocked", execution.getStatus()),
                () -> assertTrue(execution.getComment().contains("skipped by: xxx"))
        );
    }

    private TestCaseData createRecord() throws NoSuchMethodException {
        TestCaseData record = new TestCaseData();
        record.setId("uid");
        record.setStartTime(LocalDateTime.of(1970, Month.JANUARY, 1, 10, 0, 0));
        record.setEndTime(LocalDateTime.of(1970, Month.JANUARY, 1, 10, 4, 20));
        record.setTestMethod(TestClass.class.getMethod("foo"));
        return record;
    }

    private Tm4jReporterConfig createValidConfig() {
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setEnvironment("dev");
        config.setProjectKey("pk");
        config.setTestCycleKey("test_cycle");
        return config;
    }

    static class TestClass {
        @TestCaseId("ABC-T1")
        public void foo() {
        }
    }
}

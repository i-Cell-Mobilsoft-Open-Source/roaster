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
package hu.icellmobilsoft.roaster.zephyr.junit5;

import static hu.icellmobilsoft.roaster.zephyr.junit5.ZephyrExtension.START_TIME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentCaptor;

import hu.icellmobilsoft.roaster.zephyr.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestResultReporter;

class ZephyrExtensionTest {

    private ZephyrExtension testObj;

    private TestResultReporter testResultReporter;
    private ExtensionContext extensionContext;
    private ArgumentCaptor<TestCaseData> recordCaptor;
    private LocalDateTime testStartTime;

    @BeforeEach
    void setUp() throws Exception {
        testResultReporter = mock(TestResultReporter.class);
        recordCaptor = ArgumentCaptor.forClass(TestCaseData.class);
        testObj = new ZephyrExtension(() -> testResultReporter);

        extensionContext = mock(ExtensionContext.class);
        when(extensionContext.getUniqueId())
                .thenReturn("uid");
        when(extensionContext.getDisplayName())
                .thenReturn("display name");
        when(extensionContext.getRequiredTestMethod())
                .thenReturn(TestClass.class.getDeclaredMethod("foo"));

        ExtensionContext.Store store = mock(ExtensionContext.Store.class);
        testStartTime = LocalDateTime.of(1234, Month.MAY, 6, 12, 34, 56);
        when(store.remove(START_TIME, LocalDateTime.class))
                .thenReturn(testStartTime);
        when(extensionContext.getStore(any()))
                .thenReturn(store);
    }

    @Test
    void shouldStoreTimeOnTestStart() {
        // given

        // when
        testObj.beforeTestExecution(extensionContext);

        // then
        verify(extensionContext.getStore(null))
                .put(eq(START_TIME), any(LocalDateTime.class));
    }

    @Test
    void shouldCallReporterOnSuccessfulTest() {
        // given

        // when
        testObj.testSuccessful(extensionContext);

        // then
        verify(testResultReporter).reportSuccess(recordCaptor.capture());

        TestCaseData record = recordCaptor.getValue();
        assertAll(
                () -> assertEquals("uid", record.getId()),
                () -> assertEquals("display name", record.getDisplayName()),
                () -> assertEquals(TestClass.class.getDeclaredMethod("foo"), record.getTestMethod()),
                () -> assertEquals(testStartTime, record.getStartTime()),
                () -> assertNotNull(record.getEndTime())
        );
    }

    @Test
    void shouldCallReporterOnFailedTest() {
        // given
        AssertionError error = new AssertionError("error");

        // when
        testObj.testFailed(extensionContext, error);

        // then
        verify(testResultReporter).reportFail(recordCaptor.capture(), same(error));

        TestCaseData record = recordCaptor.getValue();
        assertAll(
                () -> assertEquals("uid", record.getId()),
                () -> assertEquals("display name", record.getDisplayName()),
                () -> assertEquals(TestClass.class.getDeclaredMethod("foo"), record.getTestMethod()),
                () -> assertEquals(testStartTime, record.getStartTime()),
                () -> assertNotNull(record.getEndTime())
        );
    }

    @Test
    void shouldCallReporterOnDisabledTest() {
        // given
        Optional<String> reason = Optional.of("I don't want to");

        // when
        testObj.testDisabled(extensionContext, reason);

        // then
        verify(testResultReporter).reportDisabled(recordCaptor.capture(), same(reason));

        TestCaseData record = recordCaptor.getValue();
        assertAll(
                () -> assertEquals("uid", record.getId()),
                () -> assertEquals("display name", record.getDisplayName()),
                () -> assertEquals(TestClass.class.getDeclaredMethod("foo"), record.getTestMethod()),
                () -> assertEquals(testStartTime, record.getStartTime()),
                () -> assertNotNull(record.getEndTime())
        );
    }

    static class TestClass {
        @TestCaseId("ABC-T1")
        public void foo() {
        }
    }
}

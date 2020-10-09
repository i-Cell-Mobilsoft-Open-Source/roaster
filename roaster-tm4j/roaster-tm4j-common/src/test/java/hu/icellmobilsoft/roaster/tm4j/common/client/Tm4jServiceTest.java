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
package hu.icellmobilsoft.roaster.tm4j.common.client;

import hu.icellmobilsoft.roaster.tm4j.common.client.model.Execution;
import hu.icellmobilsoft.roaster.tm4j.common.client.model.TestRun;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Tm4jServiceTest {

    private Tm4jClient tm4jClient;
    private Tm4jService testObj;

    @BeforeEach
    void setUp() {
        tm4jClient = mock(Tm4jClient.class);
        testObj = new Tm4jService(tm4jClient);
    }

    @Test
    void shouldReturnExistsWhenHttpResponseIs200() throws Exception {
        // given
        String testCaseKey = "test-case-key-1";
        Call<TestRun> call = (Call<TestRun>) mock(Call.class);
        when(tm4jClient.getTestCase(testCaseKey))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(Response.success(new TestRun()));

        // when
        boolean exist = testObj.isTestCaseExist(testCaseKey);

        // then
        assertTrue(exist);
    }

    @Test
    void shouldReturnNotExistsWhenHttpResponseIs404() throws Exception {
        // given
        String testCaseKey = "test-case-key-1";
        Call<TestRun> call = (Call<TestRun>) mock(Call.class);
        when(tm4jClient.getTestCase(testCaseKey))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(Response.error(404, ResponseBody.create(MediaType.get("text/plain"), "error")));

        // when
        boolean exist = testObj.isTestCaseExist(testCaseKey);

        // then
        assertFalse(exist);
    }

    @Test
    void shouldThrowExceptionWhenHttpResponseIs500() throws Exception {
        // given
        String testCaseKey = "test-case-key-1";
        Call<TestRun> call = (Call<TestRun>) mock(Call.class);
        when(tm4jClient.getTestCase(testCaseKey))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(Response.error(500, ResponseBody.create(MediaType.get("text/plain"), "error")));

        // when
        Executable executable = () -> testObj.isTestCaseExist(testCaseKey);

        // then
        assertThrows(Tm4jClientException.class, executable);
    }

    @Test
    void shouldThrowExceptionWhenIsTestCaseExistsThrowsIOException() throws Exception {
        // given
        String testCaseKey = "test-case-key-1";
        Call<TestRun> call = (Call<TestRun>) mock(Call.class);
        when(tm4jClient.getTestCase(testCaseKey))
                .thenReturn(call);
        when(call.execute())
                .thenThrow(IOException.class);

        // when
        Executable executable = () -> testObj.isTestCaseExist(testCaseKey);

        // then
        assertThrows(Tm4jClientException.class, executable);
    }

    @Test
    void shouldTrueWhenTestRunExistsReturns200() throws Exception {
        // given
        String testRunKey = "test-run-key-1";
        Call<TestRun> call = (Call<TestRun>) mock(Call.class);
        when(tm4jClient.getTestRun(testRunKey))
                .thenReturn(call);
        when(call.execute())
                .thenReturn(Response.success(new TestRun()));

        // when
        boolean exist = testObj.isTestRunExist(testRunKey);

        // then
        assertTrue(exist);
    }

    @Test
    void shouldThrowExceptionWhenIsTestRunExistsThrowsIOException() throws Exception {
        // given
        String testRunKey = "test-run-key-1";
        Call<TestRun> call = (Call<TestRun>) mock(Call.class);
        when(tm4jClient.getTestRun(testRunKey))
                .thenReturn(call);
        when(call.execute())
                .thenThrow(IOException.class);

        // when
        Executable executable = () -> testObj.isTestRunExist(testRunKey);

        // then
        assertThrows(Tm4jClientException.class, executable);
    }

    @Test
    void shouldNotThrowExceptionOnSuccessfulPostExecutionCall() {
        // given
        String testRunKey = "test-run-key-1";
        Execution execution = new Execution();
        Call<List<Execution>> call = (Call<List<Execution>>) mock(Call.class);
        when(tm4jClient.postExecutionsForTestRun(testRunKey, singletonList(execution)))
                .thenReturn(call);

        // when
        testObj.postResult(testRunKey, execution);

        // then
        verify(tm4jClient).postExecutionsForTestRun(testRunKey, singletonList(execution));
    }

    @Test
    void shouldThrowExceptionOnFailedPostExecutionCall() throws Exception {
        // given
        String testRunKey = "test-run-key-1";
        Execution execution = new Execution();
        Call<List<Execution>> call = (Call<List<Execution>>) mock(Call.class);
        when(tm4jClient.postExecutionsForTestRun(testRunKey, singletonList(execution)))
                .thenReturn(call);
        when(call.execute())
                .thenThrow(IOException.class);

        // when
        Executable executable = () -> testObj.postResult(testRunKey, execution);

        // then
        assertThrows(Tm4jClientException.class, executable);
    }

}

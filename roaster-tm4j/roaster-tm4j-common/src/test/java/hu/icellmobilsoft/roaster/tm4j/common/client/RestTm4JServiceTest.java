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

import hu.icellmobilsoft.roaster.tm4j.dto.domain.test_execution.Execution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestTm4JServiceTest {

    @Mock
    private Tm4jRestClient tm4jClient;

    @InjectMocks
    private RestTm4jService testObj;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnExistsWhenHttpResponseIs200() {
        // given
        String testCaseKey = "test-case-key-1";
        Response response = mock(Response.class);
        when(tm4jClient.headTestCase(testCaseKey))
                .thenReturn(response);
        when(response.getStatusInfo())
                .thenReturn(Response.Status.OK);

        // when
        boolean exist = testObj.isTestCaseExist(testCaseKey);

        // then
        assertTrue(exist);
    }

    @Test
    void shouldReturnNotExistsWhenHttpResponseIs404() {
        // given
        String testCaseKey = "test-case-key-1";
        Response response = mock(Response.class);
        when(tm4jClient.headTestCase(testCaseKey))
                .thenReturn(response);
        when(response.getStatusInfo())
                .thenReturn(Response.Status.NOT_FOUND);

        // when
        boolean exist = testObj.isTestCaseExist(testCaseKey);

        // then
        assertFalse(exist);
    }

    @Test
    void shouldThrowExceptionWhenHttpResponseIs500() {
        // given
        String testCaseKey = "test-case-key-1";
        Response response = mock(Response.class);
        when(tm4jClient.headTestCase(testCaseKey))
                .thenReturn(response);
        when(response.getStatusInfo())
                .thenReturn(Response.Status.INTERNAL_SERVER_ERROR);

        // when
        Executable executable = () -> testObj.isTestCaseExist(testCaseKey);

        // then
        assertThrows(Tm4jClientException.class, executable);
    }

    @Test
    void shouldTrueWhenTestRunExistsReturns200() {
        // given
        String testRunKey = "test-run-key-1";
        Response response = mock(Response.class);
        when(tm4jClient.headTestRun(testRunKey))
                .thenReturn(response);
        when(response.getStatusInfo())
                .thenReturn(Response.Status.OK);

        // when
        boolean exist = testObj.isTestRunExist(testRunKey);

        // then
        assertTrue(exist);
    }

    @Test
    void shouldNotThrowExceptionOnSuccessfulPostExecutionCall() {
        // given
        String testRunKey = "test-run-key-1";
        Execution execution = new Execution();

        // when
        testObj.postResult(testRunKey, execution);

        // then
        verify(tm4jClient).postExecutions(testRunKey, List.of(execution));
    }

}

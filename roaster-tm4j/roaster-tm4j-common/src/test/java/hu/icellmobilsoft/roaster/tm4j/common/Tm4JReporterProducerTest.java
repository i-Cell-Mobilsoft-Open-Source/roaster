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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestResultReporter;
import hu.icellmobilsoft.roaster.tm4j.common.config.RoasterConfigKeys;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;

class Tm4JReporterProducerTest {

    @Mock
    private Provider<TestResultReporter> testResultReporterProvider;

    @InjectMocks
    private Tm4jReporterProducer testObj;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldCreateNoopReporterIfTm4jDisabled() {
        // given
        System.setProperty(RoasterConfigKeys.ENABLED, Boolean.FALSE.toString());
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        TestResultReporter testResultReporter = mock(TestResultReporter.class);
        when(testResultReporterProvider.get()).thenReturn(testResultReporter);

        // when
        TestResultReporter reporter = testObj.createReporter(config);

        // then
        assertEquals(NoopTestResultReporter.class, reporter.getClass());
    }

    @Test
    void shouldUseReporterProducerIfTm4jEnabled() {
        // given
        System.setProperty(RoasterConfigKeys.ENABLED, Boolean.TRUE.toString());
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        TestResultReporter testResultReporter = mock(TestResultReporter.class);
        when(testResultReporterProvider.get()).thenReturn(testResultReporter);

        // when
        TestResultReporter reporter = testObj.createReporter(config);

        // then
        assertEquals(testResultReporter, reporter);
    }
}

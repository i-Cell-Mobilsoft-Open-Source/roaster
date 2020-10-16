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

import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestResultReporter;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Tm4JReporterProducerTest {

    @Test
    void shouldCreateNoopReporterIfTm4jDisabled() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setEnabled(false);
        TestResultReporter testResultReporter = mock(TestResultReporter.class);

        // when
        TestResultReporter reporter = new Tm4jReporterProducer(() -> testResultReporter)
                .createReporter(config);

        // then
        assertEquals(NoopTestResultReporter.class, reporter.getClass());
    }

    @Test
    void shouldUseReporterProducerIfTm4jEnabled() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setEnabled(true);
        TestResultReporter testResultReporter = mock(TestResultReporter.class);

        // when
        TestResultReporter reporter = new Tm4jReporterProducer(() -> testResultReporter)
                .createReporter(config);

        // then
        assertEquals(testResultReporter, reporter);
    }
}

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

import hu.icellmobilsoft.roaster.tm4j.common.api.TestResultReporter;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Tm4jReporterFactoryTest {

    @Test
    void shouldCreateNoopReporterIfTm4jDisabled() {
        // given
        Config mockConfig = mock(Config.class);
        when(mockConfig.getOptionalValue("roaster.tm4j.enabled", Boolean.class))
                .thenReturn(Optional.of(false));
        ConfigProviderResolver.instance().registerConfig(mockConfig, null);

        // when
        TestResultReporter reporter = new Tm4jReporterFactory().createReporter();

        // then
        assertEquals(NoopTestResultReporter.class, reporter.getClass());
    }
}

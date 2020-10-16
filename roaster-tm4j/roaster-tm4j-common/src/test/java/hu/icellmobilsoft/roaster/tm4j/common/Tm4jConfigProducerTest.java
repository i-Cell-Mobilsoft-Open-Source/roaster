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

import hu.icellmobilsoft.roaster.tm4j.common.config.RoasterConfigKeys;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Tm4jConfigProducerTest {

    @Test
    void shouldMapRoasterConfigCorrectly() {
        // given
        Config mockConfig = mock(Config.class);
        when(mockConfig.getOptionalValue("roaster.tm4j.enabled", Boolean.class))
                .thenReturn(Optional.of(false));
        ConfigProviderResolver.instance().registerConfig(mockConfig, null);
        mockConfig(mockConfig, RoasterConfigKeys.ENABLED, Boolean.class, false);
        mockConfig(mockConfig, RoasterConfigKeys.PROJECT_KEY, String.class, "TEST");
        mockConfig(mockConfig, RoasterConfigKeys.TEST_CYCLE_KEY, String.class, "TEST-C42");
        mockConfig(mockConfig, RoasterConfigKeys.ENVIRONMENT, String.class, "test-3");
        mockConfig(mockConfig, RoasterConfigKeys.Server.BASIC_AUTH_TOKEN, String.class, "bat");
        mockConfig(mockConfig, RoasterConfigKeys.Server.USER_NAME, String.class, "user1");
        mockConfig(mockConfig, RoasterConfigKeys.Server.PASSWORD, String.class, "pw1");

        // when
        Tm4jReporterConfig config = new Tm4jConfigProducer().createConfig();

        // then
        assertAll(
                () -> assertEquals("TEST", config.getProjectKey()),
                () -> assertEquals("TEST-C42", config.getTestCycleKey()),
                () -> assertEquals("test-3", config.getEnvironment()),
                () -> assertEquals("bat", config.getServer().getBasicAuthToken()),
                () -> assertEquals("user1", config.getServer().getUserName()),
                () -> assertEquals("pw1", config.getServer().getPassword())
        );
    }

    private <T> void mockConfig(Config config, String key, Class<T> type, T value) {
        when(config.getOptionalValue(key, type))
                .thenReturn(Optional.of(value));
    }
}

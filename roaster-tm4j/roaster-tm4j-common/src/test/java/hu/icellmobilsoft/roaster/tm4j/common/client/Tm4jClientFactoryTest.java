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

import hu.icellmobilsoft.roaster.tm4j.common.config.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class Tm4jClientFactoryTest {

    private Tm4jClientFactory testObj;

    @BeforeEach
    void setUp() {
        testObj = new Tm4jClientFactory();
    }

    @Test
    void shouldThrowExceptionIfUrlConfigMissing() {
        // given
        Tm4jReporterServerConfig config = new Tm4jReporterServerConfig();

        // when
        Executable executable = () -> testObj.createClient(config);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldThrowExceptionIfCredentialsConfigMissing() {
        // given
        Tm4jReporterServerConfig config = new Tm4jReporterServerConfig();
        config.setBaseUrl("http://foobar.ninja");

        // when
        Executable executable = () -> testObj.createClient(config);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldThrowExceptionIfAuthTokenAndUserNameConfigSet() {
        // given
        Tm4jReporterServerConfig config = new Tm4jReporterServerConfig();
        config.setBaseUrl("http://foobar.ninja");
        config.setBasicAuthToken("dGVzdA==");
        config.setUserName("tim");

        // when
        Executable executable = () -> testObj.createClient(config);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldCreateClientWithAuthToken() {
        // given
        Tm4jReporterServerConfig config = new Tm4jReporterServerConfig();
        config.setBaseUrl("http://foobar.ninja");
        config.setBasicAuthToken("dGVzdA==");

        // when
        Tm4jClient client = testObj.createClient(config);

        // then
        assertNotNull(client);
    }

    @Test
    void shouldCreateClientWithUsernamePassword() {
        // given
        Tm4jReporterServerConfig config = new Tm4jReporterServerConfig();
        config.setBaseUrl("http://foobar.ninja/");
        config.setUserName("tim");
        config.setPassword("secret");

        // when
        Tm4jClient client = testObj.createClient(config);

        // then
        assertNotNull(client);
    }
}

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
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterServerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.jupiter.api.Assertions.*;

class AuthHeadersFactoryTest {

    @Test
    void shouldThrowExceptionIfCredentialsConfigMissing() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setServer(new Tm4jReporterServerConfig());

        // when
        Executable executable = () -> new AuthHeadersFactory(config);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldThrowExceptionIfAuthTokenAndUserNameConfigSet() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setServer(new Tm4jReporterServerConfig());
        config.getServer().setBasicAuthToken("dGVzdA==");
        config.getServer().setUserName("tim");

        // when
        Executable executable = () -> new AuthHeadersFactory(config);

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldCreateAuthHeaderWithAuthToken() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setServer(new Tm4jReporterServerConfig());
        config.getServer().setBasicAuthToken("dGltOnNlY3JldA==");

        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();

        // when
        AuthHeadersFactory authHeadersFactory = new AuthHeadersFactory(config);
        MultivaluedMap<String, String> resultHeaders = authHeadersFactory.update(headers, null);

        // then
        assertEquals("Basic dGltOnNlY3JldA==", resultHeaders.getFirst("Authorization"));
    }

    @Test
    void shouldCreateAuthHeadersWithUsernamePassword() {
        // given
        Tm4jReporterConfig config = new Tm4jReporterConfig();
        config.setServer(new Tm4jReporterServerConfig());
        config.getServer().setUserName("tim");
        config.getServer().setPassword("secret");

        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();

        // when
        AuthHeadersFactory authHeadersFactory = new AuthHeadersFactory(config);
        MultivaluedMap<String, String> resultHeaders = authHeadersFactory.update(headers, null);

        // then
        assertEquals("Basic dGltOnNlY3JldA==", resultHeaders.getFirst("Authorization"));
    }
}

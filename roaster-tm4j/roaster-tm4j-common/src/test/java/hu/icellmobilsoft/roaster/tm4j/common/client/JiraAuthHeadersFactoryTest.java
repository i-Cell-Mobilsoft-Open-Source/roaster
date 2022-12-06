/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import hu.icellmobilsoft.roaster.api.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.JiraReporterServerConfig;
import hu.icellmobilsoft.roaster.tm4j.common.config.RoasterConfigKeys;

public class JiraAuthHeadersFactoryTest {

    @Spy
    private JiraReporterServerConfig config;

    @InjectMocks
    private JiraAuthHeadersFactory testObj;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        System.clearProperty(RoasterConfigKeys.Server.API_TOKEN);
        System.clearProperty(RoasterConfigKeys.Server.AUTH_TOKEN);
        System.clearProperty(RoasterConfigKeys.Server.USER_NAME);
    }

    @Test
    void shouldThrowExceptionIfCredentialsConfigMissing() {
        // when
        Executable executable = () -> testObj.init();

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldThrowExceptionIfAuthTokenAndEmailConfigSet() {
        // given
        System.setProperty(RoasterConfigKeys.Server.AUTH_TOKEN, "dGVzdA==");
        System.setProperty(RoasterConfigKeys.Server.EMAIL, "tim@examplemail.com");

        // when
        Executable executable = () -> testObj.init();

        // then
        assertThrows(InvalidConfigException.class, executable);
    }

    @Test
    void shouldCreateAuthHeaderWithAuthToken() {
        // given
        System.setProperty(RoasterConfigKeys.Server.AUTH_TOKEN, "dGltQGV4YW1wbGVtYWlsLmNvbTpzZWNyZXQ=");

        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();

        // when
        MultivaluedMap<String, String> resultHeaders = testObj.update(headers, null);

        // then
        assertEquals("Basic dGltQGV4YW1wbGVtYWlsLmNvbTpzZWNyZXQ=", resultHeaders.getFirst("Authorization"));
    }

    @Test
    void shouldCreateAuthHeadersWithEmailApiToken() {
        // given
        System.setProperty(RoasterConfigKeys.Server.API_TOKEN, "secret");
        System.setProperty(RoasterConfigKeys.Server.EMAIL, "tim@examplemail.com");

        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();

        // when
        MultivaluedMap<String, String> resultHeaders = testObj.update(headers, null);

        // then
        assertEquals("Basic dGltQGV4YW1wbGVtYWlsLmNvbTpzZWNyZXQ=", resultHeaders.getFirst("Authorization"));
    }
}

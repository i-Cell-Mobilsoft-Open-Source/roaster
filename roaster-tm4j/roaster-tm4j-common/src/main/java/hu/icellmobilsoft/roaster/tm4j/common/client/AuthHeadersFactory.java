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

import com.google.common.base.Strings;
import hu.icellmobilsoft.roaster.api.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterServerConfig;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Base64;

/**
 * Sets the {@literal Authorization} header for the TM4J rest client
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Dependent
public class AuthHeadersFactory implements ClientHeadersFactory {

    @Inject
    private Tm4jReporterConfig config;

    @PostConstruct
    public void init() {
        validateConfig(config.getServer());
    }

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        incomingHeaders.putSingle("Authorization", "Basic " + getBasicAuthToken(config.getServer()));

        return incomingHeaders;
    }

    private void validateConfig(Tm4jReporterServerConfig config) {
        if (Strings.isNullOrEmpty(config.getBasicAuthToken()) && (Strings.isNullOrEmpty(config.getUserName()) || Strings.isNullOrEmpty(config.getPassword()))) {
            throw new InvalidConfigException("userName and password should be set if basicAuthToken is missing");
        }
        if (!Strings.isNullOrEmpty(config.getBasicAuthToken()) && (!Strings.isNullOrEmpty(config.getUserName()) || !Strings.isNullOrEmpty(config.getPassword()))) {
            throw new InvalidConfigException("userName and password should be empty if basicAuthToken is supplied");
        }
    }

    private String getBasicAuthToken(Tm4jReporterServerConfig config) {
        return !Strings.isNullOrEmpty(config.getBasicAuthToken()) ?
                config.getBasicAuthToken() :
                base64Encode(config.getUserName() + ":" + config.getPassword());
    }

    private String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
}

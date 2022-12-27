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
package hu.icellmobilsoft.roaster.tm4j.common.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.google.common.base.Strings;

import hu.icellmobilsoft.roaster.api.InvalidConfigException;

/**
 * Configuration class defining the TM4J server access parameters.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Dependent
public class Tm4jReporterServerConfig implements ITm4jReporterServerConfig {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Config config = ConfigProvider.getConfig();

    @Override
    public void validate() throws InvalidConfigException {
        String basicAuthTokenFromConfig = getBasicAuthTokenFromConfig();
        String userNameFromConfig = getUserNameFromConfig();
        String passwordFromConfig = getPasswordFromConfig();

        if (Strings.isNullOrEmpty(basicAuthTokenFromConfig)
                && (Strings.isNullOrEmpty(userNameFromConfig) || Strings.isNullOrEmpty(passwordFromConfig))) {
            throw new InvalidConfigException("userName and password should be set if basicAuthToken is missing");
        }
        if (!Strings.isNullOrEmpty(basicAuthTokenFromConfig)
                && (!Strings.isNullOrEmpty(userNameFromConfig) || !Strings.isNullOrEmpty(passwordFromConfig))) {
            throw new InvalidConfigException("userName and password should be empty if basicAuthToken is supplied");
        }
    }

    @Override
    public String getUserName() {
        String userNameFromConfig = getUserNameFromConfig();
        return !Strings.isNullOrEmpty(userNameFromConfig) ? //
                userNameFromConfig : //
                new String(Base64.getDecoder().decode(getBasicAuthTokenFromConfig().getBytes(CHARSET)), CHARSET).split(":")[0];
    }

    @Override
    public String getBasicAuthToken() {
        String basicAuthTokenFromConfig = getBasicAuthTokenFromConfig();
        return !Strings.isNullOrEmpty(basicAuthTokenFromConfig) ? //
                basicAuthTokenFromConfig : //
                new String(Base64.getEncoder().encode((getUserNameFromConfig() + ':' + getPasswordFromConfig()).getBytes(CHARSET)), CHARSET);
    }

    /**
     * Returns the password from the microprofile config
     * 
     * @return the password from the microprofile config
     */
    protected String getPasswordFromConfig() {
        return config.getOptionalValue(RoasterConfigKeys.Server.PASSWORD, String.class).orElse(null);
    }

    /**
     * Returns the basic auth token from the microprofile config
     *
     * @return the basic auth token from the microprofile config
     */
    protected String getBasicAuthTokenFromConfig() {
        return config.getOptionalValue(RoasterConfigKeys.Server.BASIC_AUTH_TOKEN, String.class).orElse(null);
    }

    /**
     * Returns the username from the microprofile config
     *
     * @return the username from the microprofile config
     */
    protected String getUserNameFromConfig() {
        return config.getOptionalValue(RoasterConfigKeys.Server.USER_NAME, String.class).orElse(null);
    }

}

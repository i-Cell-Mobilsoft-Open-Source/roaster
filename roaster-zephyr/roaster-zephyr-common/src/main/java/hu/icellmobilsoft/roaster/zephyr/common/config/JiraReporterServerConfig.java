/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.zephyr.common.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.google.common.base.Strings;

import hu.icellmobilsoft.roaster.api.InvalidConfigException;

/**
 * Configuration class defining the Jira server access parameters.
 *
 * @author mark.vituska
 * @since 0.11.0
 */
public class JiraReporterServerConfig implements IJiraReporterServerConfig {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Config config = ConfigProvider.getConfig();

    /**
     * Default constructor, constructs a new object.
     */
    public JiraReporterServerConfig() {
        super();
    }

    @Override
    public void validate() throws InvalidConfigException {
        String apiTokenFromConfig = getApiTokenFromConfig();
        String authTokenFromConfig = getAuthTokenFromConfig();
        String emailFromConfig = getEmailFromConfig();

        if (Strings.isNullOrEmpty(authTokenFromConfig)
                && (Strings.isNullOrEmpty(emailFromConfig) || Strings.isNullOrEmpty(apiTokenFromConfig))) {
            throw new InvalidConfigException("email and api token should be set if authToken is missing");
        }
        if (!Strings.isNullOrEmpty(authTokenFromConfig)
                && (!Strings.isNullOrEmpty(emailFromConfig) || !Strings.isNullOrEmpty(apiTokenFromConfig))) {
            throw new InvalidConfigException("email and api token should be empty if authToken is supplied");
        }
    }

    @Override
    public String getAuthToken() {
        String basicAuthTokenFromConfig = getAuthTokenFromConfig();
        return !Strings.isNullOrEmpty(basicAuthTokenFromConfig) ? //
                basicAuthTokenFromConfig : //
                new String(Base64.getEncoder().encode((getEmailFromConfig() + ':' + getApiTokenFromConfig()).getBytes(CHARSET)), CHARSET);
    }

    @Override
    public String getEmail() {
        String userNameFromConfig = getEmailFromConfig();
        return !Strings.isNullOrEmpty(userNameFromConfig) ? //
                userNameFromConfig : //
                new String(Base64.getDecoder().decode(getAuthTokenFromConfig().getBytes(CHARSET)), CHARSET).split(":")[0];
    }

    /**
     * Returns the api token from the microprofile config
     *
     * @return the api token from the microprofile config
     */
    protected String getApiTokenFromConfig() {
        return config.getOptionalValue(RoasterConfigKeys.Server.API_TOKEN, String.class).orElse(null);
    }

    /**
     * Returns the auth token from the microprofile config
     *
     * @return the auth token from the microprofile config
     */
    protected String getAuthTokenFromConfig() {
        return config.getOptionalValue(RoasterConfigKeys.Server.AUTH_TOKEN, String.class).orElse(null);
    }

    /**
     * Returns the username from the microprofile config
     *
     * @return the username from the microprofile config
     */
    protected String getEmailFromConfig() {
        return config.getOptionalValue(RoasterConfigKeys.Server.EMAIL, String.class).orElse(null);
    }
}

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
package hu.icellmobilsoft.roaster.zephyr.common.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.google.common.base.Strings;

import hu.icellmobilsoft.roaster.api.InvalidConfigException;

/**
 * Configuration class defining the Zephyr Cloud access parameters.
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@Dependent
public class ZephyrReporterServerConfig implements IZephyrReporterServerConfig {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Config config = ConfigProvider.getConfig();

    /**
     * Default constructor, constructs a new object.
     */
    public ZephyrReporterServerConfig() {
        super();
    }

    @Override
    public void validate() throws InvalidConfigException {
        String bearerTokenFromConfig = getBearerTokenFromConfig();

        if (Strings.isNullOrEmpty(bearerTokenFromConfig)) {
            throw new InvalidConfigException("bearer token should be set");
        }
    }

    @Override
    public String getBearerToken() {
        return getBearerTokenFromConfig();
    }

    /**
     * Returns the bearer token from the microprofile config
     *
     * @return the bearer token from the microprofile config
     */
    protected String getBearerTokenFromConfig() {
        return config.getOptionalValue(RoasterConfigKeys.Cloud.BEARER_TOKEN, String.class).orElse(null);
    }
}

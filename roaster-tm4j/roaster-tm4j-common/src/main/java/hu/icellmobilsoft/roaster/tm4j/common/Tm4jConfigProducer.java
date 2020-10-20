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
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterServerConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

/**
 * Creates the {@link Tm4jReporterConfig} class from the roaster microprofile config.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Dependent
public class Tm4jConfigProducer {

    /**
     * Creates the {@link Tm4jReporterConfig} class from the roaster microprofile config.
     *
     * @return The {@code Tm4jReporterConfig} instance initialized with the roaster config values.
     */
    @Produces
    @Dependent
    public Tm4jReporterConfig createConfig() {
        return mapConfig(ConfigProvider.getConfig());
    }

    private Tm4jReporterConfig mapConfig(Config roasterConfig) {
        Tm4jReporterConfig config = new Tm4jReporterConfig();

        config.setServer(createServerConfig(roasterConfig));

        config.setEnabled(roasterConfig.getOptionalValue(RoasterConfigKeys.ENABLED, Boolean.class).orElse(true));
        config.setProjectKey(roasterConfig.getOptionalValue(RoasterConfigKeys.PROJECT_KEY, String.class).orElse(null));
        config.setTestCycleKey(roasterConfig.getOptionalValue(RoasterConfigKeys.TEST_CYCLE_KEY, String.class).orElse(null));
        config.setEnvironment(roasterConfig.getOptionalValue(RoasterConfigKeys.ENVIRONMENT, String.class).orElse("N/A"));

        return config;
    }

    private Tm4jReporterServerConfig createServerConfig(Config roasterConfig) {
        Tm4jReporterServerConfig serverConfig = new Tm4jReporterServerConfig();
        serverConfig.setBasicAuthToken(roasterConfig.getOptionalValue(RoasterConfigKeys.Server.BASIC_AUTH_TOKEN, String.class).orElse(null));
        serverConfig.setUserName(roasterConfig.getOptionalValue(RoasterConfigKeys.Server.USER_NAME, String.class).orElse(null));
        serverConfig.setPassword(roasterConfig.getOptionalValue(RoasterConfigKeys.Server.PASSWORD, String.class).orElse(null));
        return serverConfig;
    }
}

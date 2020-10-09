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
import hu.icellmobilsoft.roaster.tm4j.common.config.RoasterConfigKeys;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterConfig;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterServerConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Creates a {@code TestResultReporter} based on the Roaster configuration.
 * @see Tm4jReporterFactory#createReporter()
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public class Tm4jReporterFactory {

    /**
     * Creates a {@code TestResultReporter} based on the Roaster configuration.
     * <br><br>
     * Example yaml configuration:
     * <pre>{@code
     * roaster:
     *   tm4j:
     *     enabled: true    # The reporting can be turned of using this flag
     *     projectKey: ABC      # The project key. This is the prefix for the Jira issues also
     *     testCycleKey: ABC-C1
     *     server:
     *       url: https://jira.acme.com     # Jira server url
     *       basicAuthToken: ZXhhbXBsZS11c2VyOnNlY3JldA==       # base64(userName + ":" + password)
     *       userName: # To set the credentials the basicAuthToken or the userName + password can be used (not both)
     *       password:
     * }</pre>
     *
     * For security reasons it's recommended to set the password with command line arguments.
     * (e.g. using maven: {@code mvn test -Droaster.tm4j.server.password=secret})
     *
     * @return configured {@code TestResultReporter} implementation
     */
    public TestResultReporter createReporter() {
        Config roasterConfig = ConfigProvider.getConfig();

        Tm4jReporterConfig config = mapConfig(roasterConfig);

        return config.isEnabled() ?
                new DefaultTm4jReporter(config) :
                new NoopTestResultReporter();
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
        serverConfig.setBaseUrl(roasterConfig.getOptionalValue(RoasterConfigKeys.URL, String.class).orElse(null));
        serverConfig.setBasicAuthToken(roasterConfig.getOptionalValue(RoasterConfigKeys.BASIC_AUTH_TOKEN, String.class).orElse(null));
        serverConfig.setUserName(roasterConfig.getOptionalValue(RoasterConfigKeys.USER_NAME, String.class).orElse(null));
        serverConfig.setPassword(roasterConfig.getOptionalValue(RoasterConfigKeys.PASSWORD, String.class).orElse(null));
        return serverConfig;
    }
}

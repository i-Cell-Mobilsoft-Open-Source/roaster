/*-
 * #%L
 * Roaster
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
package hu.icellmobilsoft.roaster.zephyr.common.config;

import java.text.MessageFormat;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import hu.icellmobilsoft.roaster.api.InvalidConfigException;

/**
 * Configuration class for the TM4J reporter behaviour.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Dependent
public class ZephyrReporterConfig implements IZephyrReporterConfig {

    /**
     * Default depth of the test case structure
     */
    public static final int DEFAULT_TEST_CASE_DEPTH = 3;

    /**
     * Maximum number of results to return
     */
    public static final int DEFAULT_MAX_RESULTS = 100;

    private final Config config = ConfigProvider.getConfig();

    /**
     * Default constructor, constructs a new object.
     */
    public ZephyrReporterConfig() {
        super();
    }

    @Override
    public boolean isEnabled() {
        return config.getOptionalValue(RoasterConfigKeys.ENABLED, Boolean.class).orElse(false);
    }

    @Override
    public String getProjectKey() {
        return config.getOptionalValue(RoasterConfigKeys.PROJECT_KEY, String.class)
                .orElseThrow(() -> new InvalidConfigException("projectKey parameter is missing"));
    }

    @Override
    public String getDefaultTestCycleKey() {
        return config.getOptionalValue(RoasterConfigKeys.TEST_CYCLE_KEY, String.class)
                .orElseThrow(() -> new InvalidConfigException("testCycleKey parameter is missing"));
    }

    @Override
    public Integer getDefaultTestCaseDepth() {
        return config.getOptionalValue(RoasterConfigKeys.TEST_CASE_DEPTH_KEY, Integer.class)
                .orElse(DEFAULT_TEST_CASE_DEPTH);
    }

    @Override
    public Integer getDefaultMaxResults() {
        return config.getOptionalValue(RoasterConfigKeys.MAX_RESULTS_KEY, Integer.class)
                .orElse(DEFAULT_MAX_RESULTS);
    }

    @Override
    public Optional<String> getTestCycleKey(String tag) {
        String key = MessageFormat.format(RoasterConfigKeys.TAG_TEST_CYCLE_KEY_PATTERN, tag);
        return config.getOptionalValue(key, String.class);
    }

    @Override
    public Optional<String> getEnvironment() {
        return config.getOptionalValue(RoasterConfigKeys.ENVIRONMENT, String.class);
    }

}

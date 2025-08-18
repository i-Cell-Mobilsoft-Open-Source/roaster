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
package hu.icellmobilsoft.roaster.awaitility.common.config;

import java.time.Duration;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Helper class for obtaining Awaitility settings using microprofile config.<br>
 * General pattern is "{@code roaster.awaitility.${setting}}
 * <p>
 * ie.:
 *
 * <pre>
 *  roaster:
 *   awaitility:
 *     myPersistenceUnitName:
 *       timeout: PT30S
 *       pollDelay: PT2S
 *       pollInterval: PT1S
 *       waitLog: SUCCESS   #NONE, SUCCESS, ALL
 * </pre>
 * 
 * @author karoly.tamas
 */
public class AwaitilityConfigImpl implements AwaitilityConfig {

    /**
     * Constant <code>AWAITILITY="roaster.awaitility"</code>
     */
    private static final String AWAITILITY = "roaster.awaitility";

    /**
     * Constant <code>KEY_DELIMITER="."</code>
     */
    private static final String KEY_DELIMITER = ".";

    private static final String TIMEOUT = "timeout";
    private static final String POLL_DELAY = "pollDelay";
    private static final String POLL_INTERVAL = "pollInterval";
    private static final String WAIT_LOG = "waitLog";

    private final Logger logger = Logger.getLogger(AwaitilityConfigImpl.class);
    private final Config config = ConfigProvider.getConfig();

    /**
     * Default constructor.
     */
    public AwaitilityConfigImpl() {

    }

    @Override
    public Optional<Duration> getTimeout() {
        return getOptionalDurationConfigValue(TIMEOUT);
    }

    @Override
    public Optional<Duration> getPollDelay() {
        return getOptionalDurationConfigValue(POLL_DELAY);
    }

    @Override
    public Optional<Duration> getPollInterval() {
        return getOptionalDurationConfigValue(POLL_INTERVAL);
    }

    @Override
    public Optional<AwaitilityLogDetail> getWaitLog() {
        return getOptionalLogDetailConfigValue(WAIT_LOG);
    }

    private Optional<Duration> getOptionalDurationConfigValue(String key) {
        String fullKey = joinKey(key);
        Optional<Duration> value = config.getOptionalValue(fullKey, Duration.class);
        logConfig(fullKey, value);
        return value;
    }

    private Optional<AwaitilityLogDetail> getOptionalLogDetailConfigValue(String key) {
        String fullKey = joinKey(key);
        Optional<AwaitilityLogDetail> value = config.getOptionalValue(fullKey, AwaitilityLogDetail.class);
        logConfig(fullKey, value);
        return value;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, AWAITILITY, key);
    }

    private void logConfig(String key, Object value) {
        logger.debug("Config key [{0}] value: [{1}]", key, value);
    }
}

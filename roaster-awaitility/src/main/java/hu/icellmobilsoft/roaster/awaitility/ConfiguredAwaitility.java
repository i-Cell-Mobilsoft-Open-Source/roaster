/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.awaitility;

import java.time.Duration;
import java.util.Optional;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionEvaluationListener;
import org.awaitility.core.ConditionEvaluationLogger;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.EvaluatedCondition;
import org.awaitility.pollinterval.FibonacciPollInterval;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Do the same as {@link Awaitility#await}, but with preconfigured values.
 *
 * @author martin-nagy
 * @since 2.7.0
 */
public class ConfiguredAwaitility {

    private static final Logger log = Logger.getLogger(ConfiguredAwaitility.class);
    private static final String CONFIG_PREFIX = "roaster.awaitility.";

    /**
     * Private constructor for utility class.
     */
    private ConfiguredAwaitility() {
    }

    /**
     * Returns {@link ConditionFactory} object with values populated from {@link Config} object.
     * 
     * @return created {@link ConditionFactory} object
     */
    public static ConditionFactory await() {
        Config config = ConfigProvider.getConfig();

        ConditionFactory conditionFactory = Awaitility.await();
        Optional<Duration> timeout = config.getOptionalValue(CONFIG_PREFIX + "timeout", Duration.class);
        if (timeout.isPresent()) {
            conditionFactory = conditionFactory.timeout(timeout.get());
        }

        Optional<Duration> pollDelay = config.getOptionalValue(CONFIG_PREFIX + "pollDelay", Duration.class);
        if (pollDelay.isPresent()) {
            conditionFactory = conditionFactory.pollDelay(pollDelay.get());
        }

        Optional<Duration> pollInterval = config.getOptionalValue(CONFIG_PREFIX + "pollInterval", Duration.class);
        if (pollInterval.isPresent()) {
            conditionFactory = conditionFactory.pollInterval(pollInterval.get());
        } else {
            conditionFactory = conditionFactory.pollInterval(FibonacciPollInterval.fibonacci());
        }

        Optional<LogDetail> waitLog = config.getOptionalValue(CONFIG_PREFIX + "waitLog", LogDetail.class);
        if (waitLog.isPresent()) {
            conditionFactory = conditionFactory.conditionEvaluationListener(getConditionEvaluationListener(waitLog.get()));
        }

        return conditionFactory;
    }

    private static ConditionEvaluationListener<?> getConditionEvaluationListener(LogDetail logDetail) {
        return switch (logDetail) {
        case NONE -> null;
        case SUCCESS -> ConfiguredAwaitility::logSuccessfulWait;
        case ALL -> new ConditionEvaluationLogger(log::info);
        };
    }

    private static void logSuccessfulWait(EvaluatedCondition<?> condition) {
        if (condition.isSatisfied()) {
            log.info(
                    "[{0}] after [{1}]ms (remaining time [{2}]ms, last poll interval was [{3}])",
                    condition.getDescription(),
                    condition.getElapsedTimeInMS(),
                    condition.getRemainingTimeInMS(),
                    condition.getPollInterval());
        }
    }

    /**
     * Enum for different logging detail levels.
     */
    public enum LogDetail {
        /**
         * Logging is disabled.
         */
        NONE,
        /**
         * Only logging fulfilled conditions.
         */
        SUCCESS,
        /**
         * Logging all conditions.
         */
        ALL,
    }
}

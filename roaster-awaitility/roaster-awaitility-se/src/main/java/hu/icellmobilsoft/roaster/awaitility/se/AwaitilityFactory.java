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
package hu.icellmobilsoft.roaster.awaitility.se;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionEvaluationListener;
import org.awaitility.core.ConditionEvaluationLogger;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.EvaluatedCondition;
import org.awaitility.pollinterval.FibonacciPollInterval;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.awaitility.common.config.AwaitilityConfigImpl;
import hu.icellmobilsoft.roaster.awaitility.common.config.AwaitilityLogDetail;

/**
 * {@link Awaitility#await} konfigurált gyártásáért felelős osztály
 *
 * @author zsolt.tomai
 * @since 0.0.1
 */
public class AwaitilityFactory {

    private static final Logger log = Logger.getLogger(AwaitilityFactory.class);

    private static final AwaitilityConfigImpl awaitilityConfig = new AwaitilityConfigImpl();

    /**
     * Private constructor to prevent instantiation.
     */
    private AwaitilityFactory() {
    }

    /**
     * Create awaitility condition factory
     * 
     * @return {@link ConditionFactory}
     */
    public static ConditionFactory create() {
        ConditionFactory conditionFactory = Awaitility.await();
        if (awaitilityConfig.getTimeout().isPresent()) {
            conditionFactory = conditionFactory.timeout(awaitilityConfig.getTimeout().get());
        }
        if (awaitilityConfig.getPollDelay().isPresent()) {
            conditionFactory = conditionFactory.pollDelay(awaitilityConfig.getPollDelay().get());
        }
        if (awaitilityConfig.getPollInterval().isPresent()) {
            conditionFactory = conditionFactory.pollInterval(awaitilityConfig.getPollInterval().get());
        } else {
            conditionFactory = conditionFactory.pollInterval(FibonacciPollInterval.fibonacci());
        }
        if (awaitilityConfig.getWaitLog().isPresent()) {
            conditionFactory = conditionFactory.conditionEvaluationListener(getConditionEvaluationListener(awaitilityConfig.getWaitLog().get()));
        }
        return conditionFactory;
    }

    private static ConditionEvaluationListener<?> getConditionEvaluationListener(AwaitilityLogDetail awaitilityLogDetail) {
        switch (awaitilityLogDetail) {
            case NONE:
                return null;
            case SUCCESS:
                return AwaitilityFactory::logSuccessfulWait;
            case ALL:
                return new ConditionEvaluationLogger(log::info);
            default:
                throw new IllegalStateException("Unexpected value: " + awaitilityLogDetail);
        }
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

}

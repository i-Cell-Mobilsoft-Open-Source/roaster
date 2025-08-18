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
package hu.icellmobilsoft.roaster.awaitility.producer;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionEvaluationListener;
import org.awaitility.core.ConditionEvaluationLogger;
import org.awaitility.core.ConditionFactory;
import org.awaitility.core.EvaluatedCondition;
import org.awaitility.pollinterval.FibonacciPollInterval;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.awaitility.common.config.AwaitilityConfigImpl;
import hu.icellmobilsoft.roaster.awaitility.common.config.AwaitilityLogDetail;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

/**
 * {@link Awaitility#await} konfigurált gyárátsáért felelős osztály
 *
 * @author martin.nagy
 * @author tamas.karoly
 * @since 2.6.0
 */
@ApplicationScoped
public class AwaitilityProducer {

    private static final Logger log = Logger.getLogger(AwaitilityProducer.class);

    private final AwaitilityConfigImpl awaitilityConfig = new AwaitilityConfigImpl();

    /**
     * Default constructor
     */
    public AwaitilityProducer() {
    }

    /**
     * Create awaitility condition factory
     *
     * @return {@link ConditionFactory}
     */
    @Dependent
    @Produces
    public ConditionFactory createAwaitilityConditionFactory() {
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

    private ConditionEvaluationListener<?> getConditionEvaluationListener(AwaitilityLogDetail awaitilityLogDetail) {
        switch (awaitilityLogDetail) {
            case NONE:
                return null;
            case SUCCESS:
                return this::logSuccessfulWait;
            case ALL:
                return new ConditionEvaluationLogger(log::info);
            default:
                throw new IllegalStateException("Unexpected value: " + awaitilityLogDetail);
        }
    }

    private void logSuccessfulWait(EvaluatedCondition<?> condition) {
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

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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;

import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestResultReporter;
import hu.icellmobilsoft.roaster.tm4j.common.config.ITm4jReporterConfig;

/**
 * Creates a {@code TestResultReporter} based on the Roaster configuration.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Dependent
public class Tm4jReporterProducer {

    @Inject
    @Tm4jRest
    private Provider<TestResultReporter> tm4jTestResultReporterProvider;

    @Inject
    @ZephyrRest
    private Provider<TestResultReporter> zephyrTestResultReporterProvider;

    /**
     * Creates a {@code TestResultReporter} based on the Roaster configuration
     *
     * @param config
     *            tells if the reporting is enabled of not, determines the returned implementation
     * @return configured {@code TestResultReporter} implementation
     */
    @Produces
    @Dependent
    public TestResultReporter createReporter(ITm4jReporterConfig config) {
        if (config.isEnabled() && !config.useZephyr()) {
            return tm4jTestResultReporterProvider.get();
        } else if (config.isEnabled()) {
            return zephyrTestResultReporterProvider.get();
        }
        return new NoopTestResultReporter();
    }
}

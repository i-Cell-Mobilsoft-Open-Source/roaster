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
package hu.icellmobilsoft.roaster.zephyr.common;

import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestResultReporter;

import java.util.Optional;

/**
 * {@code TestResultReporter} implementation that does nothing on callback calls. Can be used for disabling the default functionality for example the
 * configuration says so.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public enum NoopTestResultReporter implements TestResultReporter {
    /**
     * Singleton instance of {@code NoopTestResultReporter}
     */
    INSTANCE;

    @Override
    public void reportSuccess(TestCaseData testCaseData) {
        // do nothing
    }

    @Override
    public void reportFail(TestCaseData testCaseData, Throwable cause) {
        // do nothing
    }

    @Override
    public void reportDisabled(TestCaseData testCaseData, Optional<String> reason) {
        // do nothing
    }
}

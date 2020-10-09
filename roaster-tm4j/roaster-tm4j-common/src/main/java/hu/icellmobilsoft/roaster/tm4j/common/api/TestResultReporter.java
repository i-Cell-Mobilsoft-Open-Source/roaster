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
package hu.icellmobilsoft.roaster.tm4j.common.api;

import java.util.Optional;

/**
 * Common interface for reporting test cases run statuses.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public interface TestResultReporter {

    /**
     * Reports that the test run was successful.
     *
     * @param record data describing the test case and run parameters
     */
    void reportSuccess(TestCaseData record);

    /**
     * Reports that the test run failed.
     *
     * @param record data describing the test case and run parameters
     * @param cause the error cause why the test failed
     */
    void reportFail(TestCaseData record, Throwable cause);

    /**
     * Reports that the test case has not run.
     *
     * @param record data describing the test case and run parameters
     * @param reason {@code Optional} {@code String} for giving the reason why the test is disabled
     */
    void reportDisabled(TestCaseData record, Optional<String> reason);

}

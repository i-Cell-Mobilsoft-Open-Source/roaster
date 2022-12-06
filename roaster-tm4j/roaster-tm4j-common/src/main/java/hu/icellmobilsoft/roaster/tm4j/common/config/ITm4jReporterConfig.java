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
package hu.icellmobilsoft.roaster.tm4j.common.config;

import java.util.Optional;

import javax.enterprise.inject.Vetoed;

/**
 * Configuration interface for the TM4J reporter behaviour.
 *
 * @author martin.nagy
 * @since 0.10.0
 */
@Vetoed
public interface ITm4jReporterConfig {
    /**
     * Returns {@literal true} if the TM4J reporting is enabled
     * 
     * @return {@literal true} if the TM4J reporting is enabled
     */
    boolean isEnabled();

    /**
     * Returns {@literal true} if the reporting should be done through Zephyr Cloud
     *
     * @return {@literal true} if the reporting should be done through Zephyr Cloud
     */
    boolean useZephyr();

    /**
     * Returns the project key. This is the prefix for the Jira issues also.
     * 
     * @return the project key
     */
    String getProjectKey();

    /**
     * Returns the default TM4J test cycle key. E.g. {@literal ABC-C1} where ABC is the project key.
     * 
     * @return the default TM4J test cycle key
     */
    String getDefaultTestCycleKey();

    /**
     * Returns the test cycle for the given tag
     * 
     * @param tag
     *            test tag
     * @return the test cycle for the given tag
     */
    Optional<String> getTestCycleKey(String tag);

    /**
     * Returns the name of the current environment where the tests are running
     * 
     * @return The name of the current environment where the tests are running
     */
    Optional<String> getEnvironment();

}

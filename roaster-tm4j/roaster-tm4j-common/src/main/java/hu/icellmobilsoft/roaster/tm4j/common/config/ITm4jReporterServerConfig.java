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

import jakarta.enterprise.inject.Vetoed;

import hu.icellmobilsoft.roaster.api.InvalidConfigException;

/**
 * Configuration interface defining the TM4J server access parameters.
 *
 * @author martin.nagy
 * @since 0.10.0
 */
@Vetoed
public interface ITm4jReporterServerConfig {

    /**
     * Validates the configuration
     *
     * @throws InvalidConfigException
     *             on validation error
     */
    void validate() throws InvalidConfigException;

    /**
     * Returns the username
     *
     * @return the username
     */
    String getUserName();

    /**
     * Returns the basic auth token
     *
     * @return the basic auth token
     */
    String getBasicAuthToken();
}

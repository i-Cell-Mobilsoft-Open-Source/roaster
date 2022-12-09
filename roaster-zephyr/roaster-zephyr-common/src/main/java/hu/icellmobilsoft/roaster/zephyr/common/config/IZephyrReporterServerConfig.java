/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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

import javax.enterprise.inject.Vetoed;

/**
 * Configuration interface defining common Zephyr Cloud access parameters.
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@Vetoed
public interface IZephyrReporterServerConfig extends IReporterServerConfig {

    /**
     * Returns the bearer token used with the Zephyr Cloud API.
     *
     * @return the bearer token used with Zephyr Cloud API.
     */
    String getBearerToken();
}

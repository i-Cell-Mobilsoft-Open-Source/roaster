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

/**
 * Configuration for awaitility config
 * 
 * @author karoly.tamas
 */
public interface AwaitilityConfig {

    /**
     * Timeout setting
     *
     * @return optional timeout value
     */
    Optional<Duration> getTimeout();

    /**
     * Poll delay setting
     *
     * @return optional pollDelay value
     */
    Optional<Duration> getPollDelay();

    /**
     * Poll interval setting
     *
     * @return optional pollInterval value
     */
    Optional<Duration> getPollInterval();

    /**
     * Wait log setting
     *
     * @return optional waitLog value
     */
    Optional<AwaitilityLogDetail> getWaitLog();

}

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

import hu.icellmobilsoft.roaster.tm4j.common.spi.Tm4jRecord;

import java.util.Optional;

/**
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public interface Tm4jReporter {

    /**
     *
     * @param record
     */
    void reportSuccess(Tm4jRecord record);

    /**
     *
     * @param record
     * @param cause
     */
    void reportFail(Tm4jRecord record, Throwable cause);

    /**
     *
     * @param record
     * @param reason {@code Optional} {@code String}
     */
    void reportDisabled(Tm4jRecord record, Optional<String> reason);

}

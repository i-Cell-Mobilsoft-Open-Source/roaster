/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.restassured.response.producer.impl;

import jakarta.enterprise.context.Dependent;

import hu.icellmobilsoft.roaster.restassured.response.producer.spi.AbstractConfigurableResponseProcessor;

/**
 * {@link AbstractConfigurableResponseProcessor} implementation to make custom implementation injection easier
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author martin.nagy
 * @since 0.5.0
 */
@Dependent
public final class ConfigurableResponseProcessor<RESPONSE> extends AbstractConfigurableResponseProcessor<RESPONSE> {

    /**
     * Default constructor, constructs a new object.
     */
    public ConfigurableResponseProcessor() {
        super();
    }

    // empty body, everything has to go in the AbstractConfigurableResponseProcessor class
}

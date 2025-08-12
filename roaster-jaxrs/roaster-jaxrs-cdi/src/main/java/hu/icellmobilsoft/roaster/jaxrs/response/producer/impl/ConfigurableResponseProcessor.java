/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.jaxrs.response.producer.impl;

import java.util.List;

import jakarta.ws.rs.core.Configurable;

import hu.icellmobilsoft.roaster.jaxrs.se.response.ResponseProcessor;
import hu.icellmobilsoft.roaster.jaxrs.se.response.ResponseProcessorConfig;

/**
 * Configurable {@link ResponseProcessor}
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author imre.scheffer
 * @since 0.8.0
 */
public class ConfigurableResponseProcessor<RESPONSE> extends hu.icellmobilsoft.roaster.jaxrs.se.response.ConfigurableResponseProcessor <RESPONSE> {

    /**
     * Creates a new {@link hu.icellmobilsoft.roaster.jaxrs.se.response.ConfigurableResponseProcessor} instance for the given {@link ResponseProcessorConfig} and filters.
     *
     * @param config
     *            Configuration class populated with microprofile config values
     * @param filters
     *            Request filter objects. See: {@link Configurable#register(Object)}
     */
    public ConfigurableResponseProcessor(ResponseProcessorConfig config, List<Object> filters) {
        super(config, filters);
    }
}

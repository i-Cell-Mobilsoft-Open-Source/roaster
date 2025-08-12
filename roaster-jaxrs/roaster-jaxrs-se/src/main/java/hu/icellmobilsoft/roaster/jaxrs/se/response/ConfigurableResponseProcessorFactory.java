/*-
 * #%L
 * Roaster
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
package hu.icellmobilsoft.roaster.jaxrs.se.response;

import java.util.List;

import jakarta.ws.rs.core.Configurable;

/**
 * A factory class for creating instances of {@link ConfigurableResponseProcessor}
 *
 * @author martin-nagy
 * @since 2.6.0
 */
public class ConfigurableResponseProcessorFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigurableResponseProcessorFactory() {
    }

    /**
     * Returns a new ConfigurableResponseProcessor instance with config with the given configKey.
     * 
     * @param configKey
     *            config key, e.g.: {@literal testsuite.rest.example}
     * @return ConfigurableResponseProcessor with default expectedStatusCode 200
     * @param <RESPONSE>
     *            response class (any type)
     */
    public static <RESPONSE> ConfigurableResponseProcessor<RESPONSE> create(String configKey) {
        return create(configKey, 200);
    }

    /**
     * Returns a new ConfigurableResponseProcessor instance with config with the given configKey.
     * 
     * @param configKey
     *            config key, e.g.: {@literal testsuite.rest.example}
     * @param expectedStatusCode
     *            expected HTTP status code
     * @return ConfigurableResponseProcessor with the given expectedStatusCode
     * @param <RESPONSE>
     *            response class (any type)
     */
    public static <RESPONSE> ConfigurableResponseProcessor<RESPONSE> create(String configKey, int expectedStatusCode) {
        return create(configKey, expectedStatusCode, List.of());
    }

    /**
     * Returns a new ConfigurableResponseProcessor instance with config with the given configKey.
     *
     * @param configKey
     *            config key, e.g.: {@literal testsuite.rest.example}
     * @param expectedStatusCode
     *            expected HTTP status code
     * @param filters
     *            Request filter objects. See: {@link Configurable#register(Object)}
     * @return ConfigurableResponseProcessor with the given expectedStatusCode
     * @param <RESPONSE>
     *            response class (any type)
     */
    public static <RESPONSE> ConfigurableResponseProcessor<RESPONSE> create(String configKey, int expectedStatusCode, List<Object> filters) {
        ConfigurableResponseProcessor<RESPONSE> configurableResponseProcessor = new ConfigurableResponseProcessor<>(
                new ProcessorConfigImpl(configKey),
                filters);
        configurableResponseProcessor.setExpectedStatusCode(expectedStatusCode);
        return configurableResponseProcessor;
    }
}

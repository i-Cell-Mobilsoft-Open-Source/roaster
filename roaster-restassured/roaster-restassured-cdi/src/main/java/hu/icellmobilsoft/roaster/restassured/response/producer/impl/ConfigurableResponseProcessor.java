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
package hu.icellmobilsoft.roaster.restassured.response.producer.impl;

import hu.icellmobilsoft.roaster.jaxrs.se.response.ResponseProcessorConfig;
import hu.icellmobilsoft.roaster.restassured.se.response.ResponseProcessor;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Configurable {@link ResponseProcessor}
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author martin.nagy
 * @since 0.5.0
 */
public class ConfigurableResponseProcessor<RESPONSE>
        extends hu.icellmobilsoft.roaster.restassured.se.response.ConfigurableResponseProcessor<RESPONSE> {

    /**
     * Constructs a ConfigurableResponseProcessor instance with the specified configuration and request/response specifications.
     *
     * @param config
     *            the configuration object containing base URI key, path, and optional headers
     * @param jsonRequestSpecification
     *            the request specification for JSON-based requests
     * @param xmlRequestSpecification
     *            the request specification for XML-based requests
     * @param jsonResponseSpecification
     *            the response specification for JSON-based requests
     * @param xmlResponseSpecification
     *            the response specification for XML-based requests
     * @throws NullPointerException
     *             if the provided config is null
     */
    public ConfigurableResponseProcessor(ResponseProcessorConfig config, RequestSpecification jsonRequestSpecification,
            RequestSpecification xmlRequestSpecification, ResponseSpecification jsonResponseSpecification,
            ResponseSpecification xmlResponseSpecification) {
        super(config, jsonRequestSpecification, xmlRequestSpecification, jsonResponseSpecification, xmlResponseSpecification);
    }
}

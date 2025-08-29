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
package hu.icellmobilsoft.roaster.restassured.se.response;

import java.nio.charset.StandardCharsets;

import hu.icellmobilsoft.roaster.jaxrs.se.response.ProcessorConfigImpl;
import hu.icellmobilsoft.roaster.restassured.se.producer.Jackson2ObjectMapperFactoryImpl;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * A factory class for creating instances of {@link ConfigurableResponseProcessor}.<br>
 * If the created class is not configured for your needs, you can replace this class by calling a new factory using this class' static fields.
 *
 * @author martin-nagy
 * @since 2.6.0
 */
public class ConfigurableResponseProcessorFactory {

    /**
     * Application XML media type
     */
    public static final String MEDIA_TYPE_APPLICATION_XML = "application/xml";
    /**
     * Text XML media type
     */
    public static final String MEDIA_TYPE_TEXT_XML = "text/xml";
    /**
     * Combined XML media type
     */
    public static final String MEDIA_TYPE_XML = MEDIA_TYPE_APPLICATION_XML + "," + MEDIA_TYPE_TEXT_XML;
    /**
     * JSON media type
     */
    public static final String MEDIA_TYPE_APPLICATION_JSON = "application/json";

    /**
     * ObjectMapperConfig with {@link Jackson2ObjectMapperFactoryImpl}
     */
    public static final ObjectMapperConfig OBJECT_MAPPER_CONFIG = new ObjectMapperConfig()
            .jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactoryImpl());
    /**
     * JSON request specification with {@link #OBJECT_MAPPER_CONFIG}
     */
    public static final RequestSpecification JSON_REQUEST_SPECIFICATION = new RequestSpecBuilder().setContentType(MEDIA_TYPE_APPLICATION_JSON)
            .setAccept(MEDIA_TYPE_APPLICATION_JSON)
            .setConfig(RestAssuredConfig.config().objectMapperConfig(OBJECT_MAPPER_CONFIG))
            .build();
    /**
     * XML request specification
     */
    public static final RequestSpecification XML_REQUEST_SPECIFICATION = new RequestSpecBuilder().setContentType(MEDIA_TYPE_APPLICATION_XML)
            .setAccept(MEDIA_TYPE_APPLICATION_XML)
            .setConfig(
                    RestAssuredConfig.config()
                            .encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType(StandardCharsets.UTF_8, MEDIA_TYPE_XML)))
            .build();
    /**
     * JSON response specification
     */
    public static final ResponseSpecification JSON_RESPONSE_SPECIFICATION = createJsonResponseSpecification(200);

    /**
     * XML response specification
     */
    public static final ResponseSpecification XML_RESPONSE_SPECIFICATION = createXmlResponseSpecification(200);

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
        ConfigurableResponseProcessor<RESPONSE> configurableResponseProcessor = new ConfigurableResponseProcessor<>(
                new ProcessorConfigImpl(configKey),
                JSON_REQUEST_SPECIFICATION,
                XML_REQUEST_SPECIFICATION,
                createJsonResponseSpecification(expectedStatusCode),
                createXmlResponseSpecification(expectedStatusCode));
        configurableResponseProcessor.setExpectedStatusCode(expectedStatusCode);
        return configurableResponseProcessor;
    }

    private static ResponseSpecification createJsonResponseSpecification(int expectedStatusCode) {
        return new ResponseSpecBuilder().expectContentType(MEDIA_TYPE_APPLICATION_JSON).expectStatusCode(expectedStatusCode).build();
    }

    private static ResponseSpecification createXmlResponseSpecification(int expectedStatusCode) {
        return new ResponseSpecBuilder().expectContentType(MEDIA_TYPE_APPLICATION_XML).expectStatusCode(expectedStatusCode).build();
    }

}

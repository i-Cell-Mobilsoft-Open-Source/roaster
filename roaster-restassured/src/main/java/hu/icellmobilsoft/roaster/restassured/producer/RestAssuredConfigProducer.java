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
package hu.icellmobilsoft.roaster.restassured.producer;

import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.annotation.XML;
import io.restassured.config.EncoderConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;

/**
 * Producer class for RestAssured config<br>
 *
 * @author mark.petrenyi
 * @author imre.scheffer
 */
@ApplicationScoped
public class RestAssuredConfigProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public RestAssuredConfigProducer() {
        super();
    }

    /**
     * Produce JSON content setting config<br>
     * Ha igény van egy saját ObjectMapperConfig-ra, mint ami a ObjectMapperConfigProducer-ben keletkezik, akkor minta megoldásnak használható
     * például:
     * 
     * <pre>
     * &#64;Inject
     * &#64;JSON
     * private RestAssuredConfig restAssuredConfig;
     * 
     * ((Jackson2ObjectMapperFactoryImpl) restAssuredConfig.getObjectMapperConfig().jackson2ObjectMapperFactory()).getObjectMapper()
     *         .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
     * </pre>
     * 
     * @return JSON based RestAssuredConfig
     */
    @Produces
    @JSON
    private RestAssuredConfig produceJSONRestAssuredConfig() {
        ObjectMapperConfig objectMapperConfig = CDI.current().select(ObjectMapperConfig.class, new JSON.Literal()).get();
        return RestAssuredConfig.config().objectMapperConfig(objectMapperConfig);
    }

    /**
     * Produce XML content setting config
     * 
     * @return XML based RestAssuredConfig
     */
    @Produces
    @XML
    private RestAssuredConfig produceXMLRestAssuredConfig() {
        return RestAssuredConfig.config().encoderConfig(
                EncoderConfig.encoderConfig().defaultCharsetForContentType(StandardCharsets.UTF_8, RequestSpecificationProducer.MEDIA_TYPE_XML));
    }
}

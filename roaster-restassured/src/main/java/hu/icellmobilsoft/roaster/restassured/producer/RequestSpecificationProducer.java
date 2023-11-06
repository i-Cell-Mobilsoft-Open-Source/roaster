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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.annotation.XML;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;

/**
 * Producer class for RestAssured RequestSpecification
 * 
 * @author imre.scheffer
 */
@ApplicationScoped
public class RequestSpecificationProducer {

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
     * Default constructor, constructs a new object.
     */
    public RequestSpecificationProducer() {
        super();
    }

    /**
     * Produce JSON content setting RequestSpecification
     * 
     * @return XML based RequestSpecification
     */
    @Produces
    @XML
    public RequestSpecification produceXMLRequestSpecification() {
        RestAssuredConfig xmlConfig = CDI.current().select(RestAssuredConfig.class, new XML.Literal()).get();
        return new RequestSpecBuilder().setContentType(MEDIA_TYPE_APPLICATION_XML).setAccept(MEDIA_TYPE_APPLICATION_XML).setConfig(xmlConfig).build();
    }

    /**
     * Produce JSON content setting RequestSpecification
     * 
     * @return JSON based RequestSpecification
     */
    @Produces
    @JSON
    public RequestSpecification produceJSONRequestSpecification() {
        RestAssuredConfig jsonConfig = CDI.current().select(RestAssuredConfig.class, new JSON.Literal()).get();
        return new RequestSpecBuilder().setContentType(MEDIA_TYPE_APPLICATION_JSON).setAccept(MEDIA_TYPE_APPLICATION_JSON).setConfig(jsonConfig)
                .build();
    }
}

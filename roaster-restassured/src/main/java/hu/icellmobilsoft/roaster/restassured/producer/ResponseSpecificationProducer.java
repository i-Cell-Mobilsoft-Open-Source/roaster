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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.annotation.XML;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

/**
 * Producer class for RestAssured RequestSpecification
 * 
 * @author imre.scheffer
 * @since 0.0.1
 */
@ApplicationScoped
public class ResponseSpecificationProducer {

    /**
     * Produce XML content setting ResponseSpecification
     * 
     * @return XML based ResponseSpecification
     */
    @Produces
    @XML
    public ResponseSpecification produceXMLResponseSpecification() {
        return new ResponseSpecBuilder().expectContentType(RequestSpecificationProducer.MEDIA_TYPE_APPLICATION_XML).expectStatusCode(200).build();
    }

    /**
     * Produce JSON content setting ResponseSpecification
     * 
     * @return JSON based ResponseSpecification
     */
    @Produces
    @JSON
    public ResponseSpecification produceJSONResponseSpecification() {
        return new ResponseSpecBuilder().expectContentType(RequestSpecificationProducer.MEDIA_TYPE_APPLICATION_JSON).expectStatusCode(200).build();
    }
}

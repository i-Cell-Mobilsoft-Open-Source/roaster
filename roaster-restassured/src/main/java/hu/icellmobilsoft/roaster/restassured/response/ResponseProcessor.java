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
package hu.icellmobilsoft.roaster.restassured.response;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.annotation.XML;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.inject.spi.CDI;

/**
 * Base Response REST handler
 *
 * @param <RESPONSE> response class (any type)
 * @author imre.scheffer
 * @since 0.2.0
 */
public abstract class ResponseProcessor<RESPONSE> {

    /**
     * Base URI config key
     * 
     * @return config key like "project.service.base.uri"
     */
    public abstract String baseUriKey();

    /**
     * Get value by {@link #baseUriKey()} from microprofile config
     * 
     * @return value like "http://localhost:8080"
     */
    public String baseUri() {
        return ConfigProvider.getConfig().getValue(baseUriKey(), String.class);
    }

    /**
     * HTTP path to call
     * 
     * @return value like "/test/service/generate/testData"
     */
    public abstract String path();

    /**
     * Call and get JSON object from HTTP GET method
     * 
     * @param responseClass
     *            response class
     * @param pathParams
     *            response class The path parameters. See {@link RequestSpecification#get(String, Object...)} pathParams.
     * @return response object casted to responseClass
     */
    public RESPONSE getJson(Class<RESPONSE> responseClass, Object... pathParams) {
        // REST beallitasok
        RequestSpecification rSpec = createJsonRequestSpecification();
        // HTTP
        Response response = rSpec.get(path(), pathParams);
        // response feldolgozas
        return toJsonResponse(response, responseClass);
    }

    /**
     * Call and get JSON object from HTTP POST method
     * 
     * @param <REQUEST>
     *            request DTO class
     * @param requestDto
     *            request DTO object
     * @param responseClass
     *            response class
     * @param pathParams
     *            response class The path parameters. See {@link RequestSpecification#post(String, Object...)} pathParams.
     * @return response object casted to responseClass
     */
    public <REQUEST> RESPONSE postJson(REQUEST requestDto, Class<RESPONSE> responseClass, Object... pathParams) {
        RequestSpecification rSpec = createJsonRequestSpecification().body(requestDto);
        Response response = rSpec.post(path(), pathParams);
        return toJsonResponse(response, responseClass);
    }

    /**
     * Call and get XML object from HTTP GET method
     * 
     * @param responseClass
     *            response class
     * @param pathParams
     *            response class The path parameters. See {@link RequestSpecification#get(String, Object...)} pathParams.
     * @return response object casted to responseClass
     */
    public RESPONSE getXml(Class<RESPONSE> responseClass, Object... pathParams) {
        // REST beallitasok
        RequestSpecification rSpec = createXmlRequestSpecification();
        // HTTP
        Response response = rSpec.get(path(), pathParams);
        // response feldolgozas
        return toXmlResponse(response, responseClass);
    }

    /**
     * Call and get JSON object from HTTP POST method
     * 
     * @param <REQUEST>
     *            request DTO class
     * @param requestDto
     *            request DTO object
     * @param responseClass
     *            response class
     * @param pathParams
     *            response class The path parameters. See {@link RequestSpecification#post(String, Object...)} pathParams.
     * @return response object casted to responseClass
     */
    public <REQUEST> RESPONSE postXml(REQUEST requestDto, Class<RESPONSE> responseClass, Object... pathParams) {
        RequestSpecification rSpec = createXmlRequestSpecification().body(requestDto);
        Response response = rSpec.post(path(), pathParams);
        return toXmlResponse(response, responseClass);
    }

    /**
     * Creating default JSON RequestSpecification from system
     * 
     * @return Default RequestSpecification
     */
    protected RequestSpecification createJsonRequestSpecification() {
        RequestSpecification jsonSpec = CDI.current().select(RequestSpecification.class, new JSON.Literal()).get();
        return createRequestSpecification(jsonSpec);
    }

    /**
     * Creating default XML RequestSpecification from system
     * 
     * @return Default RequestSpecification
     */
    protected RequestSpecification createXmlRequestSpecification() {
        RequestSpecification xmlSpec = CDI.current().select(RequestSpecification.class, new XML.Literal()).get();
        return createRequestSpecification(xmlSpec);
    }

    /**
     * Create default ReastAssured RequestSpecification
     * 
     * @param initRequestSpecification
     *            response is expanded on this object
     * @return full setted RequestSpecification
     */
    protected RequestSpecification createRequestSpecification(RequestSpecification initRequestSpecification) {
        return RestAssured
                // given
                .given()//
                .spec(initRequestSpecification)//
                .baseUri(baseUri()).log().all();
    }

    /**
     * Process JSON RestAssured response
     * 
     * @param response
     *            RestAssured response
     * @param responseClass
     *            response DTO class
     * @return response object casted to responseClass
     */
    protected RESPONSE toJsonResponse(Response response, Class<RESPONSE> responseClass) {
        ResponseSpecification jsonSpec = CDI.current().select(ResponseSpecification.class, new JSON.Literal()).get();
        return toResponse(response, responseClass, jsonSpec);
    }

    /**
     * Process XML RestAssured response
     * 
     * @param response
     *            RestAssured response
     * @param responseClass
     *            response DTO class
     * @return response object casted to responseClass
     */
    protected RESPONSE toXmlResponse(Response response, Class<RESPONSE> responseClass) {
        ResponseSpecification xmlSpec = CDI.current().select(ResponseSpecification.class, new XML.Literal()).get();
        return toResponse(response, responseClass, xmlSpec);
    }

    /**
     * Process RestAssured response
     * 
     * @param response
     *            RestAssured response
     * @param responseClass
     *            response DTO class
     * @param iniResponseSpecification
     *            response is based on this object
     * @return response object casted to responseClass
     */
    protected RESPONSE toResponse(Response response, Class<RESPONSE> responseClass, ResponseSpecification iniResponseSpecification) {
        return response.then()//
                .log().all()//
                .spec(iniResponseSpecification)//
                .extract().response().getBody().as(responseClass);
    }
}

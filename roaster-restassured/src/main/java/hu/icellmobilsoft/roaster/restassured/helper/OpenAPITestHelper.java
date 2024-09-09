/*-
 * #%L
 * Roaster
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
package hu.icellmobilsoft.roaster.restassured.helper;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.path.MicroprofilePath;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Helper class for /openapi endpoint restassured testing
 *
 * @author mark.petrenyi
 */
@Dependent
public class OpenAPITestHelper {

    private static final String OPENAPI_VERSION_JSON_PATH = "openapi";
    private static final String DEFAULT_OPENAPI_VERSION = "3.0.3";

    @Inject
    @JSON
    private RequestSpecification requestSpecification;

    @Inject
    @JSON
    private ResponseSpecification responseSpecification;

    /**
     * Default constructor, constructs a new object.
     */
    public OpenAPITestHelper() {
        super();
    }

    /**
     * Testing /openapi
     *
     * @param baseUri
     *            URI for openapi endpoint
     */
    public void testOpenAPI(String baseUri) {
        testOpenAPI(baseUri, DEFAULT_OPENAPI_VERSION);
    }

    /**
     * Testing /openapi
     *
     * @param baseUri
     *            URI for openapi endpoint
     * @param expectedOpenapiVersion
     *            the expected openapi version
     */
    public void testOpenAPI(String baseUri, String expectedOpenapiVersion) {
        // given
        RequestSpecification requestSpec = decorateRequestSpecification(createRequestSpecification(baseUri));
        // when
        Response response = requestSpec.get(MicroprofilePath.OPENAPI_PATH);
        // then
        String openApiVersion = decorateValidatableResponse(response.then())//
                .spec(responseSpecification)//
                .extract().response().body().jsonPath().get(OPENAPI_VERSION_JSON_PATH);
        Assertions.assertEquals(expectedOpenapiVersion, openApiVersion);
    }

    /**
     * Decorates request specification with logging all. Can be overridden.
     *
     * @param initialRequestSpecification
     *            the initial request specification
     * @return the decorated request specification
     */
    protected RequestSpecification decorateRequestSpecification(RequestSpecification initialRequestSpecification) {
        return initialRequestSpecification.log().all();
    }

    /**
     * Decorates validatableResponse with logging all. Can be overridden.
     *
     * @param validatableResponse
     *            the initial response specification
     * @return the decorated response specification
     */
    protected ValidatableResponse decorateValidatableResponse(ValidatableResponse validatableResponse) {
        return validatableResponse.log().all();
    }

    private RequestSpecification createRequestSpecification(String baseUri) {
        return RestAssured.given()//
                .spec(requestSpecification)//
                .baseUri(baseUri);
    }
}

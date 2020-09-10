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
package hu.icellmobilsoft.roaster.restassured.helper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.path.MicroprofilePath;
import io.restassured.RestAssured;
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
    private static final String EXPECTED_OPENAPI_VERSION = "3.0.1";

    @Inject
    @JSON
    private RequestSpecification requestSpecification;

    @Inject
    @JSON
    private ResponseSpecification responseSpecification;

    /**
     * Testing /openapi
     * 
     * @param baseUri
     *            URI for openapi endpoint
     */
    public void testOpenAPI(String baseUri) {
        String openApiVersion = RestAssured
                // given
                .given()//
                .spec(requestSpecification)//
                .baseUri(baseUri)
                // when
                .when()//
                .log().all()//
                .get(MicroprofilePath.OPENAPI_PATH)
                // then
                .then()//
                .log().all()//
                .spec(responseSpecification)//
                .extract().response().body().jsonPath().get(OPENAPI_VERSION_JSON_PATH);
        Assertions.assertEquals(EXPECTED_OPENAPI_VERSION, openApiVersion);
    }
}

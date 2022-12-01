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
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Helper class for /versionInfo endpoint restassured testing
 * 
 * @author mark.petrenyi
 */
@Dependent
public class VersionInfoTestHelper {

    @Inject
    @JSON
    private RestAssuredConfig restAssuredConfig;

    /**
     * Testing {@value MicroprofilePath#VERSION_INFO} endpoint
     * 
     * @param baseUri
     *            URI for versionInfo endpoint
     */
    public void testVersionInfo(String baseUri) {
        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri(baseUri).build();

        ResponseSpecification sp = new ResponseSpecBuilder().expectStatusCode(200).build();
        String versionInfo = RestAssured
                // given
                .given()//
                .config(restAssuredConfig)//
                .spec(requestSpecification)//
                // when
                .when()//
                .log().all()//
                .get(MicroprofilePath.VERSION_INFO)
                // then
                .then()//
                .log().all()//
                .spec(sp)//
                .extract().response().body().asString();
        Assertions.assertNotNull(versionInfo);
    }

}

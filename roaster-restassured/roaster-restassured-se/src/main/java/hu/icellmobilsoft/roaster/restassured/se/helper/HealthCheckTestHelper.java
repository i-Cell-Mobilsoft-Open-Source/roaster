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
package hu.icellmobilsoft.roaster.restassured.se.helper;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.Assertions;

import hu.icellmobilsoft.roaster.restassured.se.path.MicroprofilePath;
import hu.icellmobilsoft.roaster.restassured.se.response.ConfigurableResponseProcessorFactory;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Helper class for /health endpoint restassured testing
 *
 * @author mark.petrenyi
 * @author martin-nagy
 * @since 2.6.0
 */
public class HealthCheckTestHelper {

    private static final String STATUS_JSON_PATH = "status";

    private final RequestSpecification requestSpecification;
    private final ResponseSpecification responseSpecification;

    /**
     * Default constructor with {@link ConfigurableResponseProcessorFactory#JSON_REQUEST_SPECIFICATION} and
     * {@link ConfigurableResponseProcessorFactory#JSON_RESPONSE_SPECIFICATION}
     */
    public HealthCheckTestHelper() {
        this(ConfigurableResponseProcessorFactory.JSON_REQUEST_SPECIFICATION, ConfigurableResponseProcessorFactory.JSON_RESPONSE_SPECIFICATION);
    }

    /**
     * Constructor with custom {@link RequestSpecification} and {@link ResponseSpecification}
     *
     * @param requestSpecification
     *            {@link RequestSpecification} to be used for testing
     * @param responseSpecification
     *            {@link ResponseSpecification} to be used for testing
     */
    public HealthCheckTestHelper(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }

    /**
     * Testing /health
     *
     * @param baseUri
     *            URI for health endpoint
     */
    public void testHealth(String baseUri) {
        testHealthPath(baseUri, MicroprofilePath.HEALTH_PATH);
    }

    /**
     * Test health/ready status UP
     *
     * @param baseUri
     *            service base uri
     */
    public void testHealthReady(String baseUri) {
        testHealthPath(baseUri, MicroprofilePath.HEALTH_READY_PATH);
    }

    /**
     * Test health/live status UP
     *
     * @param baseUri
     *            service base uri
     */
    public void testHealthLive(String baseUri) {
        testHealthPath(baseUri, MicroprofilePath.HEALTH_LIVE_PATH);
    }

    /**
     * Test health/started status UP
     *
     * @param baseUri
     *            service base uri
     */
    public void testHealthStarted(String baseUri) {
        testHealthPath(baseUri, MicroprofilePath.HEALTH_STARTED_PATH);
    }

    private void testHealthPath(String baseUri, String healthPath) {
        // given
        String status = HealthCheckResponse.Status.UP.name();

        // when
        String responseStatus = RestAssured.given()
                .spec(requestSpecification)
                .baseUri(baseUri)
                .when()
                .log()
                .all()
                .get(healthPath)
                .then()
                .log()
                .all()
                .spec(responseSpecification)
                .extract()
                .response()
                .body()
                .jsonPath()
                .get(STATUS_JSON_PATH);

        // then
        Assertions.assertEquals(status, responseStatus);
    }

}

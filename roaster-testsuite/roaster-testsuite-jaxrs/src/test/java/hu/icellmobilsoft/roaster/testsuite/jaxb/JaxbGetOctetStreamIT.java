/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.testsuite.jaxb;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.RestProcessor;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.impl.ConfigurableResponseProcessor;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Starts a mockserver with testcontainers to validate the jaxb rest calls
 * 
 * @author imre.scheffer
 * @since 0.8.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Test of jaxb client GET octet stream")
class JaxbGetOctetStreamIT extends BaseWeldUnitType {

    @Inject
    @RestProcessor(configKey = "testsuite.rest.test")
    private ConfigurableResponseProcessor<String> processor;

    @Inject
    @RestProcessor(configKey = "testsuite.rest.test", expectedStatusCode = 500)
    private ConfigurableResponseProcessor<String> processor500;

    private static final MockServerContainer MOCK_SERVER = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.14.0"));

    private static String BODY = "entityBody in stream?";

    @BeforeAll
    static void beforeAll() {
        MOCK_SERVER.start();

        // microprofile-config settings
        System.setProperty("roaster.tm4j.server/mp-rest/url", MOCK_SERVER.getEndpoint());

        // We set the expectations in the MockServerContainer
        MockServerClient expectation = new MockServerClient(MOCK_SERVER.getHost(), MOCK_SERVER.getServerPort());
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/entityId"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(BODY, MediaType.APPLICATION_OCTET_STREAM));
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/entityId500"))
                .respond(HttpResponse.response().withStatusCode(500).withBody(BODY, MediaType.APPLICATION_OCTET_STREAM));

        System.setProperty("example-project.example-service.url", MOCK_SERVER.getEndpoint());
    }

    @AfterAll
    static void afterAll() {
        MOCK_SERVER.close();
    }

    @Test
    @DisplayName("HTTP 200")
    void http200() {
        String response = processor.getOctetStream(String.class, "entityId");
        Assertions.assertEquals(BODY, response);
    }

    @Test
    @DisplayName("HTTP 500")
    void http500() {
        String response = processor500.getOctetStream(String.class, "entityId500");
        Assertions.assertEquals(BODY, response);
    }
}

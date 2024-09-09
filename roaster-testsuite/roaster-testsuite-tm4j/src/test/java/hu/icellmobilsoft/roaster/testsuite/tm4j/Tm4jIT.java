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
package hu.icellmobilsoft.roaster.testsuite.tm4j;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.Headers;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonPathBody;
import org.mockserver.model.MediaType;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.junit5.Tm4jExtension;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Starts a mockserver with testcontainers to validate the TM4J rest calls
 * 
 * @author martin.nagy
 * @since 0.7.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@ExtendWith(Tm4jExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Tm4jIT extends BaseWeldUnitType {

    private static final MockServerContainer MOCK_SERVER = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.14.0"));
    private static MockServerClient MOCK_SERVER_CLIENT;

    @BeforeAll
    static void beforeAll() {
        MOCK_SERVER.start();
        MOCK_SERVER_CLIENT = new MockServerClient(MOCK_SERVER.getHost(), MOCK_SERVER.getServerPort());

        System.setProperty("roaster.tm4j.server/mp-rest/url", MOCK_SERVER.getEndpoint());

        Headers headers = new Headers(new Header("Accept", "application/json"), new Header("Authorization", "Basic ZXhhbXBsZS11c2VyOnNlY3JldA=="));

        MOCK_SERVER_CLIENT.when(HttpRequest.request(), Times.unlimited(), TimeToLive.unlimited(), -1)
                .respond(HttpResponse.response().withStatusCode(404).withBody("Not Found"));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/rest/api/2/myself").withHeaders(headers))
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody("{\"displayName\":\"Teszt Elek\",\"key\":\"test-user-1\"}"));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/rest/atm/1.0/testrun/XXX-C123").withHeaders(headers))
                .respond(HttpResponse.response().withStatusCode(200));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/rest/atm/1.0/testcase/XXX-T1").withHeaders(headers))
                .respond(HttpResponse.response().withStatusCode(200));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withMethod("POST").withPath("/rest/atm/1.0/testrun/XXX-C123/testresults").withHeaders(headers))
                .respond(HttpResponse.response().withStatusCode(200));

    }

    @AfterAll
    static void afterAll() {
        MOCK_SERVER_CLIENT.verify(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/rest/atm/1.0/testrun/XXX-C123/testresults")
                        .withBody(JsonPathBody.jsonPath("$[0][?(@.testCaseKey == 'XXX-T1')]")));
        MOCK_SERVER.close();
    }

    @Test
    @TestCaseId("XXX-T1")
    void dummyTest() {
        assertEquals(2, 1 + 1);
    }
}

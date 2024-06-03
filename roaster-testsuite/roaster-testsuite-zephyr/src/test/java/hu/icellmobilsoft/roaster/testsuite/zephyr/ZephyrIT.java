/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.testsuite.zephyr;

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
import org.mockserver.model.MediaType;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;
import hu.icellmobilsoft.roaster.zephyr.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.zephyr.junit5.ZephyrExtension;

/**
 * Starts a mockserver with testcontainers to validate the Zephyr rest calls
 * 
 * @author czenczl
 * @since 2.1.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@ExtendWith(ZephyrExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZephyrIT extends BaseWeldUnitType {

    private static final MockServerContainer MOCK_SERVER = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.14.0"));
    private static MockServerClient MOCK_SERVER_CLIENT;

    @BeforeAll
    static void beforeAll() {
        MOCK_SERVER.start();
        MOCK_SERVER_CLIENT = new MockServerClient(MOCK_SERVER.getHost(), MOCK_SERVER.getServerPort());

        System.setProperty("roaster.zephyr.server/mp-rest/url", MOCK_SERVER.getEndpoint());
        System.setProperty("hu.icellmobilsoft.roaster.zephyr.common.client.api.ZephyrRestClient/mp-rest/url", MOCK_SERVER.getEndpoint());

        Headers jiraClientHeaders = new Headers(
                new Header("Accept", "application/json"),
                new Header("Authorization", "Basic ZHVtbXlAZHVtbXkuaHU6WlhoaGJYQnNaUzExYzJWeU9uTmxZM0psZEE9PQ=="));

        Headers zephyrClientHeaders = new Headers(
                new Header("Accept", "application/json"),
                new Header("Authorization", "Bearer ZXhhbXBsZS11c2VyOnNlY3JldA=="));

        MOCK_SERVER_CLIENT.when(HttpRequest.request(), Times.unlimited(), TimeToLive.unlimited(), -1)
                .respond(HttpResponse.response().withStatusCode(404).withBody("Not Found"));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/rest/api/3/myself").withHeaders(jiraClientHeaders))
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody("{\"displayName\":\"Teszt Elek\",\"key\":\"test-user-1\"}"));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/testcases/XXX-T1").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withStatusCode(200));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/testcases/XXX-T1/teststeps").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withContentType(MediaType.APPLICATION_JSON).withBody("{\"startAt\":\"0\",\"maxResults\":\"1\",\"total\":\"1\",\"values\":[{\"inline\":null,\"testCase\":{\"self\":\"https://api.zephyrscale.smartbear.com/v2/testcases/XXX-T2/teststeps\",\"testCaseKey\":\"XXX-T2\"}}]}"));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/testcases/XXX-T2/teststeps").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withContentType(MediaType.APPLICATION_JSON).withBody("{\"startAt\":\"0\",\"maxResults\":\"2\",\"total\":\"2\"}"));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withMethod("POST").withPath("/testexecutions").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withStatusCode(200));

        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/testcases/XXX-T3").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withStatusCode(200));
        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/testcases/XXX-T3/teststeps").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withContentType(MediaType.APPLICATION_JSON).withBody("{\"startAt\":\"0\",\"maxResults\":\"1\",\"total\":\"1\",\"values\":[{\"inline\":null,\"testCase\":{\"self\":\"https://api.zephyrscale.smartbear.com/v2/testcases/XXX-T31/teststeps\",\"testCaseKey\":\"XXX-T31\"}}]}"));
        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/testcases/XXX-T31/teststeps").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withContentType(MediaType.APPLICATION_JSON).withBody("{\"startAt\":\"0\",\"maxResults\":\"1\",\"total\":\"1\",\"values\":[{\"inline\":null,\"testCase\":{\"self\":\"https://api.zephyrscale.smartbear.com/v2/testcases/XXX-T32/teststeps\",\"testCaseKey\":\"XXX-T32\"}}]}"));
        MOCK_SERVER_CLIENT.when(HttpRequest.request().withPath("/testcases/XXX-T32/teststeps").withHeaders(zephyrClientHeaders))
                .respond(HttpResponse.response().withContentType(MediaType.APPLICATION_JSON).withBody("{\"startAt\":\"0\",\"maxResults\":\"1\",\"total\":\"1\",\"values\":[{\"inline\":null,\"testCase\":{\"self\":\"https://api.zephyrscale.smartbear.com/v2/testcases/XXX-T33/teststeps\",\"testCaseKey\":\"XXX-T33\"}}]}"));
    }

    @AfterAll
    static void afterAll() {
        MOCK_SERVER_CLIENT.verify(HttpRequest.request().withMethod("GET").withPath("/testcases/XXX-T1"));
        MOCK_SERVER.close();
    }

    @Test
    @TestCaseId("XXX-T1")
    void dummyTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @TestCaseId("XXX-T3")
    void dummyTest2() {assertEquals(2, 1 + 1);
    }

}

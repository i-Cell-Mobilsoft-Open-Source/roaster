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
package hu.icellmobilsoft.roaster.testsuite.mprestclient;

import java.net.URI;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.testsuite.jaxb.dto.DtoHelper;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Starts a mockserver with testcontainers to validate the Microprofile client rest calls
 * 
 * @author imre.scheffer
 * @since 0.8.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Test of jaxb Microprofile client")
class MprestclientIT extends BaseWeldUnitType {

    private static final MockServerContainer MOCK_SERVER = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.14.0"));

    private static String BODY = "entityBody in stream?";

    private static final String URI_KEY = TestMpRestApi.class.getName() + "/mp-rest/url";

    @BeforeAll
    static void beforeAll() {
        MOCK_SERVER.start();

        // microprofile-config beallitasok
        System.setProperty(URI_KEY, MOCK_SERVER.getEndpoint());

        // MockServerContainer-ben beallitjuk a expectations-t
        MockServerClient expectation = new MockServerClient(MOCK_SERVER.getHost(), MOCK_SERVER.getServerPort());
        expectation.when(HttpRequest.request().withPath("/mp/rest/client/post"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(BODY, MediaType.APPLICATION_JSON));

    }

    @AfterAll
    static void afterAll() {
        MOCK_SERVER.close();
    }

    @Test
    @DisplayName("HTTP 200 with JAXRS MP client builder")
    void httpBuilder200() throws BaseException {
        TestMpRestApi testMpRestApiImpl = RestClientBuilder.newBuilder()
                // set URI
                .baseUri(URI.create(System.getProperty(URI_KEY)))
                // build API interface
                .build(TestMpRestApi.class);
        String response = testMpRestApiImpl.testPost(DtoHelper.createBaseRequest());
        Assertions.assertEquals(BODY, response);
    }
}

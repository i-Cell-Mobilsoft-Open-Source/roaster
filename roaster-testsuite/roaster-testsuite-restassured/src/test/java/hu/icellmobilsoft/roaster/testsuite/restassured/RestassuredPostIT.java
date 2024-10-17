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
package hu.icellmobilsoft.roaster.testsuite.restassured;

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

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseRequest;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResponse;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.se.api.exception.JsonConversionException;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonUtil;
import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.RestProcessor;
import hu.icellmobilsoft.roaster.restassured.response.producer.impl.ConfigurableResponseProcessor;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Starts a mockserver with testcontainers to validate the restassured calls
 *
 * @author imre.scheffer
 * @since 0.8.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Test of Restassured POST calls")
class RestassuredPostIT extends BaseWeldUnitType {

    @Inject
    @RestProcessor(configKey = "testsuite.rest.test")
    private ConfigurableResponseProcessor<BaseResponse> processor;

    @Inject
    @RestProcessor(configKey = "testsuite.rest.test", expectedStatusCode = 500)
    private ConfigurableResponseProcessor<BaseResponse> processor500;

    private static final MockServerContainer MOCK_SERVER = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.14.0"));

    private static BaseResponse RESPONSE_DTO = new BaseResponse()
            .withContext(new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTC())).withFuncCode(FunctionCodeType.OK);

    @BeforeAll
    static void beforeAll() throws JsonConversionException {
        MOCK_SERVER.start();

        // microprofile-config settings
        System.setProperty("roaster.tm4j.server/mp-rest/url", MOCK_SERVER.getEndpoint());

        String jsonResponse = JsonUtil.toJson(RESPONSE_DTO);

        // We set the expectations in the MockServerContainer
        MockServerClient expectation = new MockServerClient(MOCK_SERVER.getHost(), MOCK_SERVER.getServerPort());
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/entityIdJson"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(jsonResponse, MediaType.APPLICATION_JSON_UTF_8));
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/entityIdJson500"))
                .respond(HttpResponse.response().withStatusCode(500).withBody(jsonResponse, MediaType.APPLICATION_JSON_UTF_8));

        System.setProperty("example-project.example-service.url", MOCK_SERVER.getEndpoint());
    }

    @AfterAll
    static void afterAll() {
        MOCK_SERVER.close();
    }

    @Test
    @DisplayName("HTTP 200 Json request and response")
    void httpJson200() throws JsonConversionException {
        BaseRequest requestBody = new BaseRequest()
                .withContext(new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTC()));
        BaseResponse response = processor.postJson(requestBody, BaseResponse.class, "entityIdJson");
        Assertions.assertEquals(JsonUtil.toJson(RESPONSE_DTO), JsonUtil.toJson(response));
    }

    @Test
    @DisplayName("HTTP 500 Json request and response")
    void httpJson500() throws JsonConversionException {
        BaseRequest requestBody = new BaseRequest()
                .withContext(new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTC()));
        BaseResponse response = processor500.postJson(requestBody, BaseResponse.class, "entityIdJson500");
        Assertions.assertEquals(JsonUtil.toJson(RESPONSE_DTO), JsonUtil.toJson(response));
    }
}

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

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
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
import hu.icellmobilsoft.coffee.tool.utils.marshalling.MarshallingUtil;
import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.RestProcessor;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.impl.ConfigurableResponseProcessor;
import hu.icellmobilsoft.roaster.testsuite.jaxb.dto.DtoHelper;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Starts a mockserver with testcontainers to validate the jaxb calls rest calls
 *
 * @author imre.scheffer
 * @since 0.8.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Test of jaxb client POST multipart")
class JaxbPostMultipartIT extends BaseWeldUnitType {

    @Inject
    @RestProcessor(configKey = "testsuite.rest.test")
    private ConfigurableResponseProcessor<String> processor;

    @Inject
    @RestProcessor(configKey = "testsuite.rest.test", expectedStatusCode = 500)
    private ConfigurableResponseProcessor<String> processor500;

    private static final MockServerContainer MOCK_SERVER = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.14.0"));

    private static BaseResponse RESPONSE_DTO = new BaseResponse()
            .withContext(new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTC())).withFuncCode(FunctionCodeType.OK);

    @BeforeAll
    static void beforeAll() throws JsonConversionException {
        MOCK_SERVER.start();

        // microprofile-config settings
        System.setProperty("roaster.tm4j.server/mp-rest/url", MOCK_SERVER.getEndpoint());

        // We set the expectations in the MockServerContainer
        MockServerClient expectation = new MockServerClient(MOCK_SERVER.getHost(), MOCK_SERVER.getServerPort());
        String jsonResponse = JsonUtil.toJson(RESPONSE_DTO);
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/jsonEntityId"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(jsonResponse, MediaType.APPLICATION_JSON_UTF_8));
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/jsonEntityId500"))
                .respond(HttpResponse.response().withStatusCode(500).withBody(jsonResponse, MediaType.APPLICATION_JSON_UTF_8));
        String xmlResponse = MarshallingUtil.marshall(RESPONSE_DTO);
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/xmlEntityId"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(xmlResponse, MediaType.APPLICATION_XML_UTF_8));
        expectation.when(HttpRequest.request().withPath("/rest/testService/test/xmlEntityId500"))
                .respond(HttpResponse.response().withStatusCode(500).withBody(xmlResponse, MediaType.APPLICATION_XML_UTF_8));

        System.setProperty("example-project.example-service.url", MOCK_SERVER.getEndpoint());
    }

    @AfterAll
    static void afterAll() {
        MOCK_SERVER.close();
    }

    @Test
    @DisplayName("HTTP 200 Json response")
    void http200json() {
        MultipartFormDataOutput multipartbody = new MultipartFormDataOutput();
        multipartbody.addFormData("part1", "part1Body", jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
        multipartbody.addFormData("part2", "part2Body", jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
        String response = processor.postMultipartJson(multipartbody, String.class, "jsonEntityId");
        Assertions.assertEquals(JsonUtil.toJsonOpt(RESPONSE_DTO).orElse(null), response);
    }

    @Test
    @DisplayName("HTTP 200 Xml response")
    void http200xml() {
        MultipartFormDataOutput multipartbody = new MultipartFormDataOutput();
        BaseRequest part1Dto = DtoHelper.createBaseRequest();
        multipartbody.addFormData("part1", MarshallingUtil.marshall(part1Dto), jakarta.ws.rs.core.MediaType.APPLICATION_XML_TYPE);
        multipartbody.addFormData("part2", "part2Body", jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
        String response = processor.postMultipartXml(multipartbody, String.class, "xmlEntityId");
        Assertions.assertEquals(MarshallingUtil.marshall(RESPONSE_DTO), response);
    }

    @Test
    @DisplayName("HTTP 500 Json response")
    void http500json() {
        MultipartFormDataOutput multipartbody = new MultipartFormDataOutput();
        multipartbody.addFormData("part1", "part1Body", jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
        multipartbody.addFormData("part2", "part2Body", jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
        String response = processor500.postMultipartJson(multipartbody, String.class, "jsonEntityId500");
        Assertions.assertEquals(JsonUtil.toJsonOpt(RESPONSE_DTO).orElse(null), response);
    }

    @Test
    @DisplayName("HTTP 500 Xml response")
    void http500xml() {
        MultipartFormDataOutput multipartbody = new MultipartFormDataOutput();
        BaseRequest part1Dto = new BaseRequest();
        part1Dto.withContext(new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTC()));
        multipartbody.addFormData("part1", MarshallingUtil.marshall(part1Dto), jakarta.ws.rs.core.MediaType.APPLICATION_XML_TYPE);
        multipartbody.addFormData("part2", "part2Body", jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE);
        String response = processor500.postMultipartXml(multipartbody, String.class, "xmlEntityId500");
        Assertions.assertEquals(MarshallingUtil.marshall(RESPONSE_DTO), response);
    }
}

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
package hu.icellmobilsoft.roaster.jaxrs.se.response;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter;

import hu.icellmobilsoft.coffee.module.mp.restclient.provider.DefaultBaseExceptionResponseExceptionMapper;
import hu.icellmobilsoft.roaster.api.TestException;

/**
 * Base Response REST handler based on JXRS
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author imre.scheffer
 * @since 0.8.0
 */
public abstract class ResponseProcessor<RESPONSE> {

    private final List<Object> filters;

    /**
     * Creates a new {@link ResponseProcessor} instance with no request filters.
     */
    public ResponseProcessor() {
        this(List.of());
    }

    /**
     * Creates a new {@link ResponseProcessor} instance with the given request filters.
     * 
     * @param filters
     *            Request filter objects. See: {@link Configurable#register(Object)}
     */
    public ResponseProcessor(List<Object> filters) {
        this.filters = Objects.requireNonNull(filters, "filters");
    }

    /**
     * Base URI config key
     *
     * @return config key like "project.service.base.uri"
     */
    public abstract String baseUriKey();

    /**
     * Get value by {@link #baseUriKey()} from microprofile config
     *
     * @return value like "http://localhost:8080"
     */
    public String baseUri() {
        return ConfigProvider.getConfig().getValue(baseUriKey(), String.class);
    }

    /**
     * HTTP path to call
     *
     * @return value like "/test/service/generate/testData"
     */
    public abstract String path();

    /**
     * Expected REST response status code<br>
     * <br>
     * Default value is <strong>{@value Status#OK}</strong>
     */
    private int expectedStatusCode = Status.OK.getStatusCode();

    /**
     * Call and get JSON object from HTTP GET method
     *
     * @param responseClass
     *            response class
     * @param pathParams
     *            The path parameters.
     * @return response object cast to responseClass
     */
    public RESPONSE getOctetStream(Class<RESPONSE> responseClass, Object... pathParams) {
        // REST obtaining client
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(UriBuilder.fromUri(baseUri() + path()).build(pathParams));
        target.register(MultipartFormDataWriter.class);
        filters.forEach(target::register);
        // GET kuldese
        Invocation.Builder builder = target.request().accept(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        builder = clientBuilderCustomization(builder);
        Response response = builder.get();
        return processResponse(response, responseClass);
    }

    /**
     * Http JAXRS rest client response customization
     * 
     * @param response
     *            JAXRS rest client response
     * @param responseClass
     *            expected response class
     * @return Parsed response entity
     */
    protected RESPONSE processResponse(Response response, Class<RESPONSE> responseClass) {
        if (response.getStatus() == expectedStatusCode) {
            return response.readEntity(responseClass);
        } else {
            throw new TestException(
                    MessageFormat.format("Expected handled response status is [{0}], but real is [{1}]", expectedStatusCode, response.getStatus()));
        }
    }

    /**
     * Call and get JSON object from HTTP POST method
     *
     * @param requestMultipartForm
     *            request DTO object
     * @param responseClass
     *            response class
     * @param pathParams
     *            The path parameters.
     * @return response object cast to responseClass
     */
    public RESPONSE postMultipartJson(MultipartFormDataOutput requestMultipartForm, Class<RESPONSE> responseClass, Object... pathParams) {
        return postMultipart(requestMultipartForm, responseClass, MediaType.MULTIPART_FORM_DATA_TYPE, pathParams);
    }

    /**
     * Call and get XML object from HTTP POST method
     *
     * @param requestMultipartForm
     *            request DTO object
     * @param responseClass
     *            response class
     * @param pathParams
     *            The path parameters
     * @return Readed http body entity cast to responseClass
     */
    public RESPONSE postMultipartXml(MultipartFormDataOutput requestMultipartForm, Class<RESPONSE> responseClass, Object... pathParams) {
        return postMultipart(requestMultipartForm, responseClass, MediaType.APPLICATION_XML_TYPE, pathParams);
    }

    /**
     * Call and post HTTP multipart http request
     * 
     * @param requestMultipartForm
     *            request DTO object
     * @param responseClass
     *            response class
     * @param responseMediaType
     *            response media type
     * @param pathParams
     *            The path parameters
     * @return Readed http body entity cast to responseClass
     */
    protected RESPONSE postMultipart(MultipartFormDataOutput requestMultipartForm, Class<RESPONSE> responseClass, MediaType responseMediaType,
            Object... pathParams) {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        UriBuilder uriBuilder = UriBuilder.fromUri(baseUri() + path());
        uriBuilder = uriBuilderCustomization(uriBuilder);
        ResteasyWebTarget target = client.target(uriBuilder.build(pathParams));
        target.register(MultipartFormDataWriter.class);
        target.register(DefaultBaseExceptionResponseExceptionMapper.class);
        filters.forEach(target::register);
        // basic settings
        Invocation.Builder builder = target.request().accept(responseMediaType);
        builder = clientBuilderCustomization(builder);
        // Sending POST
        Response response = builder.post(Entity.entity(requestMultipartForm, MediaType.MULTIPART_FORM_DATA_TYPE));
        return processResponse(response, responseClass);
    }

    /**
     * Customization on JAXRS client builder before invoke get/post/put/... method
     * 
     * @param clientBuilder
     *            client builder
     * @return clien builder
     */
    protected Invocation.Builder clientBuilderCustomization(Invocation.Builder clientBuilder) {
        return clientBuilder;
    }

    /**
     * Customization on URI before using on JAXRS client
     * 
     * @param uriBuilder
     *            uri builder
     * @return uri builder
     */
    protected UriBuilder uriBuilderCustomization(UriBuilder uriBuilder) {
        return uriBuilder;
    }

    /**
     * Getting expected REST response status code
     * 
     * @return Default value is {@code Status#OK}
     */
    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    /**
     * Setting expected REST response status code
     * 
     * @param expectedStatusCode
     *            default value is {@code Status#OK}
     */
    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }
}

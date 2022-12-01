/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.restassured.response.producer.spi;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.ResponseProcessorConfig;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.RestProcessor;
import hu.icellmobilsoft.roaster.restassured.response.ResponseProcessor;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Configurable {@link ResponseProcessor}
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author martin.nagy
 * @since 0.5.0
 */
public abstract class AbstractConfigurableResponseProcessor<RESPONSE> extends ResponseProcessor<RESPONSE> {
    private static final Pattern HEADER_PATTERN = Pattern.compile("\\s*([^\\s:]+)\\s*:\\s*([^\\s:]+)\\s*");

    /**
     * Base URI config key<br>
     * Populated by {@link #setConfig}
     */
    private String baseUriKey;

    /**
     * HTTP path to call. E.g.: {@literal /foo/bar} if the URL is {@literal http://localhost/foo/bar}<br>
     * Populated by {@link #setConfig}
     */
    private String path;

    /**
     * Optional HTTP request headers<br>
     * Populated by {@link #setConfig}
     *
     * @see RequestSpecification#headers(Headers)
     */
    private Headers headers;

    /**
     * Expected REST response status code<br>
     * Populated by {@link RestProcessor#expectedStatusCode()}
     *
     * @see ResponseSpecification#statusCode(int)
     */
    private int expectedStatusCode;

    /**
     * Optional HTTP request query params<br>
     *
     * @see RequestSpecification#queryParams(Map)
     */
    private Map<String, String> queryParams;

    /**
     * Initializes the configurable values
     *
     * @param config
     *            Configuration class populated with microprofile config valuest
     * @throws BaseException
     *             exception
     */
    public void setConfig(ResponseProcessorConfig config) throws BaseException {
        if (Objects.isNull(config)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, "The config is null!");
        }

        baseUriKey = config.getBaseUriKey();
        path = config.getPath();

        Optional<String[]> headersOpt = config.getHeaders();
        headers = headersOpt.isPresent() ? parse(headersOpt.get()) : null;
    }

    private Headers parse(String[] headerStrings) throws BaseException {
        List<Header> headerList = new ArrayList<>();
        for (String headerString : headerStrings) {
            Matcher matcher = HEADER_PATTERN.matcher(headerString);
            if (!matcher.matches()) {
                throw new BaseException(CoffeeFaultType.INVALID_INPUT, MessageFormat.format("Invalid header: [{0}]", headerString));
            }
            headerList.add(new Header(matcher.group(1), matcher.group(2)));
        }
        return new Headers(headerList);
    }

    @Override
    protected RequestSpecification createRequestSpecification(RequestSpecification initRequestSpecification) {
        RequestSpecification requestSpecification = super.createRequestSpecification(initRequestSpecification);
        if (headers != null) {
            requestSpecification.headers(headers);
        }
        if (queryParams != null) {
            requestSpecification.queryParams(queryParams);
        }
        return requestSpecification;
    }

    @Override
    protected RESPONSE toResponse(Response response, Class<RESPONSE> responseClass, ResponseSpecification iniResponseSpecification) {
        iniResponseSpecification.statusCode(expectedStatusCode);
        return super.toResponse(response, responseClass, iniResponseSpecification);
    }

    @Override
    public String baseUriKey() {
        return baseUriKey;
    }

    @Override
    public String path() {
        return path;
    }

    /**
     * Sets the new expectedStatusCode value
     *
     * @param expectedStatusCode
     *            new expectedStatusCode value
     */
    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    /**
     * Getter for the field {@code expectedStatusCode}.
     *
     * @return expectedStatusCode
     */
    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    /**
     * Sets the new queryParams value
     *
     * @param queryParams
     *            new queryParams value
     */
    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * Getter for the field {@code queryParams}.
     *
     * @return queryParams
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}

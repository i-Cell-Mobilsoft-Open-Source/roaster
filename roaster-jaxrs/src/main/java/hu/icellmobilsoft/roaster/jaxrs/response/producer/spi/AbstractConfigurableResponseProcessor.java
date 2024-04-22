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
package hu.icellmobilsoft.roaster.jaxrs.response.producer.spi;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriBuilder;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.roaster.jaxrs.response.ResponseProcessor;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.ResponseProcessorConfig;

/**
 * Configurable {@link ResponseProcessor}
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author imre.scheffer
 * @since 0.8.0
 */
public class AbstractConfigurableResponseProcessor<RESPONSE> extends ResponseProcessor<RESPONSE> {

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
     */
    private MultivaluedMap<String, Object> headers;

    /**
     * Optional HTTP request query params
     */
    private Map<String, String> queryParams;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractConfigurableResponseProcessor() {
        super();
    }

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
        headers = headersOpt.isPresent() ? parse(headersOpt.get()) : new MultivaluedHashMap<>();
    }

    private MultivaluedMap<String, Object> parse(String[] headerStrings) throws BaseException {
        MultivaluedMap<String, Object> headerMap = new MultivaluedHashMap<>();
        for (String headerString : headerStrings) {
            Matcher matcher = HEADER_PATTERN.matcher(headerString);
            if (!matcher.matches()) {
                throw new BaseException(CoffeeFaultType.INVALID_INPUT, MessageFormat.format("Invalid header: [{0}]", headerString));
            }
            headerMap.add(matcher.group(1), matcher.group(2));
        }
        return headerMap;
    }

    @Override
    protected UriBuilder uriBuilderCustomization(UriBuilder uriBuilder) {
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return uriBuilder;
    }

    @Override
    protected Builder clientBuilderCustomization(Builder clientBuilder) {
        // beallitjuk a headereket egyenkent, nem irjuk felul a benne levoket
        headers.forEach(clientBuilder::header);
        return clientBuilder;
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

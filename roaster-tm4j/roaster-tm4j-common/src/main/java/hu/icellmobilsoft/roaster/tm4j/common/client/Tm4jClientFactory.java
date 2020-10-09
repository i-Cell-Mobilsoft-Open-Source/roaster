/*-
 * #%L
 * Coffee
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
package hu.icellmobilsoft.roaster.tm4j.common.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hu.icellmobilsoft.roaster.tm4j.common.config.InvalidConfigException;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterServerConfig;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating {@code Tm4jClient} implementation.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
class Tm4jClientFactory {

    /**
     * Creates a {@code Tm4jClient} Retrofit proxy implementation based on the configuration parameter.
     *
     * @param config configuration used for creating the rest client used for the Tm4jClient implementation
     * @return {@code Tm4jClient} implementation
     */
    Tm4jClient createClient(Tm4jReporterServerConfig config) {
        Objects.requireNonNull(config);
        validateConfig(config);

        return new Retrofit.Builder()
                .baseUrl(getBaseUrl(config))
                .addConverterFactory(createConverterFactory())
                .client(createHttpClient(config))
                .build()
                .create(Tm4jClient.class);
    }

    private void validateConfig(Tm4jReporterServerConfig config) {
        if (config.getBaseUrl() == null) {
            throw new InvalidConfigException("url parameter is missing");
        }
        if (config.getBasicAuthToken() == null && (config.getUserName() == null || config.getPassword() == null)) {
            throw new InvalidConfigException("userName and password should be set if basicAuthToken is missing");
        }
        if (config.getBasicAuthToken() != null && (config.getUserName() != null || config.getPassword() != null)) {
            throw new InvalidConfigException("userName and password should be empty if basicAuthToken is supplied");
        }
    }

    private JacksonConverterFactory createConverterFactory() {
        return JacksonConverterFactory.create(
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        );
    }

    private OkHttpClient createHttpClient(Tm4jReporterServerConfig config) {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> addAuthHeader(chain, config))
                .build();
    }

    private String getBaseUrl(Tm4jReporterServerConfig config) {
        return (
                config.getBaseUrl().endsWith("/") ?
                        config.getBaseUrl() :
                        config.getBaseUrl() + "/"
        ) + "rest/atm/1.0/";
    }

    private Response addAuthHeader(Interceptor.Chain chain, Tm4jReporterServerConfig config) throws IOException {
        return chain.proceed(
                chain.request().newBuilder()
                        .addHeader("Authorization", getAuthHeader(config))
                        .build()
        );
    }

    private String getAuthHeader(Tm4jReporterServerConfig config) {
        return config.getBasicAuthToken() != null && !config.getBasicAuthToken().isBlank() ?
                "Basic " + config.getBasicAuthToken() :
                Credentials.basic(config.getUserName(), config.getPassword());
    }

}

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

import hu.icellmobilsoft.roaster.tm4j.common.client.model.Execution;
import hu.icellmobilsoft.roaster.tm4j.common.config.Tm4jReporterServerConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 * Class for handling the TM4J client calls
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public class Tm4jService {
    private final Tm4jClient tm4jClient;

    /**
     * Creates an instance using the given configuration and the default TM4J client
     *
     * @param config configuration used for creating the TM4J client
     */
    public Tm4jService(Tm4jReporterServerConfig config) {
        this(new Tm4jClientFactory().createClient(config));
    }

    /**
     * Creates an instance using the given TM4J client
     *
     * @param tm4jClient TM4J client used for rest calls
     */
    public Tm4jService(Tm4jClient tm4jClient) {
        this.tm4jClient = Objects.requireNonNull(tm4jClient);
    }

    /**
     * Returns {@code true} if the test run exists with the given key on the server
     *
     * @param key test run key used at the search on the server
     * @return {@code true} if the test run exists with the given key on the server
     */
    public boolean isTestRunExist(String key) {
        Objects.requireNonNull(key);
        try {
            int code = tm4jClient.getTestRun(key).execute().code();
            return isEntityExistsBasedOnHttpResponseCode(code);
        } catch (IOException e) {
            throw new Tm4jClientException(e);
        }
    }

    /**
     * Returns {@code true} if the test case exists with the given key on the server
     *
     * @param key test case key used at the search on the server
     * @return {@code true} if the test case exists with the given key on the server
     */
    public boolean isTestCaseExist(String key) {
        Objects.requireNonNull(key);
        try {
            int code = tm4jClient.getTestCase(key).execute().code();
            return isEntityExistsBasedOnHttpResponseCode(code);
        } catch (IOException e) {
            throw new Tm4jClientException(e);
        }
    }

    /**
     * Posts the execution data to the server with the given test run key
     *
     * @param testRunKey the test run key
     * @param execution the {@code Execution} to be published
     */
    public void postResult(String testRunKey, Execution execution) {
        Objects.requireNonNull(testRunKey);
        Objects.requireNonNull(execution);
        try {
            tm4jClient.postExecutionsForTestRun(testRunKey, Collections.singletonList(execution)).execute();
        } catch (IOException e) {
            throw new Tm4jClientException(e);
        }
    }

    private boolean isEntityExistsBasedOnHttpResponseCode(int code) {
        if (code >= 200 && code < 300) {
            return true;
        }
        if (code == 404) {
            return false;
        }
        throw new Tm4jClientException("Rest endpoint responded with: " + code);
    }
}

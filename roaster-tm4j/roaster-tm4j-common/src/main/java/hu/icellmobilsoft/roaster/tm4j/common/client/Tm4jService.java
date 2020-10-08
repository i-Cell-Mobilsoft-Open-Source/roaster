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

/**
 * TODO
 * https://support.smartbear.com/tm4j-server/api-docs/v1/
 */
public class Tm4jService {
    private final Tm4jClient tm4jClient;

    public Tm4jService(Tm4jReporterServerConfig config) {
        tm4jClient = new Tm4jClientFactory().createClient(config);
    }

    public boolean isTestRunExist(String key) {
        try {
            int code = tm4jClient.getTestRun(key).execute().code();
            return isEntityExistsBasedOnHttpResponseCode(code);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    public boolean isTestCaseExist(String key) {
        try {
            int code = tm4jClient.getTestCase(key).execute().code();
            return isEntityExistsBasedOnHttpResponseCode(code);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    public void postResult(String testRunKey, Execution execution) {
        try {
            tm4jClient.postExecutionsForTestRun(testRunKey, Collections.singletonList(execution)).execute();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private boolean isEntityExistsBasedOnHttpResponseCode(int code) {
        if (code >= 200 && code < 300) {
            return true;
        }
        if (code == 404) {
            return false;
        }
        throw new ClientException("Rest endpoint responded with: " + code);
    }
}

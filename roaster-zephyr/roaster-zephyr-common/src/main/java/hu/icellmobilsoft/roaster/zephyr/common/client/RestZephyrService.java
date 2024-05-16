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
package hu.icellmobilsoft.roaster.zephyr.common.client;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.icellmobilsoft.roaster.zephyr.common.client.api.JiraRestClient;
import hu.icellmobilsoft.roaster.zephyr.common.client.api.ZephyrRestClient;
import hu.icellmobilsoft.roaster.zephyr.common.config.IJiraReporterServerConfig;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.Execution;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.TestSteps;

/**
 * Class for handling the Zephyr Cloud client calls
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@ApplicationScoped
public class RestZephyrService {

    @Inject
    @RestClient
    private ZephyrRestClient zephyrClient;

    @Inject
    @RestClient
    private JiraRestClient jiraClient;

    @Inject
    private IJiraReporterServerConfig serverConfig;

    private static final Set<String> existingTestCycleKeys = new HashSet<>();

    private static final Map<String, String> accountIdsByUserName = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public RestZephyrService() {
        super();
    }

    /**
     * Returns {@code true} if the test cycle exists with the given key on the server
     *
     * @param key
     *            test cycle key used at the search on the server
     * @return {@code true} if the test run exists with the given key on the server
     */
    public boolean isTestCycleExist(String key) {
        Objects.requireNonNull(key);

        if (existingTestCycleKeys.contains(key)) {
            return true;
        }

        Response response = zephyrClient.getTestCycle(key);
        boolean exists = isEntityExistsBasedOnResponseStatus(response.getStatusInfo());
        if (exists) {
            existingTestCycleKeys.add(key);
        }
        return exists;
    }

    /**
     * Returns {@code true} if the test case exists with the given key on the server
     *
     * @param key
     *            test case key used at the search on the server
     * @return {@code true} if the test case exists with the given key on the server
     */
    public boolean isTestCaseExist(String key) {
        Response response = zephyrClient.getTestCase(Objects.requireNonNull(key));
        return isEntityExistsBasedOnResponseStatus(response.getStatusInfo());
    }

    /**
     * Returns number of test steps from the test case with the given key on the server
     *
     * @param key
     *            test case key used at the search on the server
     * @return number of test steps from the test case with the given key on the server
     */
    public int numberOfTestSteps(String key) {
        Response response = zephyrClient.getTestCaseSteps(Objects.requireNonNull(key));
        ObjectMapper objectMapper = new ObjectMapper();
        TestSteps testSteps = null;
        try {
            testSteps = objectMapper.readValue(response.readEntity(String.class), TestSteps.class);
            BigInteger total = testSteps.getTotal();
            if (total != null) {
                return total.intValue();
            } else {
                // required field in response
                return testSteps.getMaxResults().intValue();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Posts the test execution data to the server with the given test run key
     *
     * @param execution
     *            the {@code ZephyrExecution} to be published
     */
    public void postResult(Execution execution) {
        Objects.requireNonNull(execution);

        zephyrClient.postExecution(execution);
    }

    /**
     * Returns the account id of the current configured jira user
     *
     * @return the account id of the current configured jira user
     */
    public String getAccountId() {
        return accountIdsByUserName.computeIfAbsent(serverConfig.getEmail(), k -> jiraClient.getSelf().getAccountId());
    }

    private boolean isEntityExistsBasedOnResponseStatus(Response.StatusType statusType) {
        if (statusType.getFamily() == Response.Status.Family.SUCCESSFUL) {
            return true;
        }
        if (statusType.toEnum() == Response.Status.NOT_FOUND) {
            return false;
        }
        throw new ZephyrClientException("Rest endpoint responded with: " + statusType);
    }
}

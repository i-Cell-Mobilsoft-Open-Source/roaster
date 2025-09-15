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

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderResolver;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import hu.icellmobilsoft.roaster.zephyr.common.client.api.JiraRestClient;
import hu.icellmobilsoft.roaster.zephyr.common.client.api.ZephyrRestClient;
import hu.icellmobilsoft.roaster.zephyr.common.config.IJiraReporterServerConfig;
import hu.icellmobilsoft.roaster.zephyr.common.config.IZephyrReporterConfig;
import hu.icellmobilsoft.roaster.zephyr.common.config.JiraReporterServerConfig;
import hu.icellmobilsoft.roaster.zephyr.common.config.ZephyrReporterConfig;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.Execution;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.TestSteps;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.ValueType;

/**
 * Class for handling the Zephyr Cloud client calls
 *
 * @author mark.vituska
 * @since 0.11.0
 */
public class RestZephyrService {

    private final ZephyrRestClient zephyrClient = RestClientBuilderResolver.instance()
            .newBuilder()
            .baseUri(
                    ConfigProvider.getConfig()
                            .getOptionalValue("ZephyrRestClient/mp-rest/url", URI.class)
                            .orElseGet(() -> URI.create("https://api.zephyrscale.smartbear.com/v2")))
            .followRedirects(true)
            .build(ZephyrRestClient.class);

    private final JiraRestClient jiraClient = RestClientBuilderResolver.instance()
            .newBuilder()
            .baseUri(ConfigProvider.getConfig().getValue("roaster.zephyr.server/mp-rest/url", URI.class))
            .followRedirects(true)
            .build(JiraRestClient.class);

    private final IJiraReporterServerConfig serverConfig = new JiraReporterServerConfig();
    private final IZephyrReporterConfig zephyrConfig = new ZephyrReporterConfig();

    private static final Set<String> existingTestCycleKeys = Sets.newConcurrentHashSet();
    private static final Map<String, String> accountIdsByUserName = Maps.newConcurrentMap();

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
     * @param depth
     *            actual depth of the test case structure
     * @return number of test steps from the test case with the given key on the server
     */
    public int numberOfTestSteps(String key, int depth) {
        if (!zephyrConfig.isTestStepsEnabled()) {
            return 0;
        }
        if (depth > zephyrConfig.getDefaultTestStepsTestCaseDepth()) {
            throw new ZephyrClientException("Maximum test case depth reached: " + zephyrConfig.getDefaultTestStepsTestCaseDepth());
        }
        TestSteps testSteps = zephyrClient.getTestCaseSteps(Objects.requireNonNull(key), zephyrConfig.getDefaultTestStepsMaxResults());
        int numberOfTestSteps = 0;
        for (ValueType value : testSteps.getValues()) {
            if (value.isSetTestCase()) {
                numberOfTestSteps += numberOfTestSteps(value.getTestCase().getTestCaseKey(), depth + 1);
            } else {
                numberOfTestSteps += 1;
            }
        }
        return numberOfTestSteps;
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

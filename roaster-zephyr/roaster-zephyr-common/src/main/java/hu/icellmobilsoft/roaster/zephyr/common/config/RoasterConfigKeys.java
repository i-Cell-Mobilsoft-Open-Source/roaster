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
package hu.icellmobilsoft.roaster.zephyr.common.config;

/**
 * Common class for Roaster config keys
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public interface RoasterConfigKeys {

    /**
     * The reporting (and rest calls) can be turned off using this flag
     */
    String ENABLED = "roaster.zephyr.enabled";

    /**
     *  The project key. This is the prefix for the Jira issues also.
     */
    String PROJECT_KEY = "roaster.zephyr.projectKey";

    /**
     * TM4J test cycle key. E.g. {@literal ABC-C1} where ABC is the project key.
     */
    String TEST_CYCLE_KEY = "roaster.zephyr.testCycleKey";

    /**
     * TM4J test case depth key.
     */
    String TEST_STEPS_TEST_CASE_DEPTH_KEY = "roaster.zephyr.testSteps.testCaseDepth";

    /**
     * TM4J max results key.
     */
    String TEST_STEPS_MAX_RESULTS_KEY = "roaster.zephyr.testSteps.maxResults";

    /**
     * TM4J test cycle key pattern for test tags
     */
    String TAG_TEST_CYCLE_KEY_PATTERN = "roaster.zephyr.cycle.{0}";

    /**
     * (Optional) The name of the current environment where the tests are running.
     * It will appear in the test result comment.
     */
    String ENVIRONMENT = "roaster.zephyr.environment";

    /**
     * Keys for configuring the Zephyr Cloud environment.
     */
    interface Cloud {
        /**
         * The token used for authentication with the Zephyr Cloud environment.
         * <a href="https://support.smartbear.com/zephyr-scale-cloud/docs/rest-api/generating-api-access-tokens.html">Zephyr Scale Access Token</a>
         */
        String BEARER_TOKEN = "roaster.zephyr.cloud/bearerToken";
    }

    /**
     * Keys for configuring the TM4J server
     */
    interface Server {
        /**
         * Jira server api token.
         * <a href="https://support.atlassian.com/atlassian-account/docs/manage-api-tokens-for-your-atlassian-account/">Atlassian Api Tokens</a><br>
         */
        String API_TOKEN = "roaster.zephyr.server/apiToken";
        /**
         * Token used for authentication with the Jira server.
         * To create it use the following formula: <small>{@code base64(userName + ":" + apiToken)}</small>
         * <a href="https://tools.ietf.org/html/rfc2617">RFC 2617</a><br>
         * To set the credentials the authToken or the userName + apiToken can be used (not both).
         */
        String AUTH_TOKEN = "roaster.zephyr.server/authToken";

        /**
         * Atlassian account e-mail address.
         */
        String EMAIL = "roaster.zephyr.server/email";
    }
}

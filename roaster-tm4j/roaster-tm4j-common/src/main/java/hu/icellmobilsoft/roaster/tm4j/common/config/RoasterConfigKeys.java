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
package hu.icellmobilsoft.roaster.tm4j.common.config;

/**
 * Common class for Roaster config keys
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public interface RoasterConfigKeys {

    /**
     * The reporting (and rest calls) can be turned of using this flag
     */
    String ENABLED = "roaster.tm4j.enabled";

    /**
     *  The project key. This is the prefix for the Jira issues also.
     */
    String PROJECT_KEY = "roaster.tm4j.projectKey";

    /**
     * TM4J test cycle key. E.g. {@literal ABC-C1} where ABC is the project key.
     */
    String TEST_CYCLE_KEY = "roaster.tm4j.testCycleKey";

    /**
     * (Optional) The name of the current environment where the tests are running.
     * It will appear in the test result comment.
     * Default: {@literal N/A}
     */
    String ENVIRONMENT = "roaster.tm4j.environment";

    /**
     * Keys for configuring the TM4J server
     */
    interface Server {
        /**
         * The token used for basic authentication for the TM4J server.
         * To create it use the following formula: <small>{@code base64(userName + ":" + password)}</small>
         * <a href="https://tools.ietf.org/html/rfc2617">RFC 2617</a><br>
         * To set the credentials the basicAuthToken or the userName + password can be used (not both).
         */
        String BASIC_AUTH_TOKEN = "roaster.tm4j.server.basicAuthToken";

        /**
         * TM4J server user name
         */
        String USER_NAME = "roaster.tm4j.server.userName";

        /**
         * TM4J server user password
         */
        String PASSWORD = "roaster.tm4j.server.password";
    }
}

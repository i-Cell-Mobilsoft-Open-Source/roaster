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

/**
 * The common TM4J server handling logic is located here.
 * <br><br>
 * Example for this module yaml configuration:
 * <pre>{@code
 * roaster:
 *    tm4j:
 *       enabled: true    # The reporting can be turned on using this flag (default: false)
 *       projectKey: ABC      # The project key. This is the prefix for the Jira issues also
 *       testCycleKey: ABC-C1   # TM4J test cycle key. E.g. ABC-C1 where ABC is the project key.
 *       environment: sandbox    # (Optional) The name of the current environment where the tests are running (default: N/A)
 *       server/mp-rest/url: https://jira.example.com   # TM4J server url
 *       server/basicAuthToken: ZXhhbXBsZS11c2VyOnNlY3JldA==       # base64(userName + ":" + password)
 *       server/userName: # To set the credentials the basicAuthToken or the userName + password can be used (not both)
 *       server/password:
 * }</pre>
 *
 * For security reasons it's recommended to set the password with command line arguments.
 * (e.g. using maven: {@code mvn test -Droaster.tm4j.server.password=secret})
 *
 * @author martin.nagy
 * @since 0.2.0
 */
package hu.icellmobilsoft.roaster.tm4j.common;

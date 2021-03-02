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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.common.base.Strings;

/**
 * Configuration class defining the TM4J server access parameters.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
public class Tm4jReporterServerConfig {
    private String userName;
    private String password;
    private String basicAuthToken;

    /**
     * Calculates the user name using the configuration
     * 
     * @return the user name
     */
    public String calculateUserName() {
        return !Strings.isNullOrEmpty(userName) ? //
                userName : //
                new String(Base64.getDecoder().decode(basicAuthToken)).split(":")[0];
    }

    /**
     * Calculates the basic auth token using the configuration
     * 
     * @return the basic auth token
     */
    public String calculateBasicAuthToken() {
        return !Strings.isNullOrEmpty(basicAuthToken) ? //
                basicAuthToken : //
                Base64.getEncoder().encodeToString((userName + ':' + password).getBytes(StandardCharsets.UTF_8));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBasicAuthToken() {
        return basicAuthToken;
    }

    public void setBasicAuthToken(String basicAuthToken) {
        this.basicAuthToken = basicAuthToken;
    }
}

/*-
 * #%L
 * Roaster
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
package hu.icellmobilsoft.roaster.restassured.helper;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import io.restassured.specification.RequestSpecification;
import jakarta.enterprise.inject.spi.CDI;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * JSON RequestSpecification helper
 * 
 * @author tamas.cserhati
 *
 */
public class JSONSpecificationHelper {

    private static final String HEADER_LOGIN = "login";
    private static final String HEADER_SESSION_TOKEN = "sessionToken";

    /**
     * Default constructor, constructs a new object.
     */
    public JSONSpecificationHelper() {
        super();
    }

    /**
     * gets the default json request specification and adds the serviceBaseUri
     * 
     * @param serviceBaseUri
     *            base uri
     * @return restassured request spec
     */
    public static RequestSpecification defaultRequestSpecification(String serviceBaseUri) {
        RequestSpecification requestSpecification = CDI.current().select(RequestSpecification.class, new JSON.Literal()).get();
        requestSpecification.baseUri(serviceBaseUri);
        return requestSpecification;
    }

    /**
     * adds session headers to the request
     * 
     * @param serviceBaseUri
     *            base uri
     * @param login
     *            login value
     * @param sessionToken
     *            session token value
     * @return restassured request spec
     */
    public static RequestSpecification loggedinRequestSpecification(String serviceBaseUri, String login, String sessionToken) {
        RequestSpecification requestSpecification = defaultRequestSpecification(serviceBaseUri);
        if (StringUtils.isAllBlank(login, sessionToken)) {
            return requestSpecification;
        }

        requestSpecification.header(HEADER_LOGIN, login);
        requestSpecification.header(HEADER_SESSION_TOKEN, sessionToken);
        return requestSpecification;
    }
}

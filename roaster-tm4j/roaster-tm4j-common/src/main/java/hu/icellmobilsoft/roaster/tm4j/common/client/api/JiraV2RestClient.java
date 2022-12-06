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
package hu.icellmobilsoft.roaster.tm4j.common.client.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hu.icellmobilsoft.roaster.tm4j.common.client.AuthHeadersFactory;
import hu.icellmobilsoft.roaster.tm4j.common.client.Tm4jJsonProvider;
import hu.icellmobilsoft.roaster.tm4j.dto.domain.jira.User;

/**
 * Interface for microprofile rest client. <br>
 * <br>
 * For details see the <a href="https://docs.atlassian.com/software/jira/docs/api/REST/latest/">JIRA Server platform REST API reference</a>
 *
 * @author martin.nagy
 * @since 0.4.0
 */
@RegisterRestClient(configKey = "roaster.tm4j.server")
@RegisterProvider(Tm4jJsonProvider.class)
@RegisterClientHeaders(AuthHeadersFactory.class)
@Path("/rest/api/2")
public interface JiraV2RestClient {

    /**
     * Returns the data of the logged-in user
     * 
     * @return the data of the logged-in user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/myself")
    @JsonIgnoreProperties(ignoreUnknown = true)
    User getSelf();

}

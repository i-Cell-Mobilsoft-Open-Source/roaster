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
package hu.icellmobilsoft.roaster.zephyr.common.client.api;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

import hu.icellmobilsoft.roaster.zephyr.common.client.JiraAuthHeadersFactory;
import hu.icellmobilsoft.roaster.zephyr.common.client.ZephyrJsonbContextResolver;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.jira.User;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Interface for microprofile rest client. <br>
 * <br>
 * For details see the <a href="https://developer.atlassian.com/cloud/jira/platform/rest/v3/">JIRA Server platform REST API reference</a>
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@RegisterClientHeaders(JiraAuthHeadersFactory.class)
@RegisterProvider(ZephyrJsonbContextResolver.class)
@Path("/rest/api/3")
public interface JiraRestClient {

    /**
     * Returns the data of the logged-in user
     *
     * @return the data of the logged-in user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/myself")
    User getSelf();

}

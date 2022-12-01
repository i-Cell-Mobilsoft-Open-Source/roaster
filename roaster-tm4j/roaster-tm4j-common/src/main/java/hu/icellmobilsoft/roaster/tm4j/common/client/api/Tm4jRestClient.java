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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import hu.icellmobilsoft.roaster.tm4j.common.client.AuthHeadersFactory;
import hu.icellmobilsoft.roaster.tm4j.common.client.Tm4jJsonProvider;
import hu.icellmobilsoft.roaster.tm4j.dto.domain.test_execution.Execution;

/**
 * Interface for microprofile rest client. <br>
 * <br>
 * For details see the <a href="https://support.smartbear.com/tm4j-server/api-docs/">TM4J rest API documentation</a>
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@RegisterRestClient(configKey = "roaster.tm4j.server")
@RegisterProvider(Tm4jJsonProvider.class)
@RegisterClientHeaders(AuthHeadersFactory.class)
@Path("/rest/atm/1.0")
public interface Tm4jRestClient {

    /**
     * Checks if the given test case exists
     * 
     * @param testCaseKey
     *            test case key to find
     * @return response containing HTTP status {@literal 200} if the test case exists or {@literal 404} if not
     */
    @HEAD
    @Path("/testcase/{testCaseKey}")
    Response headTestCase(@PathParam("testCaseKey") String testCaseKey);

    /**
     * Checks if the given test run exists
     * 
     * @param testRunKey
     *            test run key to find
     * @return response containing HTTP status {@literal 200} if the test case exists or {@literal 404} if not
     */
    @HEAD
    @Path("/testrun/{testRunKey}")
    Response headTestRun(@PathParam("testRunKey") String testRunKey);

    /**
     * Submits the test run results to the given test run key
     * 
     * @param testRunKey
     *            test run key
     * @param executions
     *            test run results
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/testrun/{testRunKey}/testresults")
    void postExecutions(@PathParam("testRunKey") String testRunKey, List<Execution> executions);

}

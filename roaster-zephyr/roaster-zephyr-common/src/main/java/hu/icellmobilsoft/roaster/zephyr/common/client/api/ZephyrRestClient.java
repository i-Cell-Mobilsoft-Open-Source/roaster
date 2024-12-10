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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import hu.icellmobilsoft.roaster.zephyr.common.client.ZephyrAuthHeadersFactory;
import hu.icellmobilsoft.roaster.zephyr.common.client.ZephyrJsonbContextResolver;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.Execution;
import hu.icellmobilsoft.roaster.zephyr.dto.domain.test_execution.TestSteps;

/**
 * Interface for microprofile rest client. <br>
 * <br>
 * For details see the <a href="https://support.smartbear.com/zephyr-scale-cloud/api-docs/">Zephyr Cloud rest API documentation</a>
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@RegisterRestClient(baseUri = "https://api.zephyrscale.smartbear.com/v2")
@RegisterClientHeaders(ZephyrAuthHeadersFactory.class)
@RegisterProvider(ZephyrJsonbContextResolver.class)
public interface ZephyrRestClient {

    /**
     * Checks if the given test case exists
     *
     * @param testCaseKey
     *            test case key to find
     * @return response containing HTTP status {@literal 200} if the test case exists or {@literal 404} if not
     */
    @GET
    @Path("/testcases/{testCaseKey}")
    Response getTestCase(@PathParam("testCaseKey") String testCaseKey);

    /**
     * Return the test steps of the given test case
     *
     * @param testCaseKey
     *            test case key to find
     * @param maxResults
     *            maximum number of results to return
     * @return response containing HTTP status {@literal 200} if the test case exists or {@literal 404} if not
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/testcases/{testCaseKey}/teststeps")
    TestSteps getTestCaseSteps(@PathParam("testCaseKey") String testCaseKey, @QueryParam("maxResults") Integer maxResults);

    /**
     * Checks if the given test cycle exists
     *
     * @param testCycleKey
     *            test cycle key to find
     * @return response containing HTTP status {@literal 200} if the test case exists or {@literal 404} if not
     */
    @GET
    @Path("/testcycles/{testCycleKey}")
    Response getTestCycle(@PathParam("testCycleKey") String testCycleKey);

    /**
     * Submits the test run results
     *
     * @param execution
     *            test run results
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/testexecutions")
    void postExecution(Execution execution);

}

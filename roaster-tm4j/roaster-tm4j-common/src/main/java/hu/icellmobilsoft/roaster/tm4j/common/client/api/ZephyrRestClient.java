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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import hu.icellmobilsoft.roaster.tm4j.common.client.Tm4jJsonProvider;
import hu.icellmobilsoft.roaster.tm4j.common.client.ZephyrAuthHeadersFactory;
import hu.icellmobilsoft.roaster.tm4j.dto.domain.test_execution.ZephyrExecution;

/**
 * Interface for microprofile rest client.
 * <br><br>
 * For details see the <a href="https://support.smartbear.com/zephyr-scale-cloud/api-docs/">Zephyr Cloud rest API documentation</a>
 *
 * @author mark.vituska
 * @since 0.11.0
 */
@RegisterRestClient(baseUri = "https://api.zephyrscale.smartbear.com/v2")
@RegisterProvider(Tm4jJsonProvider.class)
@RegisterClientHeaders(ZephyrAuthHeadersFactory.class)
public interface ZephyrRestClient {

    @GET
    @Path("/testcases/{testCaseKey}")
    Response getTestCase(@PathParam("testCaseKey") String testCaseKey);

    @GET
    @Path("/testcycles/{testCycleKey}")
    Response getTestCycle(@PathParam("testCycleKey") String testCycleKey);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/testexecutions")
    void postExecution(ZephyrExecution execution);

}

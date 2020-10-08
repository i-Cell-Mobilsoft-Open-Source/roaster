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
package hu.icellmobilsoft.roaster.tm4j.common.client;

import hu.icellmobilsoft.roaster.tm4j.common.client.model.Execution;
import hu.icellmobilsoft.roaster.tm4j.common.client.model.TestRun;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

interface Tm4jClient {

    @GET("testcase/{testCaseKey}")
    Call<TestRun> getTestCase(@Path("testCaseKey") String testCaseKey);

    @GET("testrun/{testRunKey}")
    Call<TestRun> getTestRun(@Path("testRunKey") String testRunKey);

    @POST("testrun/{testRunKey}/testresults")
    Call<List<Execution>> postExecutionsForTestRun(@Path("testRunKey") String testRunKey, @Body List<Execution> executions);

}

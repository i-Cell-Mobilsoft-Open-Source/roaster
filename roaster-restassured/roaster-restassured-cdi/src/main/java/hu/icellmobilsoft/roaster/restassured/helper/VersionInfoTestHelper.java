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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import io.restassured.config.RestAssuredConfig;

/**
 * Helper class for /versionInfo endpoint restassured testing
 * 
 * @author mark.petrenyi
 */
@Dependent
public class VersionInfoTestHelper extends hu.icellmobilsoft.roaster.restassured.se.helper.VersionInfoTestHelper {

    /**
     * Constructor with {@link RestAssuredConfig} injected with {@link JSON} qualifier
     * 
     * @param restAssuredConfig
     *            injected {@link RestAssuredConfig}
     */
    @Inject
    public VersionInfoTestHelper(@JSON RestAssuredConfig restAssuredConfig) {
        super(restAssuredConfig);
    }
}

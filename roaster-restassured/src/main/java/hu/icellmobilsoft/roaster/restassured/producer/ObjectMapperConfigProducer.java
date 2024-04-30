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
package hu.icellmobilsoft.roaster.restassured.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;

/**
 * JSON RestAssuredConfig beállításainál használt ObjectMapperConfig producer
 * 
 * @author imre.scheffer
 * @since 0.2.0
 * @see RestAssuredConfig
 */
@ApplicationScoped
public class ObjectMapperConfigProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public ObjectMapperConfigProducer() {
        super();
    }

    /**
     * JSON típusú ObjectMapperConfig
     * 
     * @return ObjectMapperConfig
     */
    @Produces
    @JSON
    public ObjectMapperConfig produce() {
        Jackson2ObjectMapperFactory jackson2ObjectMapperFactory = CDI.current().select(Jackson2ObjectMapperFactory.class).get();
        return new ObjectMapperConfig().jackson2ObjectMapperFactory(jackson2ObjectMapperFactory);
    }
}

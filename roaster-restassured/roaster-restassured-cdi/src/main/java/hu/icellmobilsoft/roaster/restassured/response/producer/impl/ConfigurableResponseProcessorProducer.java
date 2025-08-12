/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.restassured.response.producer.impl;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.RestProcessor;
import hu.icellmobilsoft.roaster.jaxrs.se.response.ResponseProcessorConfig;
import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.annotation.XML;
import hu.icellmobilsoft.roaster.restassured.response.producer.spi.AbstractConfigurableResponseProcessorProducer;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

/**
 * CDI producer for {@link ConfigurableResponseProcessor} default implementation
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author martin.nagy
 * @since 0.5.0
 */
@Dependent
public final class ConfigurableResponseProcessorProducer<RESPONSE>
        extends AbstractConfigurableResponseProcessorProducer<ConfigurableResponseProcessor<RESPONSE>> {

    /**
     * Default constructor, constructs a new object.
     */
    public ConfigurableResponseProcessorProducer() {
        super();
    }

    @Override
    @Produces
    @RestProcessor(configKey = "")
    public ConfigurableResponseProcessor<RESPONSE> createConfiguredResponseProcessor(InjectionPoint injectionPoint) throws BaseException {
        return super.createConfiguredResponseProcessor(injectionPoint);
    }

    /**
     * Creates a managed {@link ConfigurableResponseProcessor} implementation
     *
     * @return the created {@link ConfigurableResponseProcessor} implementation
     */
    @Override
    protected ConfigurableResponseProcessor<RESPONSE> getBaseResponseProcessor(ResponseProcessorConfig config) throws BaseException {
        CDI<Object> cdi = CDI.current();
        return new ConfigurableResponseProcessor<>(
                config,
                cdi.select(RequestSpecification.class, new JSON.Literal()).get(),
                cdi.select(RequestSpecification.class, new XML.Literal()).get(),
                cdi.select(ResponseSpecification.class, new JSON.Literal()).get(),
                cdi.select(ResponseSpecification.class, new XML.Literal()).get()
        );
    }

}

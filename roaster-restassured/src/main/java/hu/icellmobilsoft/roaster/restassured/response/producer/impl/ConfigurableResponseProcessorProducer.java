/*-
 * #%L
 * Coffee
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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.roaster.restassured.response.producer.RestProcessor;
import hu.icellmobilsoft.roaster.restassured.response.producer.spi.AbstractConfigurableResponseProcessor;
import hu.icellmobilsoft.roaster.restassured.response.producer.spi.AbstractConfigurableResponseProcessorProducer;

/**
 * CDI producer for {@link AbstractConfigurableResponseProcessor} default implementation
 *
 * @param <RESPONSE>
 *            response class (any type)
 * @author martin.nagy
 * @since 0.5.0
 */
@Dependent
public final class ConfigurableResponseProcessorProducer<RESPONSE>
        extends AbstractConfigurableResponseProcessorProducer<ConfigurableResponseProcessor<RESPONSE>> {

    @Override
    @Produces
    @RestProcessor(configKey = "")
    public ConfigurableResponseProcessor<RESPONSE> createConfiguredResponseProcessor(InjectionPoint injectionPoint) throws BaseException {
        return super.createConfiguredResponseProcessor(injectionPoint);
    }

    /**
     * Creates a managed {@link AbstractConfigurableResponseProcessor} implementation
     *
     * @return the created {@link AbstractConfigurableResponseProcessor} implementation
     */
    @Override
    protected ConfigurableResponseProcessor<RESPONSE> getBaseResponseProcessor() {
        return CDI.current().select(ConfigurableResponseProcessor.class).get();
    }

}

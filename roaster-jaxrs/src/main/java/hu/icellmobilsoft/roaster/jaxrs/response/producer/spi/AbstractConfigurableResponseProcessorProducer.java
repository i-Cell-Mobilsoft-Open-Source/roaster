/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.jaxrs.response.producer.spi;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.ManagedResponseProcessorConfig;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.ResponseProcessorConfig;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.RestProcessor;

/**
 * Base class for Configurable ResponseProcessor CDI producers
 *
 * @author imre.scheffer
 * @since 0.8.0
 */
public abstract class AbstractConfigurableResponseProcessorProducer<T extends AbstractConfigurableResponseProcessor<?>> {

    /**
     * Creates a managed {@link AbstractConfigurableResponseProcessor} implementation
     *
     * @return the created {@link AbstractConfigurableResponseProcessor} implementation
     */
    protected abstract T getBaseResponseProcessor();

    /**
     * Creates and configures an {@link AbstractConfigurableResponseProcessor} implementation for the given injection point
     *
     * @param injectionPoint
     *            CDI producer injection point
     * @return the created {@link AbstractConfigurableResponseProcessor} implementation
     * @throws BaseException
     *             exception
     */
    protected T createConfiguredResponseProcessor(InjectionPoint injectionPoint) throws BaseException {
        T responseProcessor = getBaseResponseProcessor();
        RestProcessor annotation = injectionPoint.getAnnotated().getAnnotation(RestProcessor.class);
        ResponseProcessorConfig config = getConfig(annotation.configKey());

        responseProcessor.setConfig(config);
        responseProcessor.setExpectedStatusCode(annotation.expectedStatusCode());
        return responseProcessor;
    }

    /**
     * Creates and initializes a {@link ResponseProcessorConfig} based on the {@code configKey}
     *
     * @param configKey
     *            config key
     * @return the created config
     */
    protected ResponseProcessorConfig getConfig(String configKey) {
        CDI<Object> cdi = CDI.current();
        ManagedResponseProcessorConfig config = cdi.select(ManagedResponseProcessorConfig.class).get();
        config.setConfigKey(configKey);
        cdi.destroy(config);
        return config;
    }
}

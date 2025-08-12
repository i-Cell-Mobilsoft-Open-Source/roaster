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
package hu.icellmobilsoft.roaster.restassured.response.producer.spi;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.jaxrs.response.producer.RestProcessor;
import hu.icellmobilsoft.roaster.jaxrs.se.response.ProcessorConfigImpl;
import hu.icellmobilsoft.roaster.jaxrs.se.response.ResponseProcessorConfig;
import hu.icellmobilsoft.roaster.restassured.se.response.ConfigurableResponseProcessor;

import jakarta.enterprise.inject.spi.InjectionPoint;

/**
 * Base class for {@link ConfigurableResponseProcessor} CDI producers
 *
 * @param <T>
 *            responseProcessor type
 *
 * @author martin.nagy
 * @since 0.5.0
 */
public abstract class AbstractConfigurableResponseProcessorProducer<T extends ConfigurableResponseProcessor<?>> {

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractConfigurableResponseProcessorProducer() {
        super();
    }

    /**
     * Creates a managed {@link ConfigurableResponseProcessor} implementation
     * 
     * @param config
     *            Configuration class populated with microprofile config values
     * @return the created {@link ConfigurableResponseProcessor} implementation
     * @throws BaseException
     *             on missing or invalid config
     */
    protected abstract T getBaseResponseProcessor(ResponseProcessorConfig config) throws BaseException;

    /**
     * Creates and configures an {@link ConfigurableResponseProcessor} implementation for the given injection point
     *
     * @param injectionPoint
     *            CDI producer injection point
     * @return the created {@link ConfigurableResponseProcessor} implementation
     * @throws BaseException
     *             exception
     */
    protected T createConfiguredResponseProcessor(InjectionPoint injectionPoint) throws BaseException {
        RestProcessor annotation = AnnotationUtil.getAnnotation(injectionPoint, RestProcessor.class)
                .orElseThrow(() -> new BaseException(CoffeeFaultType.INVALID_INPUT, "RestProcessor annotation not found!"));
        ResponseProcessorConfig config = getConfig(annotation.configKey());

        T responseProcessor = getBaseResponseProcessor(config);
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
        return new ProcessorConfigImpl(configKey);
    }
}

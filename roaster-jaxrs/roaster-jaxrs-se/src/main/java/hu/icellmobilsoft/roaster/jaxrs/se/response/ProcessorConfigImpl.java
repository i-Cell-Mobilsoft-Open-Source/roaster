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
package hu.icellmobilsoft.roaster.jaxrs.se.response;

import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Helper class for obtaining response processor settings using microprofile config.<br>
 * ie.:
 *
 * <pre>
 * testsuite:
 *   rest:
 *     example:
 *      baseUriKey: example-project.example-service.url
 *      path: /rest/exampleService/example/{id}
 *      headers:
 *        - "X-LANGUAGE: hu"
 * </pre>
 *
 * The upper configuration is injectable with:
 *
 * <pre>
 * &#64;Inject
 * &#64;RestProcessor(configKey = "testsuite.rest.example")
 * private ResponseProcessorConfig responseProcessorConfig;
 * </pre>
 *
 * @author martin.nagy
 * @since 0.5.0
 */
public class ProcessorConfigImpl implements ResponseProcessorConfig {
    private final Config config;
    private final String configKey;

    /**
     * Constructor with config key.
     * 
     * @param configKey
     *            config key, e.g.: {@literal testsuite.rest.example}
     */
    public ProcessorConfigImpl(String configKey) {
        this.configKey = configKey;
        config = ConfigProvider.getConfig();
    }

    @Override
    public String getBaseUriKey() {
        return config.getValue(configKey + ".baseUriKey", String.class);
    }

    @Override
    public String getPath() {
        return config.getValue(configKey + ".path", String.class);
    }

    @Override
    public Optional<String[]> getHeaders() {
        return config.getOptionalValue(configKey + ".headers", String[].class);
    }

}

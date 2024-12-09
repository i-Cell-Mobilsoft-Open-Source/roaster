/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.zephyr.common.client;

import org.eclipse.yasson.YassonConfig;

import jakarta.annotation.Priority;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * Custom {@link Jsonb} {@link ContextResolver} for Zephyr rest clients
 *
 * @author martin.nagy
 * @since 2.5.0
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class ZephyrJsonbContextResolver implements ContextResolver<Jsonb> {

    private static final Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(new YassonConfig().withFailOnUnknownProperties(false)).build();

    /**
     * Default constructor
     */
    public ZephyrJsonbContextResolver() {
        super();
    }

    @Override
    public Jsonb getContext(Class<?> type) {
        return jsonb;
    }
}

/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.zephyr.common.jsonb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.ws.rs.ext.ContextResolver;

/**
 * Custom default JSON-B config for MP-rest client, should be provided by coffee: https://github.com/i-Cell-Mobilsoft-Open-Source/coffee/issues/512
 * 
 * @author czenczl
 * @since 2.1.0
 */
public class CustomJsonbContextResolver implements ContextResolver<Jsonb> {

    /**
     * Default constructor, constructs a new object.
     */
    public CustomJsonbContextResolver() {
        super();
    }

    @Override
    public Jsonb getContext(Class<?> type) {
        JsonbConfig config = new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field f) {
                return true;
            }

            @Override
            public boolean isVisible(Method m) {
                return false;
            }
        }).withBinaryDataStrategy(BinaryDataStrategy.BASE_64);

        return JsonbBuilder.newBuilder().withConfig(config).build();
    }

}

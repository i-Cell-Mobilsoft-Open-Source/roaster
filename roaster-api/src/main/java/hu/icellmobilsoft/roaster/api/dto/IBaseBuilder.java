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
package hu.icellmobilsoft.roaster.api.dto;

import java.util.function.Consumer;

/**
 * Interface to support the global builder operations.
 *
 * @param <T>
 *            generic type
 */
public interface IBaseBuilder<T> {
    /**
     * Target class
     * 
     * @return the target class object
     */
    public Class<T> getTargetClass();

    /**
     * Create new empty object
     * 
     * @return empty object of the target class
     */
    public T create();

    /**
     * Build new target class with inner state settings
     * 
     * @return an xsd/db valid object of the target class
     */
    public T build();

    /**
     * Reset inner state settings
     * 
     * @return clear the inner state of the builder (remove custom fill methods).
     */
    public IBaseBuilder<T> clear();

    /**
     * Add a new fill method to the fill method list at and of the list.
     *
     * @param fillMethod
     *            the method that change the inner state of the entity object.
     * @return the current builder.
     */
    public IBaseBuilder<T> fill(Consumer<T> fillMethod);
}

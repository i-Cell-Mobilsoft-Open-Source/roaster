/*-
 * #%L
 * Coffee
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Base class to build new entities.
 * 
 * @param <T>
 *            type of builder class
 *
 */
public abstract class BaseTypeBuilder<T> implements IBaseBuilder<T> {

    private List<Consumer<T>> fillMethods = new ArrayList<>();
    private List<Consumer<T>> baseFillMethods = new ArrayList<>();
    private Class<T> targetClass = null;

    private Supplier<T> createEntityMethod = null;

    /**
     * Constructor with base parameters
     * 
     * @param targetClass
     *            the class of the entity to build
     * @param createEntity
     *            the method that create an new object (mostly {@code T::new()})
     */
    public BaseTypeBuilder(Class<T> targetClass, Supplier<T> createEntity) {
        this.targetClass = targetClass;
        this.createEntityMethod = createEntity;
    }

    @Override
    public Class<T> getTargetClass() {
        return targetClass;
    }

    @Override
    public T create() {
        return createEntityMethod.get();
    }

    /**
     * New fill method to the fill base method list at and of the list.
     * 
     * @param fillMethod
     *            custom provider to fill the base object
     * @return the current builder
     */
    protected final IBaseBuilder<T> baseFill(Consumer<T> fillMethod) {
        this.baseFillMethods.add(fillMethod);
        return this;
    }

    @Override
    public final IBaseBuilder<T> fill(Consumer<T> fillMethod) {
        this.fillMethods.add(fillMethod);
        return this;
    }

    /**
     * Interface for the child class to fill the entity before the custom fill methods executed.
     *
     * @param entity
     *            the current entity object
     */
    protected abstract void fillEntity(final T entity);

    /**
     * Create an new xsd/db valid entity. <br>
     * Running order:
     * <ul>
     * <li>empty entity created</li>
     * <li>entity filled with the #fillEntity method (Exact builder specific behaviour)</li>
     * <li>entity filled with the custom, provided values from the specific unit test ({@link BaseTypeBuilder#fill(Consumer)})</li>
     * </ul>
     */
    @Override
    public T build() {
        T result = create();
        baseFillMethods.stream().sequential().forEach(m -> m.accept(result));
        fillEntity(result);
        fillMethods.stream().sequential().forEach(m -> m.accept(result));
        return result;
    }

    @Override
    public final IBaseBuilder<T> clear() {
        fillMethods.clear();
        return this;
    }
}

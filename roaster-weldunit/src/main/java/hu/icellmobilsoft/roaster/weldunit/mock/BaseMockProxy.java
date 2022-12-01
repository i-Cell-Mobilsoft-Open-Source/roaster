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
package hu.icellmobilsoft.roaster.weldunit.mock;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.mockito.ArgumentCaptor;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces;
import hu.icellmobilsoft.roaster.api.Condition;

/**
 * Base mock proxy
 * 
 * @param <S>
 *            service class
 * @param <D>
 *            self class
 */
public abstract class BaseMockProxy<S, D extends BaseMockProxy> {

    private final Class<S> serviceClass;

    /**
     * Initializes the object with serviceClass
     * 
     * @param serviceClass
     *            the class of the service
     */
    public BaseMockProxy(Class<S> serviceClass) {
        Condition.notNull(serviceClass, "Service class type must be set.");
        this.serviceClass = serviceClass;
    }

    /**
     * Returns self
     * 
     * @return self
     */
    protected abstract D self();

    /**
     * Returns the service class mock
     * 
     * @return the service class mock
     * @throws BaseException
     *             on error
     */
    public abstract S mock() throws BaseException;

    /**
     * @return the class of the service
     */
    public final Class<S> getServiceClass() {
        return this.serviceClass;
    }

    /**
     * Helper method to provide the result entity.
     * <p>
     * If the supplier is not null, than it will be preferred. If the supplier null than the provided entity will be the result.
     *
     * @param <E>
     *            generic type
     * @param supplier
     *            the supplier method
     * @param entity
     *            entity
     * @return the result entity.
     * @throws BaseException
     *             exception
     */
    protected final <E> E getEntityResult(final FunctionalInterfaces.BaseExceptionSupplier<E> supplier, final E entity) throws BaseException {
        Condition.expected(entity != null,
                MessageFormat.format("Service proxy [{0}] should have an provided result entity.", serviceClass.getSimpleName()));
        return (supplier != null) ? supplier.get() : entity;
    }

    /**
     * Helper method to provide the result entity.
     * <p>
     * If the supplier is not null, than it will be preferred. If the supplier null than the captured argument will be the result.
     *
     * @param <E>
     *            generic type
     * @param supplier
     *            the supplier method
     * @param entityCaptor
     *            entity captor
     * @return the result entity.
     * @throws BaseException
     *             exception
     */
    protected final <E> E getCapturedEntityResult(final FunctionalInterfaces.BaseExceptionSupplier<E> supplier, final ArgumentCaptor<E> entityCaptor)
            throws BaseException {
        return (supplier != null) ? supplier.get() : entityCaptor.getValue();
    }

    /**
     * Helper method to provide the result entity list.
     * <p>
     * If the supplier is not null, than it will be preferred. If the supplier null than the provided entity will be the only element in the result
     * list.
     *
     * @param <E>
     *            generic type
     * @param supplier
     *            the supplier method
     * @param entity
     *            entity
     * @return the result list.
     * @throws BaseException
     *             exception
     */
    protected final <E> List<E> getEntityListResult(final FunctionalInterfaces.BaseExceptionSupplier<List<E>> supplier, final E entity)
            throws BaseException {
        Condition.expected(entity != null,
                MessageFormat.format("Service proxy [{0}] should have an provided result entity.", serviceClass.getSimpleName()));
        return (supplier != null) ? supplier.get() : Collections.singletonList(entity);
    }

}

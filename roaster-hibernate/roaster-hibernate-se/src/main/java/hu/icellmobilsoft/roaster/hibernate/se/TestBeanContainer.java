/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.hibernate.se;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;

import hu.icellmobilsoft.coffee.model.base.audit.AuditProvider;

/**
 * Test BeanContainer implementation. Used for replacing hibernate listener which uses CDI in their implementation.
 *
 * @author martin.nagy
 * @since 2.6.0
 */
public class TestBeanContainer implements BeanContainer {
    private final Map<Class<?>, ContainedBean<?>> beanCache = new HashMap<>();

    private final Supplier<Object> principalSupplier;

    /**
     * Constructor for {@link TestBeanContainer}.
     * 
     * @param principalSupplier
     *            the supplier for the principal.
     */
    public TestBeanContainer(Supplier<Object> principalSupplier) {
        this.principalSupplier = principalSupplier;
    }

    @Override
    public <B> ContainedBean<B> getBean(Class<B> beanType, LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        return (ContainedBean<B>) beanCache.computeIfAbsent(beanType, k -> createContainedBean(beanType, fallbackProducer));
    }

    private <B> ContainedBean<?> createContainedBean(Class<B> beanType, BeanInstanceProducer fallbackProducer) {
        if (beanType.equals(AuditProvider.class)) {
            return () -> (B) new TestAuditProvider(principalSupplier);
        }
        return () -> fallbackProducer.produceBeanInstance(beanType);
    }

    @Override
    public <B> ContainedBean<B> getBean(String name, Class<B> beanType, LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        return () -> fallbackProducer.produceBeanInstance(name, beanType);
    }

    @Override
    public void stop() {
        beanCache.clear();
    }
}

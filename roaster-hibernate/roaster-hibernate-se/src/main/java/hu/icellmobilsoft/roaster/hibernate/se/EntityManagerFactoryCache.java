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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.hibernate.cfg.Environment;
import org.hibernate.resource.beans.container.spi.BeanContainer;

import hu.icellmobilsoft.roaster.hibernate.common.config.EntityManagerFactoryFactory;
import hu.icellmobilsoft.roaster.hibernate.common.config.HibernateConfig;
import hu.icellmobilsoft.roaster.hibernate.common.config.HibernateConfigImpl;

import jakarta.persistence.EntityManagerFactory;

/**
 * JVM level cache for {@link EntityManagerFactory} instances.
 *
 * @author martin.nagy
 * @since 2.6.0
 */
public class EntityManagerFactoryCache {
    private static final Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORIES_BY_PU = new ConcurrentHashMap<>();
    private static Supplier<Object> principalSupplier = () -> "0";
    private static Supplier<BeanContainer> beanContainerFactory = () -> new TestBeanContainer(principalSupplier);

    /**
     * Private constructor to prevent instantiation.
     */
    private EntityManagerFactoryCache() {
    }

    /**
     * Returns an EntityManagerFactory for the default persistence unit.
     * 
     * @return EntityManagerFactory for the default persistence unit.
     */
    public static EntityManagerFactory getForDefaultPersistenceUnit() {
        return getForPersistenceUnit(HibernateConfig.DEFAULT_PERSISTENCE_UNIT_NAME);
    }

    /**
     * Returns an EntityManagerFactory for the given persistence unit name. At the first call, a shutdown hook is registered to close all
     * EntityManagerFactory instances.
     * 
     * @param persistenceUnitName
     *            name of the persistence unit for which the EntityManagerFactory should be returned.
     * @return EntityManagerFactory for the given persistence unit name.
     */
    public static EntityManagerFactory getForPersistenceUnit(String persistenceUnitName) {
        if (ENTITY_MANAGER_FACTORIES_BY_PU.isEmpty()) {
            // At the first call we register a shutdown hook to close all EntityManagerFactory instances
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown hook called - closing all EntityManagerFactory instances ...");
                ENTITY_MANAGER_FACTORIES_BY_PU.values().forEach(EntityManagerFactory::close);
            }));
        }

        return ENTITY_MANAGER_FACTORIES_BY_PU.computeIfAbsent(
                persistenceUnitName,
                key -> EntityManagerFactoryFactory.createNewEntityManagerFactory(
                        new HibernateConfigImpl(persistenceUnitName),
                        EntityManagerFactoryCache::customizeHibernateConfig));
    }

    private static void customizeHibernateConfig(Map<String, Object> props) {
        props.put(Environment.BEAN_CONTAINER, beanContainerFactory.get());
        props.put(Environment.DELAY_CDI_ACCESS, true);
    }

    /**
     * Sets the supplier for the principal.
     * 
     * @param principalSupplier
     *            the supplier for the principal.
     */
    public static void setPrincipalSupplier(Supplier<Object> principalSupplier) {
        EntityManagerFactoryCache.principalSupplier = principalSupplier;
    }

    /**
     * Sets the supplier for the {@link BeanContainer} factory.
     * 
     * @param beanContainerFactory
     *            the supplier for the {@link BeanContainer} factory.
     */
    public static void setBeanContainerFactory(Supplier<BeanContainer> beanContainerFactory) {
        EntityManagerFactoryCache.beanContainerFactory = beanContainerFactory;
    }
}

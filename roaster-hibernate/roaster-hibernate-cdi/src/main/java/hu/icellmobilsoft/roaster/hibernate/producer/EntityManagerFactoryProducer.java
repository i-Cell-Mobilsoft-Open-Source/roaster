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
/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package hu.icellmobilsoft.roaster.hibernate.producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;

import org.hibernate.cfg.Environment;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.hibernate.annotation.HibernatePersistenceConfig;
import hu.icellmobilsoft.roaster.hibernate.common.config.EntityManagerFactoryFactory;
import hu.icellmobilsoft.roaster.hibernate.common.config.HibernateConfig;
import hu.icellmobilsoft.roaster.hibernate.common.config.HibernateConfigImpl;

/**
 * Producer for creating or obtaining {@link EntityManagerFactory} from myPu persistenceUnit from META-INF/persistence.xml
 *
 * @since 0.2.0
 * @author speter555
 * @author csaba.balogh
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

    @Inject
    private BeanManager beanManager;

    private final Map<String, EntityManagerFactory> entityManagerFactoryCache = new ConcurrentHashMap<>();

    /**
     * Cleanup of the cache and resources associated with EntityManagerFactory instances.
     */
    @PreDestroy
    public void preDestroy() {
        entityManagerFactoryCache.forEach((configKey, entityManagerFactory) -> {
            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
        });
        entityManagerFactoryCache.clear();
    }

    /**
     * Default constructor, constructs a new object.
     */
    public EntityManagerFactoryProducer() {
        super();
    }

    /**
     * Producer for creating or obtaining {@link EntityManagerFactory}
     *
     * @param injectionPoint
     *            CDI injection point
     * @return {@link EntityManagerFactory} instance
     */
    @Produces
    @Dependent
    public EntityManagerFactory produceDefaultEntityManagerFactory(InjectionPoint injectionPoint) {
        HibernateConfig hibernateConfig = getHibernateConfig(HibernateConfig.DEFAULT_PERSISTENCE_UNIT_NAME);
        return getEntityManagerFactory(hibernateConfig);
    }

    /**
     * Producer for creating or obtaining {@link EntityManagerFactory}
     *
     * @param injectionPoint
     *            CDI injection point
     * @return {@link EntityManagerFactory} instance
     *
     * @throws BaseException
     *             exception
     */
    @Produces
    @Dependent
    @HibernatePersistenceConfig
    public EntityManagerFactory produceEntityManagerFactory(InjectionPoint injectionPoint) throws BaseException {
        HibernatePersistenceConfig hibernatePersistenceConfig = AnnotationUtil.getAnnotation(injectionPoint, HibernatePersistenceConfig.class)
                .orElseThrow(() -> new BaseException(CoffeeFaultType.INVALID_INPUT, "PersistenceUnitName annotation have to have configKey value!"));
        HibernateConfig hibernateConfig = getHibernateConfig(hibernatePersistenceConfig.persistenceUnitName());
        return getEntityManagerFactory(hibernateConfig);
    }

    /**
     * Creates a new {@link HibernateConfig} instance using the specified persistenceUnitName.
     *
     * @param persistenceUnitName
     *            the name of the persistence unit for which a {@link HibernateConfig} instance is to be created
     * @return a new {@link HibernateConfig} instance configured with the specified persistenceUnitName
     */
    protected HibernateConfig getHibernateConfig(String persistenceUnitName) {
        return new HibernateConfigImpl(persistenceUnitName);
    }

    /**
     * Retrieves the {@link EntityManagerFactory} associated with the specified {@link HibernateConfig}. If an EntityManagerFactory is already cached
     * for the given HibernateConfig, it is returned. Otherwise, a new EntityManagerFactory is created using the provided HibernateConfig, added to
     * the cache, and returned.
     *
     * @param hibernateConfig
     *            The Hibernate configuration used to identify the EntityManagerFactory.
     * @return The EntityManagerFactory associated with the specified HibernateConfig.
     */
    private EntityManagerFactory getEntityManagerFactory(HibernateConfig hibernateConfig) {
        return entityManagerFactoryCache.computeIfAbsent(hibernateConfig.getConfigKey(), key -> createNewEntityManagerFactory(hibernateConfig));
    }

    /**
     * Creates a new {@link EntityManagerFactory} instance using the provided {@link HibernateConfig}.<br>
     * This method delegates the creation process to {@link EntityManagerFactoryFactory#createNewEntityManagerFactory} with a null customizer.
     *
     * @param hibernateConfig
     *            the {@link HibernateConfig} instance containing configuration details for the {@link EntityManagerFactory}
     * @return a new {@link EntityManagerFactory} instance configured with the specified {@link HibernateConfig}
     */
    protected EntityManagerFactory createNewEntityManagerFactory(HibernateConfig hibernateConfig) {
        return EntityManagerFactoryFactory.createNewEntityManagerFactory(hibernateConfig, this::initBeanManager);
    }

    private void initBeanManager(Map<String, Object> props) {
        props.put(Environment.CDI_BEAN_MANAGER, beanManager);
    }
}

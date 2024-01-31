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
/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package hu.icellmobilsoft.roaster.hibernate.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.hibernate.cfg.Environment;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.hibernate.annotation.HibernatePersistenceConfig;
import hu.icellmobilsoft.roaster.hibernate.config.HibernateConfig;

/**
 * Producer for creating or obtaining {@link EntityManagerFactory} from myPu persistenceUnit from META-INF/persistence.xml
 *
 * @since 0.2.0
 * @author speter555
 * @author csaba.balogh
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

    private final Logger logger = Logger.getLogger(EntityManagerFactoryProducer.class);

    private static final Map<String, EntityManagerFactory> entityManagerFactoryCache = new ConcurrentHashMap<>();

    @Inject
    private BeanManager beanManager;

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
        HibernateConfig hibernateConfig = CDI.current()
                .select(HibernateConfig.class, new HibernatePersistenceConfig.Literal(HibernateConfig.DEFAULT_PERSISTENCE_UNIT_NAME)).get();

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
        HibernateConfig hibernateConfig = CDI.current()
                .select(HibernateConfig.class, new HibernatePersistenceConfig.Literal(hibernatePersistenceConfig.persistenceUnitName())).get();

        return getEntityManagerFactory(hibernateConfig);
    }

    private EntityManagerFactory getEntityManagerFactory(HibernateConfig hibernateConfig) {
        entityManagerFactoryCache.entrySet().removeIf(entries -> !entries.getValue().isOpen());

        if (entityManagerFactoryCache.containsKey(hibernateConfig.getConfigKey())) {
            return entityManagerFactoryCache.get(hibernateConfig.getConfigKey());
        } else {
            return createNewEntityManagerFactory(hibernateConfig);
        }
    }

    private EntityManagerFactory createNewEntityManagerFactory(HibernateConfig hibernateConfig) {
        Map<String, Object> props = new HashMap<>();

        // TODO jakartaEE atalasnal, valosiznu kiszeheto -
        // https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#beans-cdi
        // Set CDI Bean manager
        props.put(Environment.CDI_BEAN_MANAGER, beanManager);

        // JPA use in JAVA SE
        props.put(Environment.JAKARTA_TRANSACTION_TYPE, "RESOURCE_LOCAL");
        props.put(Environment.JAKARTA_PERSISTENCE_PROVIDER, "org.hibernate.jpa.HibernatePersistenceProvider");

        // TODO jakartaEE atalasnal nincs ilyen opcio
        // //
        // props.put(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, false);

        // Set settings from Roaster config
        props.put(Environment.DIALECT, hibernateConfig.getDialect());
        props.put(Environment.POOL_SIZE, hibernateConfig.getPoolSize());
        props.put(Environment.SHOW_SQL, hibernateConfig.getShowSql());
        props.put(Environment.FORMAT_SQL, hibernateConfig.getFormatSql());
        props.put(Environment.DEFAULT_SCHEMA, hibernateConfig.getDefaultSchema());
        props.put(Environment.JAKARTA_JDBC_URL, hibernateConfig.getJpaJdbcUrl());
        props.put(Environment.JAKARTA_JDBC_USER, hibernateConfig.getJpaJdbcUser());
        props.put(Environment.JAKARTA_JDBC_PASSWORD, hibernateConfig.getJpaJdbcPassword());
        props.put(Environment.JAKARTA_JDBC_DRIVER, hibernateConfig.getJpaJdbcDriver());
        props.put(Environment.LOG_SESSION_METRICS, hibernateConfig.getLogSessionMetrics());
        props.put(Environment.LOG_JDBC_WARNINGS, hibernateConfig.getLogJdbcWarnings());
        props.put(Environment.GENERATE_STATISTICS, hibernateConfig.getGenerateStatistics());

        // If any config value is null, remove it from config map
        props.values().removeIf(Objects::isNull);

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(hibernateConfig.getConfigKey(), props);
        entityManagerFactoryCache.put(hibernateConfig.getConfigKey(), entityManagerFactory);
        return entityManagerFactory;
    }
}

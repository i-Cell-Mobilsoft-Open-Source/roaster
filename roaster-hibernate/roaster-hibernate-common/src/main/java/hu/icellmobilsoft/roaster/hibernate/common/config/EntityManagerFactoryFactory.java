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
package hu.icellmobilsoft.roaster.hibernate.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.hibernate.cfg.Environment;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Factory class for creating {@link EntityManagerFactory}
 *
 * @author martin.nagy
 * @since 2.6.0
 */
public class EntityManagerFactoryFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private EntityManagerFactoryFactory() {
    }

    /**
     * Creates a new {@link EntityManagerFactory} instance with the given {@link HibernateConfig}. Optionally a hibernate config customizer can be
     * provided.
     * 
     * @param hibernateConfig
     *            {@link HibernateConfig} instance with the configuration for the {@link EntityManagerFactory}
     * @param configCustomizer
     *            optional config customizer for the {@link EntityManagerFactory} properties. See the second param of the
     *            {@link Persistence#createEntityManagerFactory(String, Map)}
     * @return configured {@link EntityManagerFactory} instance
     */
    public static EntityManagerFactory createNewEntityManagerFactory(HibernateConfig hibernateConfig,
            Consumer<Map<String, Object>> configCustomizer) {
        Map<String, Object> props = new HashMap<>();

        // JPA use in JAVA SE
        props.put(Environment.JAKARTA_TRANSACTION_TYPE, "RESOURCE_LOCAL");
        props.put(Environment.JAKARTA_PERSISTENCE_PROVIDER, "org.hibernate.jpa.HibernatePersistenceProvider");

        // TODO no such option with jakartaEE atalas
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

        if (configCustomizer != null) {
            configCustomizer.accept(props);
        }

        // If any config value is null, remove it from config map
        props.values().removeIf(Objects::isNull);

        return Persistence.createEntityManagerFactory(hibernateConfig.getConfigKey(), props);
    }
}

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
package hu.icellmobilsoft.roaster.hibernate.config;

import hu.icellmobilsoft.roaster.hibernate.annotation.HibernatePersistenceConfig;

/**
 * Configuration for hibernate config
 * 
 * @author speter555
 * @author csaba.balogh
 */
public interface HibernateConfig {

    /**
     * Default value for {@link HibernatePersistenceConfig#persistenceUnitName}
     */
    String DEFAULT_PERSISTENCE_UNIT_NAME = "defaultPU";

    /**
     * Set configKey
     *
     * @return configKey config key
     */
    String getConfigKey();

    /**
     * Set configKey
     * 
     * @param configKey
     *            config key
     */
    void setConfigKey(String configKey);

    /**
     * 'hibernate.dialect' setting
     * 
     * @return settings value
     */
    String getDialect();

    /**
     * 'hibernate.connection.pool_size' setting
     * 
     * @return settings value
     */
    String getPoolSize();

    /**
     * 'hibernate.show_sql' setting
     * 
     * @return settings value
     */
    String getShowSql();

    /**
     * 'hibernate.format_sql' setting
     * 
     * @return settings value
     */
    String getFormatSql();

    /**
     * 'hibernate.default_schema' setting
     * 
     * @return settings value
     */
    String getDefaultSchema();

    /**
     * 'javax.persistence.jdbc.url' setting
     * 
     * @return settings value
     */
    String getJpaJdbcUrl();

    /**
     * 'javax.persistence.jdbc.user' setting
     * 
     * @return settings value
     */
    String getJpaJdbcUser();

    /**
     * 'javax.persistence.jdbc.password' setting
     * 
     * @return settings value
     */
    String getJpaJdbcPassword();

    /**
     * 'javax.persistence.jdbc.driver' setting
     * 
     * @return settings value
     */
    String getJpaJdbcDriver();

    /**
     * 'hibernate.session.events.log' setting
     * 
     * @return settings value
     */
    boolean getLogSessionMetrics();

    /**
     * 'hibernate.jdbc.log.warnings' setting
     * 
     * @return settings value
     */
    boolean getLogJdbcWarnings();

    /**
     * 'hibernate.generate_statistics' setting
     * 
     * @return settings value
     */
    boolean getGenerateStatistics();
}

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
package hu.icellmobilsoft.roaster.hibernate.config;

import org.hibernate.cfg.Environment;

/**
 * Configuration for hibernate config
 * 
 * @author speter555
 */
public interface HibernateConfig {

    /**
     * Set configKey
     * 
     * @param configKey
     *            config key
     */
    void setConfigKey(String configKey);

    /**
     * {@link Environment.DIALECT} setting
     * 
     * @return settings value
     */
    String getDialect();

    /**
     * {@link Environment.POOL_SIZE} setting
     * 
     * @return settings value
     */
    String getPooSize();

    /**
     * {@link Environment.SHOW_SQL} setting
     * 
     * @return settings value
     */
    String getShowSql();

    /**
     * {@link Environment.FORMAT_SQL} setting
     * 
     * @return settings value
     */
    String getFormatSql();

    /**
     * {@link Environment.DEFAULT_SCHEMA} setting
     * 
     * @return settings value
     */
    String getDefaultSchema();

    /**
     * {@link Environment.JPA_JDBC_URL} setting
     * 
     * @return settings value
     */
    String getJpaJdbcUrl();

    /**
     * {@link Environment.JPA_JDBC_USER} setting
     * 
     * @return settings value
     */
    String getJpaJdbcUser();

    /**
     * {@link Environment.JPA_JDBC_PASSWORD} setting
     * 
     * @return settings value
     */
    String getJpaJdbcPassword();

    /**
     * {@link Environment.JPA_JDBC_DRIVER} setting
     * 
     * @return settings value
     */
    String getJpaJdbcDriver();
}

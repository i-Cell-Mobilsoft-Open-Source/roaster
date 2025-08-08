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
package hu.icellmobilsoft.roaster.hibernate.common.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Helper class for obtaining Hibernate DB connection settings using microprofile config.<br>
 * General pattern is "{@code roaster.hibernate.${persistenceUnitName}.${setting}}
 * <p>
 * ie.:
 *
 * <pre>
 *  roaster:
 *   hibernate:
 *     myPersistenceUnitName:
 *       jdbc:
 *         driver: oracle.jdbc.OracleDriver
 *         url: jdbc:oracle:thin:@//localhost:1521/XE
 *         user: db_username
 *         password: *****
 *       hibernate:
 *         default_schema: user_schema
 *         dialect: org.hibernate.dialect.Oracle12cDialect
 *         show_sql: true
 *         format_sql: true
 * </pre>
 * 
 * @author speter555
 * @author csaba.balogh
 */
public class HibernateConfigImpl implements HibernateConfig {

    /**
     * Constant <code>HIBERNATE="roaster.hibernate"</code>
     */
    private static final String HIBERNATE = "roaster.hibernate";

    /**
     * Constant <code>KEY_DELIMITER="."</code>
     */
    private static final String KEY_DELIMITER = ".";

    private static final String DIALECT = "hibernate.dialect";
    private static final String POOL_SIZE = "hibernate.pool_size";
    private static final String SHOW_SQL = "hibernate.show_sql";
    private static final String FORMAT_SQL = "hibernate.format_sql";
    private static final String DEFAULT_SCHEMA = "hibernate.default_schema";
    private static final String LOG_SESSION_METRICS = "hibernate.log_session_metrics";
    private static final String GENERATE_STATISTICS = "hibernate.generate_statistics";

    private static final String JDBC_URL = "jdbc.url";
    private static final String JDBC_USER = "jdbc.user";
    private static final String JDBC_PASSWORD = "jdbc.password";
    private static final String JDBC_DRIVER = "jdbc.driver";
    private static final String JDBC_LOG_WARNINGS = "jdbc.log_warnings";

    private final Logger logger = Logger.getLogger(HibernateConfigImpl.class);
    private final Config config = ConfigProvider.getConfig();
    private final String configKey;

    /**
     * Default constructor. Uses {@link #DEFAULT_PERSISTENCE_UNIT_NAME} as persistence unit name.
     */
    public HibernateConfigImpl() {
        this(DEFAULT_PERSISTENCE_UNIT_NAME);
    }

    /**
     * Constructor with persistence unit name.
     * 
     * @param configKey
     *            persistence unit name used for config lookup
     */
    public HibernateConfigImpl(String configKey) {
        this.configKey = configKey;
    }

    @Override
    public String getConfigKey() {
        return configKey;
    }

    @Override
    public String getDialect() {
        return getConfigValue(DIALECT);
    }

    @Override
    public String getPoolSize() {
        return getConfigValue(POOL_SIZE);
    }

    @Override
    public String getShowSql() {
        return getConfigValue(SHOW_SQL);
    }

    @Override
    public String getFormatSql() {
        return getConfigValue(FORMAT_SQL);
    }

    @Override
    public String getDefaultSchema() {
        return getConfigValue(DEFAULT_SCHEMA);
    }

    @Override
    public String getJpaJdbcUrl() {
        return getConfigValue(JDBC_URL);
    }

    @Override
    public String getJpaJdbcUser() {
        return getConfigValue(JDBC_USER);
    }

    @Override
    public String getJpaJdbcPassword() {
        return getConfigValue(JDBC_PASSWORD);
    }

    @Override
    public String getJpaJdbcDriver() {
        return getConfigValue(JDBC_DRIVER);
    }

    @Override
    public boolean getLogSessionMetrics() {
        return getBooleanConfigValue(LOG_SESSION_METRICS);
    }

    @Override
    public boolean getLogJdbcWarnings() {
        return getBooleanConfigValue(JDBC_LOG_WARNINGS);
    }

    @Override
    public boolean getGenerateStatistics() {
        return getBooleanConfigValue(GENERATE_STATISTICS);
    }

    private String getConfigValue(String key) {
        String fullKey = joinKey(key);
        String value = config.getOptionalValue(fullKey, String.class).orElse(null);
        logConfig(fullKey, value);
        return value;
    }

    private boolean getBooleanConfigValue(String key) {
        String fullKey = joinKey(key);
        boolean value = config.getOptionalValue(fullKey, Boolean.class).orElse(false);
        logConfig(fullKey, value);
        return value;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, HIBERNATE, configKey, key);
    }

    private void logConfig(String key, Object value) {
        logger.debug("Config key [{0}] value: [{1}]", key, value);
    }
}

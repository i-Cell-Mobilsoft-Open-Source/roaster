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

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Hibernate configuration from roaster-*.yml under roaster.hibernate key.
 * 
 * @author speter555
 */
@Dependent
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

    private static final String JDBC_URL = "jdbc.url";
    private static final String JDBC_USER = "jdbc.user";
    private static final String JDBC_PASSWORD = "jdbc.password";
    private static final String JDBC_DRIVER = "jdbc.driver";

    private Logger logger = Logger.getLogger(HibernateConfigImpl.class);

    private String configKey;

    @Inject
    private Config config;

    /**
     * Set ConfigKey
     * 
     * @param configKey
     *            key
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getDialect() {
        return getConfigValue(DIALECT);
    }

    public String getPooSize() {
        return getConfigValue(POOL_SIZE);
    }

    public String getShowSql() {
        return getConfigValue(SHOW_SQL);
    }

    public String getFormatSql() {
        return getConfigValue(FORMAT_SQL);
    }

    public String getDefaultSchema() {
        return getConfigValue(DEFAULT_SCHEMA);
    }

    public String getJpaJdbcUrl() {
        return getConfigValue(JDBC_URL);
    }

    public String getJpaJdbcUser() {
        return getConfigValue(JDBC_USER);
    }

    public String getJpaJdbcPassword() {
        return getConfigValue(JDBC_PASSWORD);
    }

    public String getJpaJdbcDriver() {
        return getConfigValue(JDBC_DRIVER);
    }

    private String getConfigValue(String key) {
        Optional<String> optionalConfigValue = config.getOptionalValue(joinKey(key), String.class);
        String value = optionalConfigValue.orElse(null);
        logger.debug("{0} key ha {1} value.", key, value);
        return value;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, HIBERNATE, configKey, key);
    }

}

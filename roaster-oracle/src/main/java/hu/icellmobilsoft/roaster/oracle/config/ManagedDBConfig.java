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
package hu.icellmobilsoft.roaster.oracle.config;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.roaster.oracle.constatns.DBTypeEnum;

/**
 * Helper class for obtaining DB connection settings using microprofile config.<br>
 * General pattern is "{@code roaster.datasource.${dbType}.${schema}.${setting}}
 * <p>
 * ie.:
 *
 * <pre>
 *  roaster:
 *      datasource:
 *          oracle:
 *              employee:
 *                  url: jdbc:oracle:thin:@ldap://ldap.sample.hu:389/SAMPLEDB,cn=OracleContext,dc=sample,dc=hu
 *                  user: user
 *                  password: *****
 * </pre>
 * <p>
 * The upper configuration is injectable with:
 *
 * <pre>
 * &#64;Inject
 * &#64;DBConnection(dbType = "oracle", schema = "employee")
 * ManagedDBConfig dbConfig;
 * </pre>
 * <p>
 *
 * or:
 *
 * <pre>
 * ManagedDBConfig dbConfig = CDI.current().select(ManagedDBConfig.class, new DBConnection.Literal("oracle", "employee")).get();
 * </pre>
 *
 * @author balazs.joo
 */
@Dependent
public class ManagedDBConfig implements DBConfig {

    /**
     * Constant <code>DB_PREFIX="roaster.datasource"</code>
     */
    public static final String DB_PREFIX = "roaster.datasource";

    /**
     * Constant <code>URL="url"</code>
     */
    public static final String URL = "url";
    /**
     * Constant <code>USER="user"</code>
     */
    public static final String USER = "user";
    /**
     * Constant <code>PASSWORD="password"</code>
     */
    public static final String PASSWORD = "password";
    /**
     * Constant {@value}
     */
    public static final String MAXIMUM_POOL_SIZE = "maximumPoolSize";
    /**
     * Constant <code>KEY_DELIMITER="."</code>
     */
    public static final String KEY_DELIMITER = ".";

    @Inject
    private Config config;

    private String configKey;

    /**
     * {@inheritDoc}
     * <p>
     * The url where the selected DB is available.
     */
    @Override
    public String getUrl() {
        return config.getOptionalValue(joinKey(URL), String.class).orElse("localhost");
    }

    /**
     * {@inheritDoc}
     * <p>
     * The user of the selected DB to connect with.
     */
    @Override
    public String getUser() {
        return config.getOptionalValue(joinKey(USER), String.class).orElse("admin");
    }

    /**
     * {@inheritDoc}
     * <p>
     * The password of the selected DB to connect with.
     */
    @Override
    public String getPassword() {
        return config.getOptionalValue(joinKey(PASSWORD), String.class).orElse(null);
    }

    @Override
    public int getMaximumPoolSize() {
        return config.getOptionalValue(joinKey(MAXIMUM_POOL_SIZE), Integer.class).orElse(5);
    }

    /**
     * Getter for the field {@code configKey}.
     *
     * @return configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Sets the new configKey value
     * 
     * @param configKey
     *            new configKey value
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, DB_PREFIX, DBTypeEnum.ORACLE.value(), configKey, key);
    }
}

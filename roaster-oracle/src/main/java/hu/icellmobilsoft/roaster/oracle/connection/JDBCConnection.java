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
package hu.icellmobilsoft.roaster.oracle.connection;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Objects;

import jakarta.enterprise.context.Dependent;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.oracle.config.ManagedDBConfig;

/**
 * Connection container with configuration
 *
 * @author balazs.joo
 */
@Dependent
public class JDBCConnection implements Closeable {

    private static final String ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION = "Error occurred during DB connection! [{0}]";
    private static final String ERROR_MSG_DB_CONFIGURATION_NOT_SET = "DB configuration not set!";

    private final Logger log = Logger.getLogger(JDBCConnection.class);

    private ManagedDBConfig config;
    private HikariDataSource dataSource;

    /**
     * Default constructor, constructs a new object.
     */
    public JDBCConnection() {
        super();
    }

    /**
     * Creates connection, using given configuration
     *
     * @return JDBC connection
     * @throws BaseException
     *             exception
     */
    public Connection getConnection() throws BaseException {
        initIfNeeded();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            String errorMsg = MessageFormat.format(ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION, e.getLocalizedMessage());
            log.error(errorMsg);
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, errorMsg, e);
        }
    }

    private void initIfNeeded() throws TechnicalException {
        if (Objects.isNull(config)) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, ERROR_MSG_DB_CONFIGURATION_NOT_SET);
        }
        if (dataSource == null) {
            log.trace("Creating dataSource. Url: [{0}], user: [{1}]", config.getUrl(), config.getUser());
            dataSource = createDataSource();
        }
    }

    private HikariDataSource createDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());

        return new HikariDataSource(hikariConfig);
    }

    /**
     * Set configuration
     *
     * @param config
     *            configuration
     * @return this
     */
    public JDBCConnection withConfig(ManagedDBConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Check connection is closed
     *
     * @return true if connection is null, closed or error by checking
     */
    public boolean isClosed() {
        if (Objects.isNull(dataSource)) {
            return true;
        }
        return dataSource.isClosed();
    }

    /**
     * Destroys created Connection
     */
    @Override
    public void close() {
        if (!isClosed()) {
            log.trace("Closing dataSource...");
            dataSource.close();
        }
    }
}

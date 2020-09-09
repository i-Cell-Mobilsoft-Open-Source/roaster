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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Objects;

import javax.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.oracle.config.ManagedDBConfig;

/**
 * Connection container with configuration
 *
 * @author balazs.joo
 * @since 0.0.2
 */
@Dependent
public class JDBCConnection implements Closeable {

    private static final String ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION = "Error occurred during DB connection! [{0}]";
    private static final String ERROR_MSG_ERROR_OCCURRED_DURING_CHECKING_DB_CONNECTION = "Error occurred during checking DB connection! [{0}]";
    private static final String ERROR_MSG_ERROR_OCCURRED_DURING_CLOSING_DB_CONNECTION = "Error occurred during closing DB connection! [{0}]";
    private static final String ERROR_MSG_DB_CONFIGURATION_NOT_SET = "DB configuration not set!";

    private final Logger log = Logger.getLogger(JDBCConnection.class);

    private Connection connection;

    private ManagedDBConfig config;

    /**
     * Creates connection, using given configuration
     *
     * @return
     * @throws BaseException
     */
    public Connection getConnection() throws BaseException {
        if (Objects.isNull(config)) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, ERROR_MSG_DB_CONFIGURATION_NOT_SET);
        }
        try {
            log.trace("Creating connection url [{0}], user: [{1}]", config.getUrl(), config.getUser());
            connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
        } catch (SQLException e) {
            String errorMsg = MessageFormat.format(ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION, e.getLocalizedMessage());
            log.error(errorMsg);
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, errorMsg, e);
        }
        return connection;
    }

    /**
     * Set configuration
     *
     * @param config configuration
     * @return
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
        if (Objects.isNull(connection)) {
            return true;
        }
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            log.error(MessageFormat.format(ERROR_MSG_ERROR_OCCURRED_DURING_CHECKING_DB_CONNECTION, e.getLocalizedMessage()), e);
            return true;
        }
    }

    /**
     * Destroys created Connection
     */
    @Override
    public void close() {
        try {
            if (!isClosed()) {
                log.trace("Closing connection...");
                connection.close();
            }
        } catch (SQLException e) {
            log.error(MessageFormat.format(ERROR_MSG_ERROR_OCCURRED_DURING_CLOSING_DB_CONNECTION, e.getLocalizedMessage()), e);
        }
    }
}

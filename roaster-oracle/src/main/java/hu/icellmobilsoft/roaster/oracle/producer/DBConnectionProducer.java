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
package hu.icellmobilsoft.roaster.oracle.producer;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.oracle.annotation.DBConnection;
import hu.icellmobilsoft.roaster.oracle.config.ManagedDBConfig;
import hu.icellmobilsoft.roaster.oracle.connection.JDBCConnection;

/**
 * Producer for creating or obtaining {@link Connection}
 *
 * @author balazs.joo
 */
@ApplicationScoped
public class DBConnectionProducer {

    private final Logger log = Logger.getLogger(DBConnectionProducer.class);

    private final Map<String, JDBCConnection> connectionInstances = new HashMap<>();

    /**
     * Creates or gets Connection for the given configKey
     * 
     * @param injectionPoint
     *            CDI injection point
     * @return created object
     * @throws BaseException
     *             exception
     */
    @Produces
    @Dependent
    @DBConnection(configKey = "")
    public JDBCConnection getConnection(InjectionPoint injectionPoint) throws BaseException {
        Optional<DBConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, DBConnection.class);
        String configKey = annotation.map(DBConnection::configKey)
                .orElseThrow(() -> new BaseException(CoffeeFaultType.INVALID_INPUT, "configKey value not found!"));
        return getInstance(configKey);
    }

    /**
     * Returns the Connection for the given configKey. Returned pools are cached by configKey. Synchronized in order to prevent creating multiple
     * pools for the same connection.
     *
     * @param configKey
     *            config key
     * @return connection handler object
     */
    private synchronized JDBCConnection getInstance(String configKey) {
        return connectionInstances.compute(configKey, this::getJdbcConnection);
    }

    private JDBCConnection getJdbcConnection(String configKey, JDBCConnection existingConnection) {
        return existingConnection == null || existingConnection.isClosed() ? createConnection(configKey) : existingConnection;
    }

    private JDBCConnection createConnection(String configKey) {
        try {
            log.info("Creating DB connection for configKey: [{0}]", configKey);
            ManagedDBConfig managedDBConfig = CDI.current().select(ManagedDBConfig.class, new DBConnection.Literal(configKey)).get();
            log.info("DB connection url [{0}], user: [{1}]", managedDBConfig.getUrl(), managedDBConfig.getUser());
            return CDI.current().select(JDBCConnection.class).get().withConfig(managedDBConfig);
        } catch (Exception e) {
            log.error(MessageFormat.format("Exception on initializing DB connection for configKey: [{0}], [{1}]", configKey, e.getLocalizedMessage()),
                    e);
            return null;
        }
    }

    /**
     * Destroys created Connections
     */
    @PreDestroy
    public void clear() {
        log.trace("Closing open connections...");
        for (JDBCConnection connection : connectionInstances.values()) {
            connection.close();
        }
        connectionInstances.clear();
    }

}

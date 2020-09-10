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

import java.text.MessageFormat;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.oracle.annotation.DBConnection;
import hu.icellmobilsoft.roaster.oracle.connection.JDBCConnection;
import hu.icellmobilsoft.roaster.oracle.service.OracleJDBCSelectorService;

/**
 * Producer for OracleJDBCService
 *
 * @author balazs.joo
 * @since 0.0.2
 */
@ApplicationScoped
public class OracleJDBCServiceProducer {

    private final Logger log = Logger.getLogger(OracleJDBCServiceProducer.class);

    /**
     * Produces OracleJDBCService for the DB connection specified by the given configKey
     *
     * @param injectionPoint
     */
    @Dependent
    @Produces
    @DBConnection(configKey = "")
    public OracleJDBCSelectorService getOracleJDBCSelectorService(InjectionPoint injectionPoint) throws BaseException {
        Optional<DBConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, DBConnection.class);
        String configKey = annotation.map(DBConnection::configKey)
                .orElseThrow(() -> new BaseException(CoffeeFaultType.INVALID_INPUT, "configKey value not found!"));

        JDBCConnection connection = CDI.current().select(JDBCConnection.class, new DBConnection.Literal(configKey)).get();
        if (connection != null) {
            log.trace("Creating OracleJDBCSelectorService...");
            OracleJDBCSelectorService oracleJDBCSelectorService = CDI.current().select(OracleJDBCSelectorService.class).get();
            oracleJDBCSelectorService.setJdbcConnection(connection);
            return oracleJDBCSelectorService;
        }
        throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                MessageFormat.format("Error occurred while creating OracleJDBCSelectorService for configKey [{0}]!", configKey));
    }

    /**
     * Close connection when disposed
     */
    public void returnResource(@Disposes @DBConnection(configKey = "") OracleJDBCSelectorService oracleJDBCSelectorService) {
        if (oracleJDBCSelectorService != null) {
            log.trace("Closing OracleJDBCSelectorService...");
        }
    }

}

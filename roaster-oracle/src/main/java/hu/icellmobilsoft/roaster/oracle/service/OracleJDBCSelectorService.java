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
package hu.icellmobilsoft.roaster.oracle.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction2;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.oracle.connection.JDBCConnection;

/**
 * Class representing Oracle JDBC functionality
 *
 * @author balazs.joo
 */
@Dependent
public class OracleJDBCSelectorService {

    private static final String ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION = "Error occurred during DB connection!";
    private static final String ERROR_MSG_ENTITY_NOT_FOUND = "Entity not found!";
    private static final String ERROR_MSG_SQL_STRING_IS_BLANK = "Input parameter SQL string is blank!";
    private static final String ERROR_MSG_COLUMN_NAME_IS_BLANK = "Input parameter columnName is blank!";
    private static final String ERROR_MSG_CONVERTER_IS_NULL = "Input parameter converter is null!";
    private static final String ERROR_MSG_COULD_NOT_RETRIEVE_COLUMN_VALUE = "Could not retrieve column [{0}] value!";

    private final Logger log = Logger.getLogger(OracleJDBCSelectorService.class);

    private JDBCConnection jdbcConnection;

    /**
     * Default constructor, constructs a new object.
     */
    public OracleJDBCSelectorService() {
        super();
    }

    /**
     * Run sql select command, and return given column String value
     *
     * @param sql
     *            sql command
     * @param columnName
     *            column name
     * @return column String value
     * @throws BaseException
     *             exception
     */
    public String selectFirstStringValue(String sql, String columnName) throws BaseException {
        return getFirstValue(sql, columnName, this::getStringColumnValue);
    }

    /**
     * Run sql select command, and return given column Integer value
     *
     * @param sql
     *            sql command
     * @param columnName
     *            column name
     * @return column Integer value
     * @throws BaseException
     *             exception
     */
    public Integer selectFirstIntegerValue(String sql, String columnName) throws BaseException {
        return getFirstValue(sql, columnName, this::getIntegerColumnValue);
    }

    /**
     * Run sql select command, and return given column Boolean value
     *
     * @param sql
     *            sql command
     * @param columnName
     *            column name
     * @return column Boolean value
     * @throws BaseException
     *             exception
     */
    public Boolean selectFirstBooleanValue(String sql, String columnName) throws BaseException {
        return getFirstValue(sql, columnName, this::getBooleanColumnValue);
    }

    /**
     * Run sql select command, and return given column BigDecimal value
     *
     * @param sql
     *            sql command
     * @param columnName
     *            column name
     * @return column BigDecimal value
     * @throws BaseException
     *             exception
     */
    public BigDecimal selectFirstBigDecimalValue(String sql, String columnName) throws BaseException {
        return getFirstValue(sql, columnName, this::getBigDecimalColumnValue);
    }

    /**
     * Run sql select command, and return given column Date value
     *
     * @param sql
     *            sql command
     * @param columnName
     *            column name
     * @return column Date value
     * @throws BaseException
     *             exception
     */
    public Date selectFirstDateValue(String sql, String columnName) throws BaseException {
        return getFirstValue(sql, columnName, this::getDateColumnValue);
    }

    /**
     * Run sql select command, and return row count
     *
     * @param sql
     *            sql command
     * @return row count
     * @throws BaseException
     *             exception
     */
    public Integer selectRowCount(String sql) throws BaseException {
        if (StringUtils.isBlank(sql)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, ERROR_MSG_SQL_STRING_IS_BLANK);
        }
        try (Connection connection = jdbcConnection.getConnection();
                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(sql)) {
            rs.last();
            return rs.getRow();
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION, e);
        }
    }

    /**
     * Run sql select command, and return desired result object, converted by given converter
     * <p>
     * Example for converter: <code>
     * private T convert(ResultSet rs) {
     * try {
     * T t = new T();
     * t.setId(rs.getString("X__ID"));
     * t.setName(rs.getString("NAME"));
     * return t;
     * } catch (SQLException e) {
     * return null;
     * }
     * }
     * </code>
     *
     * @param sql
     *            sql command
     * @param converter
     *            converter for desired result object
     * @param <T>
     *            type of return object
     * @return converted object
     * @throws BaseException
     *             exception
     */
    public <T> T selectFirstObject(String sql, BaseExceptionFunction<ResultSet, T> converter) throws BaseException {
        if (StringUtils.isBlank(sql)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, ERROR_MSG_SQL_STRING_IS_BLANK);
        }
        if (Objects.isNull(converter)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, ERROR_MSG_CONVERTER_IS_NULL);
        }
        try (Connection connection = jdbcConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return converter.apply(rs);
            } else {
                throw new BONotFoundException(ERROR_MSG_ENTITY_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION, e);
        }
    }

    /**
     * Run sql select command, and return list of desired result objects, converted by given converter
     * <p>
     * Example for converter: <code>
     * private T convert(ResultSet rs) {
     * try {
     * T t = new T();
     * t.setId(rs.getString("X__ID"));
     * t.setName(rs.getString("NAME"));
     * return t;
     * } catch (SQLException e) {
     * return null;
     * }
     * }
     * </code>
     *
     * @param sql
     *            sql command
     * @param converter
     *            converter for desired result objects
     * @param <T>
     *            type of return objects
     * @return list of converted objects
     * @throws BaseException
     *             exception
     */
    public <T> List<T> selectAllObjects(String sql, BaseExceptionFunction<ResultSet, T> converter) throws BaseException {
        if (StringUtils.isBlank(sql)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, ERROR_MSG_SQL_STRING_IS_BLANK);
        }
        if (Objects.isNull(converter)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, ERROR_MSG_CONVERTER_IS_NULL);
        }
        try (Connection connection = jdbcConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            List<T> resultList = new ArrayList<>();
            while (rs.next()) {
                resultList.add(converter.apply(rs));
            }
            return resultList;
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION, e);
        }
    }

    private <T> T getFirstValue(String sql, String columnName, BaseExceptionFunction2<ResultSet, String, T> function)
            throws BaseException {
        if (StringUtils.isBlank(sql)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, ERROR_MSG_SQL_STRING_IS_BLANK);
        }
        if (StringUtils.isBlank(columnName)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, ERROR_MSG_COLUMN_NAME_IS_BLANK);
        }
        try (Connection connection = jdbcConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return function.apply(rs, columnName);
            } else {
                throw new BONotFoundException(ERROR_MSG_ENTITY_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, ERROR_MSG_ERROR_OCCURRED_DURING_DB_CONNECTION, e);
        }
    }

    private String getStringColumnValue(ResultSet rs, String columnName) throws BaseException {
        try {
            return rs.getString(columnName);
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                    MessageFormat.format(ERROR_MSG_COULD_NOT_RETRIEVE_COLUMN_VALUE, columnName), e);
        }
    }

    private Integer getIntegerColumnValue(ResultSet rs, String columnName) throws BaseException {
        try {
            return rs.getInt(columnName);
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                    MessageFormat.format(ERROR_MSG_COULD_NOT_RETRIEVE_COLUMN_VALUE, columnName), e);
        }
    }

    private BigDecimal getBigDecimalColumnValue(ResultSet rs, String columnName) throws BaseException {
        try {
            return rs.getBigDecimal(columnName);
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                    MessageFormat.format(ERROR_MSG_COULD_NOT_RETRIEVE_COLUMN_VALUE, columnName), e);
        }
    }

    private Boolean getBooleanColumnValue(ResultSet rs, String columnName) throws BaseException {
        try {
            return rs.getBoolean(columnName);
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                    MessageFormat.format(ERROR_MSG_COULD_NOT_RETRIEVE_COLUMN_VALUE, columnName), e);
        }
    }

    private Date getDateColumnValue(ResultSet rs, String columnName) throws BaseException {
        try {
            return rs.getDate(columnName);
        } catch (SQLException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                    MessageFormat.format(ERROR_MSG_COULD_NOT_RETRIEVE_COLUMN_VALUE, columnName), e);
        }
    }

    /**
     * Getter for the field {@code jdbcConnection}.
     *
     * @return configKey
     */
    protected JDBCConnection getJdbcConnection() {
        return jdbcConnection;
    }

    /**
     * Sets the new jdbcConnection value
     *
     * @param jdbcConnection
     *            new jdbcConnection value
     */
    public void setJdbcConnection(JDBCConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }
}

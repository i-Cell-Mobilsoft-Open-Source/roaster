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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * DB configuration values
 *
 * @author balazs.joo
 */
public interface DBConfig {

    /**
     * <p>
     * getUrl.
     * </p>
     * 
     * @return url value
     * @throws BaseException
     *             exception
     */
    String getUrl() throws BaseException;

    /**
     * <p>
     * getUser.
     * </p>
     * 
     * @return user value
     * @throws BaseException
     *             exception
     */
    String getUser() throws BaseException;

    /**
     * <p>
     * getPassword.
     * </p>
     * 
     * @return password value
     * @throws BaseException
     *             exception
     */
    String getPassword() throws BaseException;

    /**
     * Returns the maximus size of the connection pool
     * 
     * @return the maximus size of the connection pool
     * @throws BaseException
     *             exception
     */
    int getMaximumPoolSize() throws BaseException;

}

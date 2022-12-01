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
package hu.icellmobilsoft.roaster.oracle.constatns;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * Types of databases can be handled by Roaster
 *
 * @author balazs.joo
 */
public enum DBTypeEnum {

    /**
     * Oracle RDBMS
     */
    ORACLE("oracle"),
    ;

    private final String value;

    DBTypeEnum(String v) {
        value = v;
    }

    /**
     * Returns the value
     * 
     * @return the value
     */
    public String value() {
        return value;
    }

    /**
     * Returns an enum with the given value
     * 
     * @param value
     *            search value
     * @return an enum with the given value
     */
    public static DBTypeEnum fromValue(String value) {
        return Arrays.stream(values()).filter(dbTypeEnum -> StringUtils.equals(dbTypeEnum.value, value)).findFirst().orElse(null);
    }
}

/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.testsuite.jaxb.dto;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseRequest;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * Dto helper class
 * 
 * @author imre.scheffer
 * @since 0.8.0
 */
public class DtoHelper {

    /**
     * Currently not CDI bean
     */
    private DtoHelper() {
    }

    /**
     * Create filled {@code BaseRequest}
     * 
     * @return new filled {@code BaseRequest}
     */
    public static BaseRequest createBaseRequest() {
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.withContext(createContextType());
        return baseRequest;
    }

    /**
     * Create filled {@code ContextType}
     * 
     * @return new filled {@code ContextType}
     */
    public static ContextType createContextType() {
        return new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTC());
    }
}

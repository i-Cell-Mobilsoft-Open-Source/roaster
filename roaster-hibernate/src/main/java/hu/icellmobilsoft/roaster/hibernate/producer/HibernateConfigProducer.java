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
package hu.icellmobilsoft.roaster.hibernate.producer;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.hibernate.annotation.HibernatePersistenceConfig;
import hu.icellmobilsoft.roaster.hibernate.config.HibernateConfig;

/**
 * Producer for creating HibernateConfigImpl
 *
 * @author speter555
 */
@ApplicationScoped
public class HibernateConfigProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public HibernateConfigProducer() {
        super();
    }

    /**
     * Creates HibernateConfigImpl for the injected configKey
     * 
     * @param injectionPoint
     *            CDI injection point
     * @return created class
     * @throws BaseException
     *             exception
     */
    @Produces
    @Dependent
    @HibernatePersistenceConfig(persistenceUnitName = "")
    public HibernateConfig getDBConfig(InjectionPoint injectionPoint) throws BaseException {
        Optional<HibernatePersistenceConfig> annotation = AnnotationUtil.getAnnotation(injectionPoint, HibernatePersistenceConfig.class);
        String configKey = annotation.map(HibernatePersistenceConfig::persistenceUnitName)
                .orElseThrow(() -> new BaseException(CoffeeFaultType.INVALID_INPUT, "configKey value not found!"));
        HibernateConfig hibernateConfig = CDI.current().select(HibernateConfig.class).get();
        hibernateConfig.setConfigKey(configKey);
        return hibernateConfig;
    }

}

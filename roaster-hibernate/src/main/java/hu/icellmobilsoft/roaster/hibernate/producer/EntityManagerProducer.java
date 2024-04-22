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
/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package hu.icellmobilsoft.roaster.hibernate.producer;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.roaster.hibernate.annotation.HibernatePersistenceConfig;

/**
 * Producer for creating or obtaining {@link EntityManager}
 * 
 * @since 0.2.0
 * @author speter555
 * @author csaba.balogh
 */
@ApplicationScoped
public class EntityManagerProducer {

    private final Logger logger = Logger.getLogger(EntityManagerProducer.class);

    /**
     * Default constructor, constructs a new object.
     */
    public EntityManagerProducer() {
        super();
    }

    /**
     * Producer for creating or obtaining {@link EntityManager} with defaultPU persistenceUnitName
     *
     * @param injectionPoint
     *            CDI injection point
     * @return {@link EntityManager} instance
     */
    @Produces
    @Dependent
    public EntityManager produceDefaultEntityManager(InjectionPoint injectionPoint) {
        EntityManagerFactory emf = CDI.current().select(EntityManagerFactory.class).get();
        return emf.createEntityManager();
    }

    /**
     * Producer for creating or obtaining {@link EntityManager}
     *
     * @param injectionPoint
     *            CDI injection point
     * @return {@link EntityManager} instance
     * 
     * @throws BaseException
     *             exception
     */
    @Produces
    @Dependent
    @HibernatePersistenceConfig(persistenceUnitName = "")
    public EntityManager produceEntityManager(InjectionPoint injectionPoint) throws BaseException {
        Optional<HibernatePersistenceConfig> annotation = AnnotationUtil.getAnnotation(injectionPoint, HibernatePersistenceConfig.class);
        HibernatePersistenceConfig hibernatePersistenceConfig = annotation
                .orElseThrow(() -> new BaseException(CoffeeFaultType.INVALID_INPUT, "PersistenceUnitName annotation have to have configKey value!"));
        EntityManagerFactory emf = CDI.current().select(EntityManagerFactory.class, hibernatePersistenceConfig).get();
        return emf.createEntityManager();
    }

    /**
     * Close EntityManager instance
     *
     * @param entityManager
     *            instance
     */
    public void close(@Disposes @HibernatePersistenceConfig(persistenceUnitName = "") EntityManager entityManager) {
        if (entityManager != null) {
            logger.trace("Closing EntityManager...");
            entityManager.close();
        }
    }

    /**
     * Close EntityManager instance
     *
     * @param entityManager
     *            instance
     */
    public void defaultClose(@Disposes EntityManager entityManager) {
        if (entityManager != null) {
            logger.trace("Closing EntityManager...");
            entityManager.close();
        }
    }

}

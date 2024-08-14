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
/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package hu.icellmobilsoft.roaster.testsuite.hibernate;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;

import hu.icellmobilsoft.roaster.hibernate.annotation.HibernatePersistenceConfig;

/**
 * Test of EntityManagerProducer with custom persistence units.
 *
 * @author hkrisztian96
 * @since 2.3.0
 */
@DisplayName("Test of EntityManagerProducer with custom persistence units")
class EntityManagerProducerCustomPUIT extends AbstractEntityManagerProducerIT {

    @Inject
    @HibernatePersistenceConfig(persistenceUnitName = "defaultPU")
    private EntityManager em1;

    @Inject
    @HibernatePersistenceConfig(persistenceUnitName = "defaultPU")
    private EntityManager em2;

    @Override
    protected EntityManager getEntityManager1() {
        return em1;
    }

    @Override
    protected EntityManager getEntityManager2() {
        return em2;
    }
}

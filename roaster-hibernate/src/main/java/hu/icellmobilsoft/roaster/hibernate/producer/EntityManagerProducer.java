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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Producer for creating or obtaining {@link EntityManager}
 * 
 * @since 0.2.0
 * @author speter555
 */
@ApplicationScoped
public class EntityManagerProducer {

    @Inject
    private EntityManagerFactory entityManagerFactory;

    /**
     * Producer for creating or obtaining {@link EntityManager}
     * 
     * @return {@link EntityManager} instance
     */
    @Produces
    @ApplicationScoped
    public EntityManager produceEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Close EntityManager instance
     *
     * @param entityManager
     *            instance
     */
    public void close(@Disposes EntityManager entityManager) {
        entityManager.close();
    }
}

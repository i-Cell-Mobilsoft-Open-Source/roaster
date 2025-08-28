/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.hibernate.se;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.EntityManager;

/**
 * JVM level cache for {@link EntityManager} instances.
 *
 * @author martin.nagy
 * @since 2.6.0
 */
public class EntityManagerCache {
    private static final Map<String, ThreadLocal<EntityManager>> ENTITY_MANAGERS_BY_PU = new ConcurrentHashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private EntityManagerCache() {
    }

    /**
     * Returns an EntityManager for the given persistence unit name.
     * 
     * @param persistenceUnitName
     *            name of the persistence unit for which the EntityManager should be returned.
     * @return EntityManager for the given persistence unit name.
     */
    public static EntityManager getForPersistenceUnit(String persistenceUnitName) {
        return ENTITY_MANAGERS_BY_PU.computeIfAbsent(persistenceUnitName, EntityManagerCache::getEntityManagerThreadLocal).get();
    }

    private static ThreadLocal<EntityManager> getEntityManagerThreadLocal(String persistenceUnitName) {
        return ThreadLocal.withInitial(() -> EntityManagerFactoryCache.getForPersistenceUnit(persistenceUnitName).createEntityManager());
    }
}

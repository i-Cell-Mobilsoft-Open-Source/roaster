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
package hu.icellmobilsoft.roaster.hibernate.common.helper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.persistence.EntityManager;

/**
 * A container class for managing a collection of entities using an {@link EntityManager}. This class provides transactional support for inserting,
 * deleting, and managing entities. It also ensures thread-safety for operations involving the internal entity list. <br>
 * Usage example:
 * 
 * <pre>
 * {@code
 * import org.junit.jupiter.api.AutoClose;
 *
 * @AutoClose
 * private final EntityContainer entityContainer = new EntityContainer(entityManager);
 *
 * @Test
 * void test() {
 *     entityContainer.insert(new ExampleEntity());
 * }
 * }
 * </pre>
 *
 * @author martin.nagy
 * @since 2.8.0
 */
public class EntityContainer implements AutoCloseable {
    private final List<Object> entities = new CopyOnWriteArrayList<>();
    private final EntityManager entityManager;

    /**
     * Constructs an instance of the EntityContainer with the provided EntityManager.
     *
     * @param entityManager
     *            the EntityManager used to manage entities within the container
     */
    public EntityContainer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Inserts the given entity into the container by executing the operation within a transactional context. This method ensures that the provided
     * entity is processed in a transaction by utilizing the {@link TransactionHelper}.
     *
     * @param entity
     *            the entity to be inserted into the transaction and managed by the container
     */
    public void insert(Object entity) {
        TransactionHelper.runInTransaction(entityManager, () -> insertInAlreadyExistingTransaction(entity));
    }

    /**
     * Inserts the given entity into a pre-existing transaction context. The method persists the entity using the {@link EntityManager} and adds it to
     * an internal list of managed entities.
     *
     * @param entity
     *            the entity to be added to the transaction and managed entity list
     */
    public void insertInAlreadyExistingTransaction(Object entity) {
        entityManager.persist(entity);
        entities.add(entity);
    }

    /**
     * Deletes all entities from the container and clears the internal entity list. <br>
     * This method performs the removal of entities in a thread-safe manner and ensures the removal operations are executed within a transactional
     * context. <br>
     * If the entity list is empty, the method returns immediately without any further processing. Otherwise, it locks the entity list, iterates over
     * it in reverse order to remove entities using the provided {@link EntityManager}, and clears the list afterward.
     */
    public void deleteAll() {
        if (entities.isEmpty()) {
            return;
        }
        synchronized (entities) {
            TransactionHelper.runInTransaction(entityManager, () -> entities.reversed().forEach(entityManager::remove));
            entities.clear();
        }
    }

    @Override
    public void close() {
        deleteAll();
    }
}

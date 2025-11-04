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

import java.util.function.Supplier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * The {@code TransactionHelper} class provides utility methods to execute actions or retrieve results within a transactional context using an
 * {@link EntityManager}. These methods abstract away the complexities of handling transaction lifecycles, such as beginning, committing, and rolling
 * back transactions.
 *
 * @author martin.nagy
 * @since 2.8.0
 */
public class TransactionHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private TransactionHelper() {
    }

    /**
     * Executes the given {@code Runnable} within a transactional context using the provided {@link EntityManager}. If the transaction is already
     * active, the {@code Runnable} is executed without starting a new transaction. If the transaction is not active, a new transaction is started,
     * and the {@code Runnable} is executed. Upon successful execution, the transaction is committed. If an exception occurs during execution, the
     * transaction is rolled back, and the exception is rethrown.
     *
     * @param em
     *            the {@link EntityManager} to be used for managing the transaction
     * @param runnable
     *            the {@code Runnable} to execute within the transactional context
     * @throws RuntimeException
     *             if an exception occurs during the {@code Runnable}'s execution, it is propagated after a rollback
     */
    public static void runInTransaction(EntityManager em, Runnable runnable) {
        EntityTransaction transaction = em.getTransaction();

        if (transaction.isActive()) {
            runnable.run();
            return;
        }

        transaction.begin();
        try {
            runnable.run();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Executes the given supplier within a transactional context using the provided {@link EntityManager}. If the transaction is already active, the
     * supplier is executed without starting a new transaction. Otherwise, a new transaction is started, and the supplier's result is returned. Any
     * exception during execution will result in a rollback, and the exception is rethrown.
     *
     * @param <T>
     *            the type of the result returned by the supplier
     * @param em
     *            the {@link EntityManager} to be used for managing the transaction
     * @param supplier
     *            the supplier to execute within the transactional context
     * @return the result of the supplier's execution
     * @throws RuntimeException
     *             if the supplier throws an exception during execution, it is propagated after a rollback
     */
    public static <T> T runInTransaction(EntityManager em, Supplier<T> supplier) {
        EntityTransaction transaction = em.getTransaction();

        if (transaction.isActive()) {
            return supplier.get();
        }

        transaction.begin();
        try {
            T result = supplier.get();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}

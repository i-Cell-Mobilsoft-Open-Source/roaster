/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.testsuite.hibernate;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.testsuite.hibernate.model.MyTable;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Abstract test class for EntityManagerProducer test cases.
 *
 * @author hkrisztian96
 * @since 2.3.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // Using PER_METHOD to make sure the container will shut down between the tests.
abstract class AbstractEntityManagerProducerIT extends BaseWeldUnitType {

    private MyTable mockDeclaration;

    @AfterEach
    void cleanUp() {
        deleteTestData(mockDeclaration);
    }

    @Test
    void testEntityManagerFirstUsage() {
        test();
    }

    @Test
    void testEntityManagerSecondUsage() {
        test();
    }

    /**
     * Returns the first {@link EntityManager} instance of the test class.
     *
     * @return {@link EntityManager}.
     */
    protected abstract EntityManager getEntityManager1();

    /**
     * Returns the second {@link EntityManager} instance of the test class.
     *
     * @return {@link EntityManager}.
     */
    protected abstract EntityManager getEntityManager2();

    private void test() {
        mockDeclaration = insertTestData(new MyTable());
    }

    private MyTable insertTestData(MyTable testData) {
        EntityManager em1 = getEntityManager1();
        EntityManager em2 = getEntityManager2();
        try {
            em1.getTransaction().begin();
            em1.persist(testData);
            em1.getTransaction().commit();
            em2.getTransaction().begin();
            em2.persist(testData);
            em2.getTransaction().commit();
            return testData;
        } catch (RuntimeException e) {
            em1.getTransaction().rollback();
            em2.getTransaction().rollback();
            throw e;
        }
    }

    private void deleteTestData(MyTable testData) {
        EntityManager em1 = getEntityManager1();
        EntityManager em2 = getEntityManager2();
        em1.getTransaction().begin();
        em1.remove(em1.merge(testData));
        em1.getTransaction().commit();
        em2.getTransaction().begin();
        em2.remove(em2.merge(testData));
        em2.getTransaction().commit();
    }

}

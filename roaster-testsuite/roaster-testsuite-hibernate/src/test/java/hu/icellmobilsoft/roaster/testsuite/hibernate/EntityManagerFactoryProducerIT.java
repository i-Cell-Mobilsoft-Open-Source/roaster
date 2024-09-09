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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.testsuite.hibernate.model.MyTable;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Test of EntityManagerFactoryProducer.
 *
 * @author attila-kiss-it
 * @since 2.2.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@DisplayName("Test of EntityManagerFactoryProducer")
@TestInstance(Lifecycle.PER_METHOD) // Using PER_METHOD to make sure the container will shut down between the tests.
class EntityManagerFactoryProducerIT extends BaseWeldUnitType {

    @Inject
    private EntityManager em;

    private MyTable mockDeclaration;

    @AfterEach
    void cleanUp() {
        deleteTestData(mockDeclaration);
    }

    @Test
    void testEntityManagerFirstUsage() throws BaseException {
        test();
    }

    @Test
    void testEntityManagerSecondUsage() throws BaseException {
        test();
    }

    private void test() throws BaseException {
        mockDeclaration = insertTestData(new MyTable());
    }

    private MyTable insertTestData(MyTable testData) {
        try {
            em.getTransaction().begin();
            em.persist(testData);
            em.getTransaction().commit();
            return testData;
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    private void deleteTestData(MyTable testData) {
        em.getTransaction().begin();
        em.remove(em.merge(testData));
        em.getTransaction().commit();
    }

}

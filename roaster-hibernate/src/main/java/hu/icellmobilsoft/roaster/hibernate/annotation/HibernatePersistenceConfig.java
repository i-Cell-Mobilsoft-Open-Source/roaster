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
package hu.icellmobilsoft.roaster.hibernate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import hu.icellmobilsoft.roaster.hibernate.config.HibernateConfig;

/**
 * Qualifier for set ConfigKey for setup Hibernate over EntityManager
 *
 * @author speter555
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface HibernatePersistenceConfig {

    /**
     * Persistence unit name
     * 
     * @return the name of the persistence unit
     */
    @Nonbinding
    String persistenceUnitName() default HibernateConfig.DEFAULT_PERSISTENCE_UNIT_NAME;

    /**
     * Supports inline instantiation of the {@link HibernatePersistenceConfig} qualifier.
     *
     * @author speter555
     */
    final class Literal extends AnnotationLiteral<HibernatePersistenceConfig> implements HibernatePersistenceConfig {

        private static final long serialVersionUID = 1L;

        /**
         * persistenceUnitName
         */
        private final String persistenceUnitName;

        /**
         * Instantiates the literal with persistenceUnitName
         *
         * @param persistenceUnitName
         *            persistenceUnitName
         */
        public Literal(String persistenceUnitName) {
            this.persistenceUnitName = persistenceUnitName;
        }

        @Nonbinding
        public String persistenceUnitName() {
            return persistenceUnitName;
        }

    }
}

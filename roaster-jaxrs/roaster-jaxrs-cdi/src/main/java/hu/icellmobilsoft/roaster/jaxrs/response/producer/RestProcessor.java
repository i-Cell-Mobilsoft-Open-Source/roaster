/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.jaxrs.response.producer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * CDI qualifier for configuring ResponseProcessor
 *
 * @author martin.nagy
 * @since 0.5.0
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface RestProcessor {

    /**
     * Config key of the rest endpoint<br>
     * ie. if endpoint details are defined in the project-*.yml by the keys: {@code testsuite.example.*=...} then configKey should be
     * "{@literal testsuite.example}"
     *
     * @return config key
     */
    @Nonbinding
    String configKey();

    /**
     * Expected REST response status code
     *
     * @return Expected REST response status code
     */
    @Nonbinding
    int expectedStatusCode() default 200;

    /**
     * Supports inline instantiation of the {@link RestProcessor} qualifier.
     */
    final class Literal extends AnnotationLiteral<RestProcessor> implements RestProcessor {
        private static final long serialVersionUID = 1L;

        /**
         * config key
         */
        private final String configKey;

        /**
         * expected HTTP status code
         */
        private final int expectedStatusCode;

        /**
         * Instantiates the literal with configKey
         *
         * @param configKey
         *            config key
         */
        public Literal(String configKey) {
            this(configKey, 200);
        }

        /**
         * Instantiates the literal with configKey and expected HTTP status code
         *
         * @param configKey
         *            config key
         * @param expectedStatusCode
         *            expected HTTP status code
         */
        public Literal(String configKey, int expectedStatusCode) {
            this.configKey = configKey;
            this.expectedStatusCode = expectedStatusCode;
        }

        @Nonbinding
        public String configKey() {
            return configKey;
        }

        @Override
        public int expectedStatusCode() {
            return expectedStatusCode;
        }
    }

}

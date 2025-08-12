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
package hu.icellmobilsoft.roaster.restassured;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.inject.Inject;

import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.WeldInitiator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BaseConfigurableWeldITTest extends BaseConfigurableWeldIT {
    @Inject
    private Example example;

    @Test
    void exampleTest(Example example) {
        Assertions.assertNotNull(example);
        Assertions.assertEquals("Hello!", example.hello());
    }

    @Override
    protected void configureWeldInitiatorBuilder(WeldInitiator.Builder weldInitiatorBuilder) {
        weldInitiatorBuilder.addBeans(createExampleBean());
    }

    Bean<?> createExampleBean() {
        return MockBean.builder().types(Example.class).scope(ApplicationScoped.class).creating(new Example()).build();
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 2", "2, 3, 5", })
    @ExplicitParamInjection
    void test(int a, int b, int expectedSum) {
        Assertions.assertEquals(expectedSum, a + b);
    }
}

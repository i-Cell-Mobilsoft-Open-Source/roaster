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
package hu.icellmobilsoft.roaster.module.testdoc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import hu.icellmobilsoft.roaster.testdoc.TestDoc;

/**
 * {@link TestDoc} example test class used in {@link TestDocTest}
 * 
 * @author janos.hamrak
 * @since 2.3.0
 */
@TestDoc(titleHeadingLevel = 3)
@DisplayName("Example test class")
class TestDocExampleTest {

    @Test
    @DisplayName("Example test method in TestDocExampleTest class")
    @Disabled("only for example")
    void exampleTestMethod() {
        Assertions.assertTrue(true);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Example parameterized test method in TestDocExampleTest class")
    @Disabled("only for example")
    void exampleParameterizedTestMethod(String param) {
        Assertions.assertNull(param);
    }
}

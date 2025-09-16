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
package hu.icellmobilsoft.roaster.testname;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestNameCaptorExtension.class)
class TestNameCaptorExtensionTest {

    @BeforeAll
    static void beforeAll() {
        assertEquals(TestNameCaptorExtensionTest.class, TestNameCaptorExtension.getCurrentTestClass());
        assertNull(TestNameCaptorExtension.getCurrentTestMethod());

        assertEquals("TestNameCaptorExtensionTest", TestNameUtil.getTestMethodName());
        assertEquals("TNCET", TestNameUtil.getTestMethodName(20));
        assertEquals("TNC", TestNameUtil.getTestMethodName(2));
    }

    @BeforeEach
    void setUp() {
        assertEquals(TestNameCaptorExtensionTest.class, TestNameCaptorExtension.getCurrentTestClass());
        assertTrue(TestNameCaptorExtension.getCurrentTestMethod().getName().startsWith("testMethod"));
    }

    @Test
    void testMethod1() {
        assertEquals(TestNameCaptorExtensionTest.class, TestNameCaptorExtension.getCurrentTestClass());
        assertEquals("testMethod1", TestNameCaptorExtension.getCurrentTestMethod().getName());

        assertEquals("TestNameCaptorExtensionTest.testMethod1", TestNameUtil.getTestMethodName());
        assertEquals("TNCET.testMethod1", TestNameUtil.getTestMethodName(20));
        assertEquals("TNCET.TM", TestNameUtil.getTestMethodName(15));
    }

    @Test
    void testMethod2() {
        assertEquals(TestNameCaptorExtensionTest.class, TestNameCaptorExtension.getCurrentTestClass());
        assertEquals("testMethod2", TestNameCaptorExtension.getCurrentTestMethod().getName());

        assertEquals("TestNameCaptorExtensionTest.testMethod2", TestNameUtil.getTestMethodName());
        assertEquals("TNCET.testMethod2", TestNameUtil.getTestMethodName(20));
        assertEquals("TNCET.TM", TestNameUtil.getTestMethodName(15));
    }
}

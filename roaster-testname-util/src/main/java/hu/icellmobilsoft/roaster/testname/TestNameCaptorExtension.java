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

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Stores the current test class and method in a thread local. Can be used directly to get the full test name, or can be used with
 * {@link TestNameUtil} to get the abbreviated name for generating request ids, SIDs, database IDs, etc.
 *
 * @author martin-nagy
 * @since 2.7.0
 */
public class TestNameCaptorExtension implements BeforeTestExecutionCallback, BeforeEachCallback, BeforeAllCallback, AfterTestExecutionCallback,
        AfterEachCallback, AfterAllCallback {

    private static final ThreadLocal<Class<?>> currentTestClass = new ThreadLocal<>();
    private static final ThreadLocal<Method> currentTestMethod = new ThreadLocal<>();

    /**
     * Default constructor
     */
    public TestNameCaptorExtension() {
        // empty
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        store(context);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        store(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        store(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        clear();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        clear();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        clear();
    }

    private void store(ExtensionContext context) {
        currentTestClass.set(context.getTestClass().orElse(null));
        currentTestMethod.set(context.getTestMethod().orElse(null));
    }

    private void clear() {
        currentTestClass.remove();
        currentTestMethod.remove();
    }

    /**
     * Returns the current test class.
     * @return current test class
     */
    public static Class<?> getCurrentTestClass() {
        return currentTestClass.get();
    }

    /**
     * Returns the current test method.
     * @return current test method
     */
    public static Method getCurrentTestMethod() {
        return currentTestMethod.get();
    }
}

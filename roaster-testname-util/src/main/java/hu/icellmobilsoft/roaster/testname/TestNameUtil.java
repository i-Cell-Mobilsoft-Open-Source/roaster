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

import org.apache.commons.lang3.StringUtils;

/**
 * Utility to generate abbreviated test names.
 * 
 * @see TestNameCaptorExtension
 * 
 * @author martin-nagy
 * @since 2.7.0
 */
public class TestNameUtil {

    /**
     * Private constructor to avoid instantiation.
     */
    private TestNameUtil() {
    }

    /**
     * Returns the full test method name
     * 
     * @return full test method name
     */
    public static String getTestMethodName() {
        return getTestMethodName(Integer.MAX_VALUE);
    }

    /**
     * Returns the full test method name, or abbreviated if the name is too long.
     * 
     * @param maxLength
     *            returned max length of the returned name
     *
     * @throws IllegalStateException
     *             if {@link TestNameCaptorExtension} was not used in the test class
     * @return the abbreviated test method name
     */
    public static String getTestMethodName(int maxLength) {
        Class<?> currentTestClass = TestNameCaptorExtension.getCurrentTestClass();
        if (currentTestClass == null) {
            throw new IllegalStateException("Test should be used with TestNameCaptorExtension in order to use this method!");
        }
        Method currentTestMethod = TestNameCaptorExtension.getCurrentTestMethod();
        if (currentTestMethod == null) {
            return getSimpleAbbreviation(currentTestClass.getSimpleName(), maxLength);
        }

        String testClassName = currentTestClass.getSimpleName();
        String methodName = currentTestMethod.getName();

        if (testClassName.length() + methodName.length() + 1 <= maxLength) {
            return testClassName + "." + methodName;
        }
        String abbreviatedTestClassName = getUpperCaseLetters(testClassName);
        if (abbreviatedTestClassName.length() + methodName.length() + 1 <= maxLength) {
            return abbreviatedTestClassName + "." + methodName;
        }
        String abbreviatedMethodName = getUpperCaseLetters(StringUtils.capitalize(methodName));
        String abbreviatedFullName = abbreviatedTestClassName + "." + abbreviatedMethodName;
        return StringUtils.substring(abbreviatedFullName, 0, maxLength);
    }

    private static String getSimpleAbbreviation(String name, int maxLength) {
        if (name.length() <= maxLength) {
            return name;
        }
        return StringUtils.substring(getUpperCaseLetters(name), 0, maxLength);
    }

    private static String getUpperCaseLetters(String string) {
        return string.replaceAll("[^A-Z]", "");
    }

}

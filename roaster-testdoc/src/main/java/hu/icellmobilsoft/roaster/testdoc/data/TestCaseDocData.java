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
package hu.icellmobilsoft.roaster.testdoc.data;

import hu.icellmobilsoft.roaster.testdoc.TestDoc;

/**
 * The collected {@link TestDoc} data for one test case.
 *
 * @author janos.hamrak
 * @since 2.3.0
 */
public class TestCaseDocData {
    private final String methodName;
    private final String displayName;

    /**
     * All args constructor.
     *
     * @param methodName
     *            test method name
     * @param displayName
     *            test method display name
     */
    public TestCaseDocData(String methodName, String displayName) {
        this.methodName = methodName;
        this.displayName = displayName;
    }

    /**
     * Returns test method name
     * 
     * @return test method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Returns test method display name
     *
     * @return test method display name
     */
    public String getDisplayName() {
        return displayName;
    }
}

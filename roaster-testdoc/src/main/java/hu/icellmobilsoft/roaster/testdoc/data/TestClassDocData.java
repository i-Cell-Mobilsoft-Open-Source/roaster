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

import java.util.List;

import hu.icellmobilsoft.roaster.testdoc.TestDoc;

/**
 * The collected {@link TestDoc} data for one test class.
 *
 * @author janos.hamrak
 * @since 2.3.0
 */
public class TestClassDocData {

    private final String className;
    private final String displayName;
    private final int titleHeadingLevel;
    private final List<TestCaseDocData> testCaseDocDataList;

    /**
     * All args constructor.
     *
     * @param className
     *            test class name
     * @param displayName
     *            test class display name
     * @param titleHeadingLevel
     *            document table title heading level
     * @param testCaseDocDataList
     *            test cases doc data
     */
    public TestClassDocData(String className, String displayName, int titleHeadingLevel, List<TestCaseDocData> testCaseDocDataList) {
        this.className = className;
        this.displayName = displayName;
        this.titleHeadingLevel = titleHeadingLevel;
        this.testCaseDocDataList = testCaseDocDataList;
    }

    /**
     * Returns test class name
     * 
     * @return test class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns test class display name
     * 
     * @return test class display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns document table title heading level
     *
     * @return document table title heading level
     */
    public int getTitleHeadingLevel() {
        return titleHeadingLevel;
    }

    /**
     * Returns test cases doc data
     *
     * @return test cases doc data
     */
    public List<TestCaseDocData> getTestCaseDocDataList() {
        return testCaseDocDataList;
    }
}

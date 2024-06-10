/*-
 * #%L
 * Coffee
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
package hu.icellmobilsoft.roaster.zephyr.common.api.reporter;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;

import jakarta.enterprise.inject.Vetoed;

/**
 * Defines the common data describing a test case and run parameters.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
@Vetoed
public class TestCaseData {

    /**
     * Unique identifier for the test case. Should contain the test class and method name.
     */
    private String id;

    /**
     * The display name of the test case.
     */
    private String displayName;

    /**
     * The {@code Method} instance for further annotation processing.
     */
    private Method testMethod;

    /**
     * The time the test run started.
     */
    private LocalDateTime startTime;

    /**
     * The time the test run finished.
     */
    private LocalDateTime endTime;

    /**
     * Test tags
     */
    private Collection<String> tags;

    /**
     * The number of test data of the parameterized tests,
     * which is the number of executions of the test case, is 1 by default.
     */
    private long testDataCount;

    /**
     * Default constructor, constructs a new object.
     */
    public TestCaseData() {
        super();
    }

    /**
     * Getter for the field {@code id}.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the new id value
     *
     * @param id
     *            new id value
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the field {@code displayName}.
     *
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the new displayName value
     *
     * @param displayName
     *            new displayName value
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Getter for the field {@code testMethod}.
     *
     * @return testMethod
     */
    public Method getTestMethod() {
        return testMethod;
    }

    /**
     * Sets the new testMethod value
     *
     * @param testMethod
     *            new testMethod value
     */
    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    /**
     * Getter for the field {@code startTime}.
     *
     * @return startTime
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the new startTime value
     *
     * @param startTime
     *            new startTime value
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Getter for the field {@code endTime}.
     *
     * @return endTime
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the new endTime value
     *
     * @param endTime
     *            new endTime value
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Getter for the field {@code tags}.
     *
     * @return tags
     */
    public Collection<String> getTags() {
        return tags;
    }

    /**
     * Sets the new tags value
     *
     * @param tags
     *            new tags value
     */
    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    /**
     * Getter for the field {@code testDataCount}.
     *
     * @return testDataCount
     */
    public long getTestDataCount() {
        return testDataCount;
    }

    /**
     * Sets the new testDataCount value
     *
     * @param testDataCount
     *            new testDataCount value
     */
    public void setTestDataCount(long testDataCount) {
        this.testDataCount = testDataCount;
    }
}

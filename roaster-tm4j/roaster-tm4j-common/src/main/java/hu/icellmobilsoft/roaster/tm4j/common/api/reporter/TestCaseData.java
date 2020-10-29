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
package hu.icellmobilsoft.roaster.tm4j.common.api.reporter;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * Defines the common data describing a test case and run parameters.
 *
 * @author martin.nagy
 * @since 0.2.0
 */
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

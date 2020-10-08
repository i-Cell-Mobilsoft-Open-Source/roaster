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
package hu.icellmobilsoft.roaster.tm4j.junit5;

import hu.icellmobilsoft.roaster.tm4j.common.Tm4jReporter;
import hu.icellmobilsoft.roaster.tm4j.common.Tm4jReporterFactory;
import hu.icellmobilsoft.roaster.tm4j.common.spi.Tm4jRecord;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestWatcher;

import java.time.LocalDateTime;
import java.util.Optional;

public class Tm4jExtension implements TestWatcher, BeforeTestExecutionCallback {
    private static final String START_TIME = "START_TIME";

    private final Tm4jReporter reporter = new Tm4jReporterFactory().createReporter();

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        getStore(context).put(START_TIME, LocalDateTime.now());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        reporter.reportSuccess(createTm4jRecord(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        reporter.reportFail(createTm4jRecord(context), cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        reporter.reportDisabled(createTm4jRecord(context), reason);
    }

    private Tm4jRecord createTm4jRecord(ExtensionContext context) {
        LocalDateTime startTime = getStore(context).remove(START_TIME, LocalDateTime.class);
        LocalDateTime endTime = LocalDateTime.now();

        Tm4jRecord record = new Tm4jRecord();
        record.setId(context.getUniqueId());
        record.setDisplayName(context.getDisplayName());
        record.setTestMethod(context.getRequiredTestMethod());
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        return record;
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}

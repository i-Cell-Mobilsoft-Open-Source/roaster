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
package hu.icellmobilsoft.roaster.zephyr.junit5;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.zephyr.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.zephyr.common.api.reporter.TestResultReporter;

/**
 * JUnit 5 extension to publish the test result to a TM4J server.
 * The test case id should be mapped with the test method via the {@link TestCaseId} annotation.
 *
 * @author martin.nagy
 * @see TestCaseId
 * @since 0.2.0
 */
public class ZephyrExtension implements TestWatcher, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    /**
     * Constant used as JUnit storage key for test run start time
     */
    protected static final String START_TIME = "START_TIME";

    private final Logger log = Logger.getLogger(ZephyrExtension.class);

    private final Supplier<TestResultReporter> reporterSupplier;

    /**
     * Creates an instance with a {@code TestResultReporter} using CDI to get the {@code TestResultReporter} dependency.
     */
    public ZephyrExtension() {
        this(() -> CDI.current().select(TestResultReporter.class).get());
    }

    /**
     * Creates an instance with a {@code TestResultReporter} supplier passed as a parameter.
     *
     * @param reporterSupplier {@code TestResultReporter} supplier defining callbacks for test lifecycle events
     */
    public ZephyrExtension(Supplier<TestResultReporter> reporterSupplier) {
        this.reporterSupplier = Objects.requireNonNull(reporterSupplier);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        getStore(context).put(START_TIME, LocalDateTime.now(ZoneId.systemDefault()));
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        getReporter().reportSuccess(createZephyrRecord(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        getReporter().reportFail(createZephyrRecord(context), cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        getReporter().reportDisabled(createZephyrRecord(context), reason);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        // nothing to do
    }

    private TestResultReporter getReporter() {
        return reporterSupplier.get();
    }

    private TestCaseData createZephyrRecord(ExtensionContext context) {
        TestCaseData record = new TestCaseData();
        record.setId(context.getUniqueId());
        record.setDisplayName(context.getDisplayName());
        record.setTestMethod(context.getRequiredTestMethod());
        record.setStartTime(getStartTime(context));
        record.setEndTime(LocalDateTime.now(ZoneId.systemDefault()));
        record.setTags(context.getTags());
        record.setTestDataCount(getTestDataListCount(context));
        return record;
    }

    private LocalDateTime getStartTime(ExtensionContext context) {
        return getStore(context).get(START_TIME, LocalDateTime.class);
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

    /**
     * Returns the number of the parameters for the test running, this number will multiply the number of the test steps,
     * so it's default 1, it the none of the annotations exist
     *
     * @param context {@code ExtensionContext} context of the test execution
     */
    private long getTestDataListCount(ExtensionContext context) {
        long testDataListCount = 1;
        try {
            for (Annotation annotation : context.getRequiredTestMethod().getDeclaredAnnotations()) {
                if (annotation instanceof MethodSource) {
                    testDataListCount = getTestDataListCount((MethodSource) annotation);
                } else if (annotation instanceof EnumSource) {
                    testDataListCount = getTestDataListCount((EnumSource) annotation);
                } else if (annotation instanceof ArgumentsSource) {
                    testDataListCount = getTestDataListCount((ArgumentsSource) annotation, context);
                } else if (annotation instanceof ValueSource) {
                    testDataListCount = getTestDataListCount((ValueSource) annotation);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred at parameter source annotation processing", e);
        }
        return testDataListCount;
    }

    private long getTestDataListCount(MethodSource ms) throws Exception {
        String[] values = ms.value();
        if (values != null && values.length > 0) {
            String[] split = values[0].split("#");
            Method staticMethod = Class.forName(split[0]).getDeclaredMethod(split[1]);
            Stream<Arguments> result = (Stream<Arguments>) staticMethod.invoke(null);
            return result.count();
        } else {
            return 1;
        }
    }

    private long getTestDataListCount(EnumSource es) {
        String[] names = es.names();
        if (names.length > 0) {
            return names.length;
        } else {
            Class<? extends Enum<?>> enumClass = es.value();
            return enumClass.getEnumConstants().length;
        }
    }

    private long getTestDataListCount(ArgumentsSource as, ExtensionContext context) throws Exception {
        Class<? extends ArgumentsProvider> providerClass = as.value();
        Stream<? extends Arguments> result = providerClass.getConstructor().newInstance().provideArguments(context);
        return result.count();
    }

    private long getTestDataListCount(ValueSource vs) {
        //Now limited only use strings
        if (vs.strings().length > 0) {
            return vs.strings().length;
        } else {
            return 1;
        }
    }
}

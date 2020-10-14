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

import hu.icellmobilsoft.roaster.tm4j.common.Tm4jReporterProducer;
import hu.icellmobilsoft.roaster.tm4j.common.api.TestCaseId;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestCaseData;
import hu.icellmobilsoft.roaster.tm4j.common.api.reporter.TestResultReporter;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestWatcher;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.CDI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * JUnit 5 extension to publish the test result to a TM4J server.
 * The test case id should be mapped with the test method via the {@link TestCaseId} annotation.
 * <br><br>
 * Example:
 * <pre>
 *{@literal @}ExtendWith(Tm4jExtension.class)
 * class ExampleTest {
 *    {@literal @}Test
 *    {@literal @}TestCaseId("ABC-T1")
 *     void testOne() {
 *         assertTrue(1 == 1);
 *     }
 * }
 * </pre>
 *
 * For configuration see: {@link Tm4jReporterProducer#createReporter}
 *
 * @see TestCaseId
 * @author martin.nagy
 * @since 0.2.0
 */
@Vetoed
public class Tm4jExtension implements TestWatcher, BeforeTestExecutionCallback {
    /**
     * Constant used as JUnit storage key for test run start time
     */
    protected static final String START_TIME = "START_TIME";

    private final Supplier<TestResultReporter> reporterSupplier;

    /**
     * Creates an instance with a {@code TestResultReporter} using CDI to get the {@code TestResultReporter} dependency.
     */
    public Tm4jExtension() {
        this(() -> CDI.current().select(TestResultReporter.class).get());
    }

    /**
     * Creates an instance with a {@code TestResultReporter} supplier passed as a parameter.
     *
     * @param reporterSupplier {@code TestResultReporter} supplier defining callbacks for test lifecycle events
     */
    public Tm4jExtension(Supplier<TestResultReporter> reporterSupplier) {
        this.reporterSupplier = Objects.requireNonNull(reporterSupplier);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        getStore(context).put(START_TIME, LocalDateTime.now());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        getReporter().reportSuccess(createTm4jRecord(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        getReporter().reportFail(createTm4jRecord(context), cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        getReporter().reportDisabled(createTm4jRecord(context), reason);
    }

    private TestResultReporter getReporter() {
        return reporterSupplier.get();
    }

    private TestCaseData createTm4jRecord(ExtensionContext context) {
        TestCaseData record = new TestCaseData();
        record.setId(context.getUniqueId());
        record.setDisplayName(context.getDisplayName());
        record.setTestMethod(context.getRequiredTestMethod());
        record.setStartTime(getStartTime(context));
        record.setEndTime(LocalDateTime.now());
        return record;
    }

    private LocalDateTime getStartTime(ExtensionContext context) {
        return getStore(context).remove(START_TIME, LocalDateTime.class);
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}

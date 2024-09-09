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
package hu.icellmobilsoft.roaster.testdoc.processor;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementKindVisitor9;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import hu.icellmobilsoft.roaster.testdoc.TestDoc;
import hu.icellmobilsoft.roaster.testdoc.data.TestCaseDocData;
import hu.icellmobilsoft.roaster.testdoc.data.TestClassDocData;

/**
 * Collects the {@link TestCaseDocData} from the {@link TestDoc} annotated classes and fields.
 *
 * @author janos.hamrak
 * @since 2.3.0
 */
public class TestDocVisitor extends ElementKindVisitor9<Void, Map<String, TestClassDocData>> {

    private static final int DEFAULT_TITLE_HEADING_LEVEL = 2;

    /**
     * Default constructor, constructs a new object.
     */
    public TestDocVisitor() {
        super();
    }

    @Override
    public Void visitTypeAsClass(TypeElement e, Map<String, TestClassDocData> testDocDataByClassName) {
        processClass(e, testDocDataByClassName);

        for (Element enclosedElement : e.getEnclosedElements()) {
            visit(enclosedElement, testDocDataByClassName);
        }

        return super.visitTypeAsClass(e, testDocDataByClassName);
    }

    @Override
    public Void visitExecutableAsMethod(ExecutableElement e, Map<String, TestClassDocData> testDocDataByClassName) {
        processMethod(e, testDocDataByClassName);

        return super.visitExecutableAsMethod(e, testDocDataByClassName);
    }

    /**
     * Collects test class doc data of given test class, annotated with {@link TestDoc}.
     *
     * @param clazz
     *            test class to process
     * @param testDocDataByClassName
     *            test doc data mapped by class name
     */
    private void processClass(TypeElement clazz, Map<String, TestClassDocData> testDocDataByClassName) {
        Optional<TestDoc> testDocAnnotation = Optional.ofNullable(clazz.getAnnotation(TestDoc.class));
        if (testDocAnnotation.isEmpty()) {
            return;
        }
        String className = clazz.getSimpleName().toString();
        Optional<DisplayName> displayNameAnnotation = Optional.ofNullable(clazz.getAnnotation(DisplayName.class));
        String displayName = displayNameAnnotation.isPresent() ? displayNameAnnotation.get().value() : "";
        int titleHeadingLevel = testDocAnnotation.get().titleHeadingLevel();
        testDocDataByClassName.put(className, new TestClassDocData(className, displayName, titleHeadingLevel, new ArrayList<>()));
    }

    /**
     * Collects test case doc data of given test method, annotated with {@link Test} or {@link ParameterizedTest}.
     *
     * @param element
     *            test method to process
     * @param testDocDataByClassName
     *            test doc data mapped by class name
     */
    private void processMethod(ExecutableElement element, Map<String, TestClassDocData> testDocDataByClassName) {
        Optional<Test> testAnnotation = Optional.ofNullable(element.getAnnotation(Test.class));
        Optional<ParameterizedTest> parameterizedTestAnnotation = Optional.ofNullable(element.getAnnotation(ParameterizedTest.class));
        if (testAnnotation.isEmpty() && parameterizedTestAnnotation.isEmpty()) {
            return;
        }

        String enclosingClassName = element.getEnclosingElement().getSimpleName().toString();
        String methodName = element.getSimpleName().toString();
        Optional<DisplayName> displayNameAnnotation = Optional.ofNullable(element.getAnnotation(DisplayName.class));
        String displayName = displayNameAnnotation.isPresent() ? displayNameAnnotation.get().value() : "";

        testDocDataByClassName.get(enclosingClassName).getTestCaseDocDataList().add(new TestCaseDocData(methodName, displayName));
    }

}

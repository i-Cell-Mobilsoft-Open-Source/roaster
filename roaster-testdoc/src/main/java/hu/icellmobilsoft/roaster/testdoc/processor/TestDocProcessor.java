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

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;

import hu.icellmobilsoft.roaster.testdoc.TestDoc;
import hu.icellmobilsoft.roaster.testdoc.config.TestDocConfig;
import hu.icellmobilsoft.roaster.testdoc.data.TestClassDocData;
import hu.icellmobilsoft.roaster.testdoc.writer.AsciiDocWriter;

/**
 * Annotation processor for {@link TestDoc}
 *
 * @author janos.hamrak
 * @since 2.3.0
 */
@AutoService(Processor.class)
public class TestDocProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(TestDoc.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TestDocConfig config;
        try {
            config = new TestDocConfig(processingEnv.getOptions());
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }

        Map<String, TestClassDocData> testDocData = collectTestDocData(annotations, roundEnv);
        if (!testDocData.isEmpty()) {
            writeToFile(testDocData, new AsciiDocWriter(config), config);
        }

        return false;
    }

    private Map<String, TestClassDocData> collectTestDocData(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var visitor = new TestDocVisitor();

        Map<String, TestClassDocData> testDocDataByClassName = new HashMap<>();
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                visitor.visit(element, testDocDataByClassName);
            }
        }
        return testDocDataByClassName;
    }

    private void writeToFile(Map<String, TestClassDocData> testDocData, AsciiDocWriter docWriter, TestDocConfig config) {
        try (Writer writer = createWriter(config)) {
            docWriter.write(testDocData, writer);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private Writer createWriter(TestDocConfig config) throws IOException {
        return config.isOutputToClassPath()
                ? processingEnv.getFiler()
                        .createResource(StandardLocation.CLASS_OUTPUT, "", config.getOutputDir() + config.getOutputFileName())
                        .openWriter()
                : Files.newBufferedWriter(Paths.get(config.getOutputDir(), config.getOutputFileName()));
    }
}

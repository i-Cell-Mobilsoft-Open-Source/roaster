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
package hu.icellmobilsoft.roaster.module.testdoc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import hu.icellmobilsoft.roaster.testdoc.TestDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.roaster.testdoc.config.TestDocConfig;

/**
 * {@link TestDoc} file generation test
 * 
 * @author janos.hamrak
 * @since 2.3.0
 */
@TestDoc(titleHeadingLevel = 3)
@DisplayName("Testing TestDoc functions")
class TestDocTest {

    @Test
    @DisplayName("Generated file should contain the test data")
    void generatedFileShouldContainTestData() throws URISyntaxException, IOException {
        URL generatedFileUrl = getClass().getResource("/" + TestDocConfig.DEFAULT_OUTPUT_PATH + TestDocConfig.DEFAULT_OUTPUT_FILE_NAME);
        assertNotNull(generatedFileUrl);

        String generatedFile = Files.readString(Paths.get(generatedFileUrl.toURI()));

        // classes
        assertTrue(generatedFile.contains("Testing TestDoc functions"));
        assertTrue(generatedFile.contains("Example test class"));

        // methods
        assertTrue(generatedFile.contains("generatedFileShouldContainTestData"));
        assertTrue(generatedFile.contains("exampleTestMethod"));

        // display names
        assertTrue(generatedFile.contains("Generated file should contain the test data"));
        assertTrue(generatedFile.contains("Example test method in TestDocExampleTest class"));
    }
}

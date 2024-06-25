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
package hu.icellmobilsoft.roaster.testdoc.config;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;

/**
 * Class representing the configuration for the module
 *
 * @author janos.hamrak
 * @since 2.3.0
 */
public class TestDocConfig {

    /**
     * Default output path
     */
    public static final String DEFAULT_OUTPUT_PATH = "META-INF/";
    /**
     * Default output file name
     */
    public static final String DEFAULT_OUTPUT_FILE_NAME = "test_doc.adoc";
    private static final String CONFIG_PREFIX = "roaster.testDoc.";
    private static final String OUTPUT_DIR_KEY = CONFIG_PREFIX + "outputDir";
    private static final String OUTPUT_FILE_NAME_KEY = CONFIG_PREFIX + "outputFileName";
    private static final String OUTPUT_TO_CLASS_PATH_KEY = CONFIG_PREFIX + "outputToClassPath";
    private static final String COLUMNS_KEY = CONFIG_PREFIX + "columns";
    private final String outputDir;
    private final String outputFileName;
    private final boolean outputToClassPath;
    private final TestDocColumn[] columns;

    /**
     * Creates the config object based on properties
     *
     * @param properties
     *            the map which contains the config properties
     */
    public TestDocConfig(Map<String, String> properties) {
        outputDir = properties.getOrDefault(OUTPUT_DIR_KEY, DEFAULT_OUTPUT_PATH);
        outputFileName = properties.getOrDefault(OUTPUT_FILE_NAME_KEY, DEFAULT_OUTPUT_FILE_NAME);
        outputToClassPath = Boolean.parseBoolean(properties.getOrDefault(OUTPUT_TO_CLASS_PATH_KEY, Boolean.TRUE.toString()));
        columns = processColumnConfig(properties);
    }

    private TestDocColumn[] processColumnConfig(Map<String, String> properties) {
        String columnsString = properties.get(COLUMNS_KEY);
        if (columnsString == null) {
            return TestDocColumn.values();
        }

        String[] split = columnsString.split("\\s*,\\s*", -1);
        TestDocColumn[] columns = new TestDocColumn[split.length];
        for (int i = 0; i < split.length; i++) {
            String name = split[i].trim().toUpperCase();
            columns[i] = EnumUtils.getEnum(TestDocColumn.class, name);
            if (columns[i] == null) {
                throw new IllegalStateException(
                        MessageFormat
                                .format("Unknown testDoc column: [{0}]. Possible values: [{1}]", split[i], Arrays.toString(TestDocColumn.values())));
            }
        }
        return columns;
    }

    /**
     * Returns the directory for the generated file
     *
     * @return the directory for the generated file
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Returns the generated file name
     *
     * @return the generated file name
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * Returns {@literal true} if the output folder should be on the classpath
     *
     * @return {@literal true} if the output folder should be on the classpath
     */
    public boolean isOutputToClassPath() {
        return outputToClassPath;
    }

    /**
     * Returns the columns of the generated table
     * 
     * @return the columns of the generated table
     */
    public TestDocColumn[] getColumns() {
        return columns;
    }
}

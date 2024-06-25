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
package hu.icellmobilsoft.roaster.testdoc.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.roaster.testdoc.config.TestDocColumn;
import hu.icellmobilsoft.roaster.testdoc.config.TestDocConfig;
import hu.icellmobilsoft.roaster.testdoc.data.TestCaseDocData;
import hu.icellmobilsoft.roaster.testdoc.data.TestClassDocData;

/**
 * Writes the collected annotation data in asciidoc format
 *
 * @author janos.hamrak
 * @since 2.3.0
 */
public class AsciiDocWriter {
    private final TestDocConfig config;
    private static final String ASCIIDOC_TABLE_START_END = "|===\n";

    /**
     * Constructor with the config object
     * 
     * @param config
     *            the config object
     */
    public AsciiDocWriter(TestDocConfig config) {
        this.config = config;
    }

    /**
     * Writes test doc data to asciidoc.
     *
     * @param testDocData
     *            test doc data mapped by class name
     * @param writer
     *            asciidoc writer
     * @throws IOException
     *             if write error
     */
    public void write(Map<String, TestClassDocData> testDocData, Writer writer) throws IOException {
        for (Map.Entry<String, TestClassDocData> testClassData : testDocData.entrySet()) {
            writeHeader(writer, testClassData.getValue());
            for (TestCaseDocData testCaseDocData : testClassData.getValue().getTestCaseDocDataList()) {
                writeLine(testCaseDocData, writer);
            }
            writer.write(ASCIIDOC_TABLE_START_END);
        }
    }

    private void writeHeader(Writer writer, TestClassDocData testClassDocData) throws IOException {
        String title = StringUtils.defaultIfBlank(testClassDocData.getDisplayName(), testClassDocData.getClassName());
        writeTitleHeadingLevel(writer, testClassDocData.getTitleHeadingLevel());
        writer.write(title);
        writeColumns(writer);
    }

    private void writeTitleHeadingLevel(Writer writer, int titleHeadingLevel) throws IOException {
        for (int i = 0; i < titleHeadingLevel; i++) {
            writer.write("=");
            if (i == titleHeadingLevel - 1) {
                writer.write(" ");
            }
        }
    }

    private void writeColumns(Writer writer) throws IOException {
        writer.write(" \n[cols=\"");
        TestDocColumn[] columns = config.getColumns();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                writer.write(',');
            }
            writer.write(String.valueOf(getColumnWidth(columns[i])));
        }
        writer.write("\",options=header,stripes=even]\n");
        writer.write(ASCIIDOC_TABLE_START_END);

        for (TestDocColumn column : config.getColumns()) {
            writer.write('|');
            writer.write(getColumnDisplayName(column));
        }
        writer.write("\n");

    }

    private void writeLine(TestCaseDocData testCaseDocData, Writer writer) throws IOException {
        for (TestDocColumn column : config.getColumns()) {
            writer.write('|');
            writer.write(getColumnValue(testCaseDocData, column));
        }
        writer.write('\n');
    }

    private int getColumnWidth(TestDocColumn column) {
        switch (column) {
        case METHOD_NAME:
            return 1;
        case DISPLAY_NAME:
            return 3;
        default:
            throw newInvalidColumnException(column);
        }
    }

    private String getColumnDisplayName(TestDocColumn column) {
        switch (column) {
        case METHOD_NAME:
            return "Method name";
        case DISPLAY_NAME:
            return "Display name";
        default:
            throw newInvalidColumnException(column);
        }
    }

    private String getColumnValue(TestCaseDocData testCaseDocData, TestDocColumn column) {
        switch (column) {
        case METHOD_NAME:
            return Objects.toString(testCaseDocData.getMethodName(), "");
        case DISPLAY_NAME:
            return Objects.toString(testCaseDocData.getDisplayName(), "");
        default:
            throw newInvalidColumnException(column);
        }
    }

    private IllegalStateException newInvalidColumnException(TestDocColumn column) {
        return new IllegalStateException("Invalid column: " + column);
    }

}

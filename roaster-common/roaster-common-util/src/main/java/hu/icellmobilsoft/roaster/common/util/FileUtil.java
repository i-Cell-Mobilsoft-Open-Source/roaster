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
package hu.icellmobilsoft.roaster.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.roaster.api.TestException;

/**
 * File utils for test environment
 * 
 * @author imre.scheffer
 */
public class FileUtil {

    private static final Logger LOG = Logger.getLogger(FileUtil.class.getName());

    public static final String SRC_TEST_RESOURCES = "src/test/resources/";

    /**
     * Read file by java.nio (java 11+)
     * 
     * @param first
     *            first the path string or initial part of the path string
     * @param more
     *            more additional strings to be joined to form the path string
     * @return file content
     * 
     * @see Path#of(String, String...)
     */
    public static String readFile(String first, String... more) {
        var path = Path.of(first, more);
        return readFile(path);
    }

    /**
     * Read file by java.nio (java 11+) from ClassLoader.getSystemResourceAsStream
     * 
     * @param fileName
     *            filename like token.xml, in src/main/resources source directory
     * @return file content
     * @see ClassLoader#getSystemResourceAsStream(String)
     */
    public static String readFileFromResource(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        try (var inputStream = ClassLoader.getSystemResourceAsStream(fileName)) {
            String file = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            LOG.info(() -> MessageFormat.format("File [{0}] from resources readed!", ClassLoader.getSystemResource(fileName)));
            return file;
        } catch (IOException e) {
            throw new TestException(MessageFormat.format("Unable to read File [{0}] from resource", fileName), e);
        }
    }

    /**
     * Read file by java.nio (java 11+) from ClassLoader.getSystemResourceAsStream <br>
     * Sample:
     * 
     * <pre>
     * public void read(InputStream inputStream) throws BaseException, IOException {
     *     content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
     * }
     *
     * public void sample() {
     *     FileUtil.readFile(Path.of("src/test/resources/", "test.txt"), reader::read);
     *     String content = reader.getContent();
     * }
     * </pre>
     * 
     * @param fileName
     *            filename like token.xml, in src/main/resources source directory
     * @param function
     *            readed InputStream consumer. All exception is handled to {@code TestException}
     * @see ClassLoader#getSystemResourceAsStream(String)
     */
    public static void readFileFromResource(String fileName, ExceptionConsumer<InputStream, Exception> function) {
        if (StringUtils.isBlank(fileName)) {
            return;
        }

        try (var inputStream = ClassLoader.getSystemResourceAsStream(fileName)) {
            function.accept(inputStream);
            LOG.info(() -> MessageFormat.format("File [{0}] from resources readed!", ClassLoader.getSystemResource(fileName)));
        } catch (IOException e) {
            throw new TestException(MessageFormat.format("Unable to read File [{0}] from resource", fileName), e);
        } catch (BaseException e) {
            throw new TestException(MessageFormat.format("BaseException during reading File [{0}] from resource", fileName), e);
        } catch (Exception e) {
            throw new TestException(MessageFormat.format("Exception during reading File [{0}] from resource", fileName), e);
        }
    }

    /**
     * Read file by java.nio (java 11+) <br>
     * Sample:
     * 
     * <pre>
     * public void read(InputStream inputStream) throws BaseException, IOException {
     *     content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
     * }
     *
     * public void sample() {
     *     FileUtil.readFile(Path.of("src/test/resources/", "test.txt"), reader::read);
     *     String content = reader.getContent();
     * }
     * </pre>
     * 
     * @param path
     *            the path to the file
     * @param function
     *            readed InputStream consumer. All exception is handled to {@code TestException}
     * @see Files#readString(Path)
     * 
     */
    public static void readFile(Path path, ExceptionConsumer<InputStream, Exception> function) {
        if (path == null) {
            throw new TestException("path is null!");
        }
        try {
            function.accept(Files.newInputStream(path));
            LOG.info(() -> MessageFormat.format("File from path [{0}] readed!", path.toAbsolutePath()));
        } catch (IOException e) {
            throw new TestException(MessageFormat.format("Unable to read File from path: [{0}]", path.toAbsolutePath()), e);
        } catch (BaseException e) {
            throw new TestException(MessageFormat.format("BaseException during reading File from path: [{0}]", path.toAbsolutePath()), e);
        } catch (Exception e) {
            throw new TestException(MessageFormat.format("Exception during reading File from path: [{0}]", path.toAbsolutePath()), e);
        }
    }

    /**
     * Read file by java.nio (java 11+)
     * 
     * @param path
     *            the path to the file
     * @return file content
     * 
     * @see Files#readString(Path)
     */
    public static String readFile(Path path) {
        if (path == null) {
            throw new TestException("path is null!");
        }
        try {
            var file = Files.readString(path);
            LOG.info(() -> MessageFormat.format("File from path [{0}] readed!", path.toAbsolutePath()));
            return file;
        } catch (IOException e) {
            throw new TestException(MessageFormat.format("Unable to read File from path: [{0}]", path.toAbsolutePath()), e);
        }
    }

}

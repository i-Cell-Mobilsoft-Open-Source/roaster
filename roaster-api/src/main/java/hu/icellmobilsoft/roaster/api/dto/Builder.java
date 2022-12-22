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
package hu.icellmobilsoft.roaster.api.dto;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.roaster.api.Condition;

/**
 * Class to hold all the builder methods.
 *
 */
public class Builder {

    /**
     * The root package of the builder classes.
     */
    private static final String BUILDER_ROOT_PACKAGE = "hu.icellmobilsoft.roaster.common.test";

    private static final Object builderListLock = new Object();

    /**
     * Static list of the registered builders.
     */
    private static List<IBaseBuilder<?>> builderList = null;

    private Builder() {
    }

    /**
     * return the builder for the given class ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
     *
     * @param <T>
     *            generic class
     * @param targetClazz
     *            class to instantiate.
     * @return the builder for the target class
     */
    public static <T> IBaseBuilder<T> get(Class<T> targetClazz) {
        Condition.notNull(targetClazz, "Target class should not be null.");
        Condition.expected(!Collection.class.isAssignableFrom(targetClazz) || Map.class.isAssignableFrom(targetClazz),
                "Target class should not be an collection");

        Optional<IBaseBuilder<?>> builder = getBuilderList().stream().parallel().filter(b -> b.getTargetClass() == targetClazz).findAny();
        IBaseBuilder<T> result = null;
        if (builder.isPresent()) {
            result = (IBaseBuilder<T>) builder.get();
            result.clear();
        } else if (targetClazz.getAnnotation(XmlType.class) != null) {
            result = GenericTypeBuilder.create(targetClazz, createEntity(targetClazz));
        }
        Condition.ensure(result != null, MessageFormat.format("There is no builder for class [{0}].", targetClazz.getSimpleName()));
        return result;
    }

    /**
     * create an entity through the {@code new instance} and convert exceptions to {@code TestException}
     *
     * @param targetClass
     *            the class to create an instance
     * @param <C>
     *            the type of the instance
     * @return the new object
     */
    private static <C> Supplier<C> createEntity(final Class<C> targetClass) {
        return () -> {
            C instance = null;
            try {
                instance = targetClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Condition.shouldNeverThrown("Failed to create the target class", e);
            }
            return instance;
        };
    }

    /**
     * @param <T>
     *            generic class
     * @param targetClazz
     *            class to instantiate.
     * @return true if the target class has registered builder.
     */
    public static <T> boolean has(Class<T> targetClazz) {
        Condition.notNull(targetClazz, "Target class should not be null.");
        Optional<IBaseBuilder<?>> builder = getBuilderList().stream().parallel().filter(b -> b.getTargetClass() == targetClazz).findAny();
        return builder.isPresent();
    }

    /**
     * This method will initialize the builder list it is not present. The builder will be loaded from package {@value Builder#BUILDER_ROOT_PACKAGE}
     *
     * @return the list of the builders
     */
    private static List<IBaseBuilder<?>> getBuilderList() {
        List<IBaseBuilder<?>> tempList = builderList;
        if (tempList == null || tempList.isEmpty()) {
            synchronized (builderListLock) {
                tempList = builderList;
                if (tempList == null) {
                    List<Class<?>> classes = getClasses(BUILDER_ROOT_PACKAGE);
                    List<IBaseBuilder<?>> list = new ArrayList<>();
                    for (Class<?> c : classes) {
                        if (IBaseBuilder.class.isAssignableFrom(c) && !GenericTypeBuilder.class.isAssignableFrom(c) && !c.isInterface()
                                && !Modifier.isAbstract(c.getModifiers())) {
                            IBaseBuilder<?> iBaseBuilder = instantiateBuilder(c);
                            list.add(iBaseBuilder);
                        }
                    }
                    tempList = list;
                    builderList = tempList;
                }
            }
        }
        return builderList;
    }

    /**
     * Create an builder from the builder class.
     *
     * @param builderClass
     *            the class of the builder class
     * @return builder object after the instantiation
     */
    private static IBaseBuilder<?> instantiateBuilder(Class<?> builderClass) {
        Condition.notNull(builderClass, "Builder class should not be empty before instantiation.");
        Condition.expected(!builderClass.isInterface(), "[{0}] should not be an interface.", builderClass.getSimpleName());
        Condition.expected(BaseTypeBuilder.class.isAssignableFrom(builderClass), "[{0}] should be implement IBaseBuilder class.",
                builderClass.getSimpleName());
        Condition.expected(!Modifier.isAbstract(builderClass.getModifiers()), "[{0}] should not be abstract class.", builderClass.getSimpleName());
        IBaseBuilder<?> builder = null;
        try {
            builder = (BaseTypeBuilder<?>) builderClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Condition.shouldNeverThrown(MessageFormat.format("[{0}] could not be instantiate: ", builderClass.getSimpleName()), e);
        }
        return builder;
    }

    /**
     * @param file
     *            the file
     * @return true if the file has the suffix .class
     */
    private static boolean isClass(final Path file) {
        Condition.notNull(file, "File should not be null.");
        return hasSuffix(file, "class");
    }

    /**
     * @param file
     *            the file
     * @param suffix
     *            the suffix to check without {@code '.'}
     * @return true if the file has the suffix
     */
    private static boolean hasSuffix(final Path file, final String suffix) {
        Condition.notNull(file, "File should not be null.");
        Condition.notBlank(suffix, "Suffix should not be blank");
        String fileName = file.toString();
        return StringUtils.equals(suffix, fileName.substring(fileName.lastIndexOf('.') + 1));
    }

    /**
     * @param path
     *            the directory object
     * @return the package name of the directory
     */
    private static String pathToSubPackage(final Path path) {
        Condition.notNull(path, "Path should not be null.");
        Condition.expected(path.toFile().isDirectory(), "Path should be a directory.");
        String directoryName = path.getFileName().toString();
        if (directoryName.endsWith("/")) {
            directoryName = directoryName.substring(0, directoryName.length() - 1);
        }
        return directoryName;
    }

    /**
     * Recursively read through a directory tree and collect all the classes.
     *
     * @param directory
     *            the root directory
     * @param packageName
     *            the package name of the directory
     * @return the Class object list
     */
    private static List<Class<?>> findClasses(final Path directory, final String packageName) {
        Condition.notNull(directory, "Directory object should not be null.");
        Condition.expected(directory.toFile().exists(), "Directory should be exists.");
        List<Class<?>> result = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(directory, 1)) {
            walk.forEach(path -> {
                if (path.toFile().isDirectory() && !path.equals(directory)) {
                    result.addAll(findClasses(path, packageName + '.' + pathToSubPackage(path)));
                } else if (isClass(path)) {
                    String fileName = path.getFileName().toString();
                    try {
                        Class<?> clazz = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                        if (clazz != null) {
                            result.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        Condition.shouldNeverThrown(MessageFormat.format("Could not read class [{0}].", path), e);
                    }
                }
            });
        } catch (IOException e) {
            Condition.shouldNeverThrown(MessageFormat.format("Failed to walk through on directory [{0}].", directory), e);
        }

        return result;
    }

    /**
     * This method builds an FileSystem from the jar and walk it through recursively.
     * <p>
     * It will walk only through the path and not the whole jar!
     *
     * @param root
     *            the jar file location in the filesystem
     * @param path
     *            the subdirectory that represent the package
     * @param packageName
     *            package to load recursively
     * @return Class object list
     */
    private static List<Class<?>> getClassesFromJar(final String root, final String path, final String packageName) {
        Condition.notBlank(root, "Root should not be blank.");
        Condition.notBlank(path, "Path should not be blank.");
        Condition.notBlank(packageName, "Package name should not be blank.");
        List<Class<?>> result = new ArrayList<>();
        final HashMap<String, String> env = new HashMap<>();
        try (final FileSystem fs = FileSystems.newFileSystem(URI.create(root), env)) {
            result.addAll(findClasses(fs.getPath(path), packageName));
        } catch (IOException e) {
            Condition.shouldNeverThrown(MessageFormat.format("Failed to create filesystem for jar [{0}].", root), e);
        }
        return result;
    }

    /**
     * Get all the classes from the given package.
     * <p>
     * The list will contains all the classes from the package and subpackages. The search is going through all the resources that has the given
     * package, not only in the current unit.
     *
     * @param packageName
     *            the name of the package
     * @return Class object list
     */
    public static List<Class<?>> getClasses(final String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        List<Class<?>> result = new ArrayList<>();
        try {
            for (URL u : Collections.list(classLoader.getResources(path))) {
                final String[] paths = u.toString().split("!");
                if (paths.length == 1) {
                    result.addAll(findClasses(Paths.get(URI.create(paths[0])), packageName));
                } else {
                    result.addAll(getClassesFromJar(paths[0], paths[1], packageName));
                }
            }
        } catch (IOException e) {
            Condition.shouldNeverThrown(MessageFormat.format("Failed to get the classes from package [{0}].", packageName), e);
        }
        return result;
    }

}

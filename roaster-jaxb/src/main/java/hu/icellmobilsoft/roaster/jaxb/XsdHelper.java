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
package hu.icellmobilsoft.roaster.jaxb;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;

/**
 * JVM level cache for XSD Schema and JAXBContext objects
 *
 * @author ferenc.lutischan
 * @author martin.nagy
 * @since 2.7.0
 */
public class XsdHelper {
    private static final Map<String, Schema> XSD_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, JAXBContext> JAXB_CONTEXT_CACHE = new ConcurrentHashMap<>();

    /**
     * Private constructor for util class.
     */
    private XsdHelper() {
        super();
    }

    /**
     * Creates and caches JAXBContext for the class
     * 
     * @param forClass
     *            Class for which JAXBContext is created. Must not be null.
     * @return Cached JAXBContext object for the given class or new one if not found in the cache.
     * @throws InvalidParameterException
     *             if invalid parameter is given
     */
    public static JAXBContext getJAXBContext(Class<?> forClass) throws InvalidParameterException {
        if (forClass == null) {
            throw new InvalidParameterException("forClass is null!");
        }
        String className = forClass.getName();
        return JAXB_CONTEXT_CACHE.computeIfAbsent(className, k -> {
            try {
                return JAXBContext.newInstance(forClass);
            } catch (JAXBException e) {
                throw new IllegalStateException("Error in JAXBContext creation for class: " + className, e);
            }
        });
    }

    /**
     * Creates and caches JAXBContext for the classes
     * 
     * @param forClasses
     *            Classes for which JAXBContext is created. Must not be null.
     * @return Cached JAXBContext object for the given classes or new one if not found in the cache.
     * @throws InvalidParameterException
     *             if invalid parameter is given
     */
    public static JAXBContext getJAXBContext(Class<?>... forClasses) throws InvalidParameterException {
        if (forClasses == null) {
            throw new InvalidParameterException("forClasses is null!");
        }
        String cacheKey = Arrays.stream(forClasses).filter(Objects::nonNull).map(Class::getName).sorted().collect(Collectors.joining());
        return JAXB_CONTEXT_CACHE.computeIfAbsent(cacheKey, k -> {
            try {
                return JAXBContext.newInstance(forClasses);
            } catch (JAXBException e) {
                throw new IllegalStateException("Error in JAXBContext creation for classes: " + cacheKey, e);
            }
        });
    }

    /**
     * Creates and caches a {@link Schema} for the given XSD
     * 
     * @param xsd
     *            XSD for which Schema is created. Must not be blank.
     * @param lsResourceResolver
     *            Resource resolver for the given XSD. Must not be null.
     * @return Cached {@link Schema} object for the given XSD or new one if not found in the cache.
     * @throws InvalidParameterException
     *             if invalid parameter is given
     */
    public static Schema getSchema(String xsd, LSResourceResolver lsResourceResolver) throws InvalidParameterException {
        if (StringUtils.isBlank(xsd) || lsResourceResolver == null) {
            throw new InvalidParameterException("xsd is blank or lsResourceResolver is null!");
        }
        return XSD_CACHE.computeIfAbsent(xsd, k -> {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsd);
            if (stream == null) {
                throw new IllegalArgumentException("Cannot find schema to validate xsd: " + xsd);
            }
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            sf.setResourceResolver(lsResourceResolver);
            try {
                return sf.newSchema(new StreamSource(stream));
            } catch (SAXException e) {
                throw new IllegalStateException("Error in xsd schema creation for xsd: " + xsd, e);
            }
        });
    }

    /**
     * Clearing the underlying XSD schema cache {@link XsdHelper#XSD_CACHE}
     */
    public static void clearXsdCache() {
        XSD_CACHE.clear();
    }

    /**
     * Clearing the underlying jaxb context cache {@link XsdHelper#JAXB_CONTEXT_CACHE}
     */
    public static void clearJaxbContextCache() {
        JAXB_CONTEXT_CACHE.clear();
    }

}

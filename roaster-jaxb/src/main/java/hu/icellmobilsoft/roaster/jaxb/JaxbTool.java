/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.validation.xml.error.IXsdValidationErrorCollector;
import hu.icellmobilsoft.coffee.rest.validation.xml.error.XsdValidationErrorCollector;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.rest.validation.xml.utils.IXsdResourceResolver;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.roaster.jaxb.catalog.PublicCatalogResolver;

/**
 * JAXB (un)marshaller and JAXB-related operations
 *
 * @author attila.nyers
 * @author ferenc.lutischan
 * @author imre.scheffer
 * @author m.petrenyi
 * @author balazs.joo
 * @author martin.nagy
 * @since 2.7.0
 */
public class JaxbTool {
    private static final String ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY = "type or binary is null or empty!";

    private final Function<String, IXsdResourceResolver> catalogResolverSupplier;
    private final Supplier<IXsdValidationErrorCollector> xsdValidationErrorCollectorSupplier;

    /**
     * Default constructor with {@link PublicCatalogResolver} as catalog resolver.
     */
    public JaxbTool() {
        this(schemaPath -> {
            PublicCatalogResolver publicCatalogResolver = new PublicCatalogResolver();
            publicCatalogResolver.setXsdDirPath(schemaPath);
            return publicCatalogResolver;
        }, XsdValidationErrorCollector::new);
    }

    /**
     * Constructor with custom {@link Function} that returns {@link IXsdResourceResolver} for given schema path.
     * 
     * @param catalogResolverSupplier
     *            {@link Function} that returns {@link IXsdResourceResolver} for given schema path
     * @param xsdValidationErrorCollectorSupplier
     *            {@link Supplier} of {@link IXsdValidationErrorCollector} instance.
     */
    public JaxbTool(Function<String, IXsdResourceResolver> catalogResolverSupplier,
            Supplier<IXsdValidationErrorCollector> xsdValidationErrorCollectorSupplier) {
        this.catalogResolverSupplier = catalogResolverSupplier;
        this.xsdValidationErrorCollectorSupplier = xsdValidationErrorCollectorSupplier;
    }

    /**
     * Converts XML into an object without schema validation.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param inputStream
     *            Stream containing the input data
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, InputStream inputStream) throws BaseException {
        return unmarshalXML(type, inputStream, (String) null);
    }

    /**
     * Converts XML into an object without schema validation.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param binary
     *            Binary data containing the input information
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, byte[] binary) throws BaseException {
        if (Objects.isNull(type) || ArrayUtils.isEmpty(binary)) {
            throw new InvalidParameterException(ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY);
        }
        return unmarshalXML(type, new ByteArrayInputStream(binary));
    }

    /**
     * Converting XML into an object with schema validation.<br>
     * <br>
     * If necessary, it is possible to customize unmarshalling requirements using the "feature" or "properties" settings<br>
     * An example of security settings:<br>
     * https://owasp.org/www-project-cheat-sheets/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#JAXB_Unmarshaller <br>
     * <br>
     * We usually do this most commonly through the Xerces configurations:
     *
     * <pre>
     * -Dorg.xml.sax.parser=com.sun.org.apache.xerces.internal.parsers.SAXParser
     * -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
     * -Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl
     * -DentityExpansionLimit=100
     * </pre>
     *
     * Additional settings can be configured as follows:<br>
     * https://docs.oracle.com/javase/7/docs/api/javax/xml/bind/Unmarshaller.html
     *
     * <pre>
     * SAXParserFactory spf = SAXParserFactory.newInstance();
     * spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
     * spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
     * spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
     * SAXParser saxParser = spf.newSAXParser();
     * saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", "http://....");
     * Source xmlSource = new SAXSource(saxParser.getXMLReader(), new InputSource(inputStream));
     * T result = (T) unmarshaller.unmarshal(inputStream);
     * </pre>
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param inputStream
     *            A stream containing the input data
     * @param schemaPath
     *            Schema path
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, InputStream inputStream, String schemaPath) throws BaseException {
        if (type == null || inputStream == null) {
            throw new InvalidParameterException("type or inputStream is null!");
        }
        IXsdValidationErrorCollector errorCollector = xsdValidationErrorCollectorSupplier.get();
        try {
            JAXBContext jaxbContext = XsdHelper.getJAXBContext(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setEventHandler(errorCollector);
            // if schemaPath is empty -> no validation, only conversion
            if (StringUtils.isNotBlank(schemaPath)) {
                unmarshaller.setSchema(XsdHelper.getSchema(schemaPath, catalogResolverSupplier.apply(schemaPath)));
            }
            @SuppressWarnings("unchecked")
            T result = (T) unmarshaller.unmarshal(inputStream);
            if (!errorCollector.getErrors().isEmpty()) {
                throw new XsdProcessingException(errorCollector.getErrors(), null);
            }

            return result;
        } catch (UnmarshalException e) {
            // The default parser terminates the process on the first FATAL_ERROR with an exception throw.
            // This behavior can be set using spf.setFeature ("http://apache.org/xml/features/continue-after-fatal-error", true)
            // You can achieve this behavior using the setFeature method, but it currently meets our needs. However, the errors detected so far must
            // be included in the exception.
            throw new XsdProcessingException(errorCollector.getErrors(), e);
        } catch (JAXBException e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, e.getLocalizedMessage(), e);
        }
    }

    /**
     * Converting XML into an object with schema validation.
     *
     * @param <T>
     *            Returning type
     * @param type
     *            Type of class representing an XML object
     * @param binary
     *            The binary data containing the input
     * @param schemaPath
     *            Schema path
     * @return An object corresponding to the XML with the read values.
     * @throws BaseException
     *             In case of invalid input, or if the input data cannot be processed
     */
    public <T> T unmarshalXML(Class<T> type, byte[] binary, String schemaPath) throws BaseException {
        if (Objects.isNull(type) || ArrayUtils.isEmpty(binary)) {
            throw new InvalidParameterException(ERR_MSG_TYPE_OR_BINARY_IS_NULL_OR_EMPTY);
        }
        return unmarshalXML(type, new ByteArrayInputStream(binary), schemaPath);
    }

    /**
     * Marshals given XML {@link Object} to {@link String}. <br>
     * Sets the following fix parameters for the conversion:
     * <ul>
     * <li>jaxb.formatted.output - TRUE ({@link Marshaller#JAXB_FORMATTED_OUTPUT})</li>
     * <li>jaxb.fragment - TRUE ({@link Marshaller#JAXB_FRAGMENT})</li>
     * </ul>
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog to validate on, if null, then validation is not executed
     * @return XML String
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath) throws BaseException {
        return marshalXML(obj, schemaPath, createMarshallerProperties());
    }

    /**
     * Marshals given XML {@link Object} to {@link String}. <br>
     * Sets the following fix parameters for the conversion:
     * <ul>
     * <li>jaxb.formatted.output - TRUE ({@link Marshaller#JAXB_FORMATTED_OUTPUT})</li>
     * <li>jaxb.fragment - TRUE ({@link Marshaller#JAXB_FRAGMENT})</li>
     * </ul>
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog to validate on, if null, then validation is not executed
     * @return XML String
     * @param additionalClasses
     *            these classes will be added to the {@link JAXBContext}. Typically in case of 'Class not known to this context' errors.
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath, Class<?>... additionalClasses) throws BaseException {
        return marshalXML(obj, schemaPath, createMarshallerProperties(), additionalClasses);
    }

    /**
     * Creates a properties map for the marshaller.
     * 
     * @return properties map
     */
    protected Map<String, Object> createMarshallerProperties() {
        return Map.ofEntries( //
                Map.entry(Marshaller.JAXB_FORMATTED_OUTPUT, true), //
                Map.entry(Marshaller.JAXB_FRAGMENT, Boolean.TRUE) //
        );
    }

    /**
     * Marshals given XML {@link Object} to {@link String}. <br>
     * Sets the following fix parameters for the conversion:
     * <ul>
     * <li>jaxb.formatted.output - TRUE ({@link Marshaller#JAXB_FORMATTED_OUTPUT})</li>
     * <li>jaxb.fragment - TRUE ({@link Marshaller#JAXB_FRAGMENT})</li>
     * </ul>
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog file to validate on, if null, then validation is not executed
     * @param marshallerProperties
     *            marshaller properties
     * @return XML String
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath, Map<String, Object> marshallerProperties) throws BaseException {
        return marshalXML(obj, schemaPath, marshallerProperties, (Class<?>[]) null);
    }

    /**
     *
     * Marshals given XML {@link Object} to {@link String} with given parameters.
     *
     * @param obj
     *            XML {@code Object}
     * @param schemaPath
     *            path to XSD or catalog to validate on, if null, then validation is not executed (possibly it should be an xml catalog file)
     * @param marshallerProperties
     *            marshaller properties
     * @param additionalClasses
     *            these classes will be added to the {@link JAXBContext}. Typically in case of 'Class not known to this context' errors.
     * @return XML String
     * @throws BaseException
     *             if invalid input or cannot be marshalled
     */
    public String marshalXML(Object obj, String schemaPath, Map<String, Object> marshallerProperties, Class<?>... additionalClasses)
            throws BaseException {
        if (obj == null) {
            throw new InvalidParameterException("obj is null!");
        }
        try {
            JAXBContext jaxbContext;
            if (additionalClasses != null && additionalClasses.length != 0) {
                List<Class<?>> contextClasses = new ArrayList<>(Arrays.asList(additionalClasses));
                contextClasses.add(obj.getClass());
                jaxbContext = XsdHelper.getJAXBContext(contextClasses.toArray(new Class<?>[0]));
            } else {
                jaxbContext = XsdHelper.getJAXBContext(obj.getClass());
            }
            Marshaller marshaller = jaxbContext.createMarshaller();
            if (marshallerProperties != null) {
                for (Map.Entry<String, Object> entry : marshallerProperties.entrySet()) {
                    marshaller.setProperty(entry.getKey(), entry.getValue());
                }
            }
            IXsdValidationErrorCollector errorCollector = xsdValidationErrorCollectorSupplier.get();
            marshaller.setEventHandler(errorCollector);
            // if schemaPath is empty -> no validation, only conversion
            if (StringUtils.isNotBlank(schemaPath)) {
                marshaller.setSchema(XsdHelper.getSchema(schemaPath, catalogResolverSupplier.apply(schemaPath)));
            }
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(obj, stringWriter);
            if (!errorCollector.getErrors().isEmpty()) {
                throw new XsdProcessingException(errorCollector.getErrors(), null);
            }
            return stringWriter.getBuffer().toString();
        } catch (JAXBException e) {
            throw new XsdProcessingException(CoffeeFaultType.INVALID_INPUT, e.getMessage(), e);
        }
    }

}

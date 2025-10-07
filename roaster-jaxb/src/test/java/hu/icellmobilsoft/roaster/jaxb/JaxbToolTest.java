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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResponse;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * {@link JaxbTool} utility test class
 *
 * @author martin.nagy
 * @since 2.7.0
 */
class JaxbToolTest {
    private static final String TEST_XML = "<BaseResponse xmlns=\"http://common.dto.coffee.icellmobilsoft.hu/commonservice\">\n" //
            + "    <message>hello</message>\n" //
            + "</BaseResponse>";
    private static final String SCHEMA_PATH = "xsd/hu/icellmobilsoft/coffee/dto/common/commonservice.xsd";
    private static final String EXPECTED_ERROR_MESSAGE = "cvc-complex-type.2.4.a: Invalid content was found starting with element '{\"http://common.dto.coffee.icellmobilsoft.hu/commonservice\":message}'." //
            + " One of '{\"http://common.dto.coffee.icellmobilsoft.hu/commonservice\":context}' is expected.";

    private final JaxbTool jaxbTool = JaxbToolCache.getJaxbTool();

    @Test
    void marshal() throws BaseException {
        BaseResponse baseResponse = new BaseResponse().withMessage("hello");
        assertEquals(TEST_XML, jaxbTool.marshalXML(baseResponse, null));
    }

    @Test
    void unmarshal() throws BaseException {
        BaseResponse baseResponse = jaxbTool.unmarshalXML(BaseResponse.class, TEST_XML.getBytes());
        assertEquals("hello", baseResponse.getMessage());
    }

    @Test
    void marshalWithValidation() {
        System.setProperty("roaster.xml.catalog.path", "super.catalog.xml");
        BaseResponse baseResponse = new BaseResponse().withMessage("hello");
        XsdProcessingException xsdProcessingException = assertThrows(
                XsdProcessingException.class,
                () -> jaxbTool.marshalXML(baseResponse, SCHEMA_PATH));
        assertEquals(1, xsdProcessingException.getErrors().size());
        assertEquals(EXPECTED_ERROR_MESSAGE, xsdProcessingException.getErrors().get(0).getError());
    }

    @Test
    void unmarshalWithValidation() {
        System.setProperty("roaster.xml.catalog.path", "super.catalog.xml");
        XsdProcessingException xsdProcessingException = assertThrows(
                XsdProcessingException.class,
                () -> jaxbTool.unmarshalXML(BaseResponse.class, TEST_XML.getBytes(), SCHEMA_PATH));
        assertEquals(1, xsdProcessingException.getErrors().size());
        assertEquals(EXPECTED_ERROR_MESSAGE, xsdProcessingException.getErrors().get(0).getError());
    }

}

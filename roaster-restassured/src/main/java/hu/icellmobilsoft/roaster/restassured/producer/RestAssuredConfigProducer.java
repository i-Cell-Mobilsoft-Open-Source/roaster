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
package hu.icellmobilsoft.roaster.restassured.producer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.icellmobilsoft.roaster.restassured.annotation.JSON;
import hu.icellmobilsoft.roaster.restassured.annotation.XML;
import io.restassured.config.EncoderConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;

/**
 * Producer class for RestAssured config
 *
 * @author mark.petrenyi
 * @author imre.scheffer
 * @since 0.0.1
 */
@ApplicationScoped
public class RestAssuredConfigProducer {

    /**
     * Produce JSON content setting config
     * 
     * @return JSON based RestAssuredConfig
     */
    @Produces
    @JSON
    private RestAssuredConfig produceJSONRestAssuredConfig() {
        return RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
            @Override
            public ObjectMapper create(Type cls, String charset) {
                ObjectMapper om = new ObjectMapper().findAndRegisterModules();
                om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                // isSet...() miatt nem tudja szépen kezelni a jackson (vagy bekerül plusz
                // propertyként az isSet éstéke, vagy nem kerülnek bele a
                // primitív típusok)
                om.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
                om.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
                om.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
                om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                return om;
            }
        }));
    }

    /**
     * @return JSON based RestAssuredConfig
     * @deprecated use inject with JSON Qualifier
     * 
     *             <pre>
     * &#64;Inject
     * &#64;JSON
     * private RestAssuredConfig restAssuredConfig
     *             </pre>
     */
    @Produces
    @Deprecated(forRemoval = true, since = "0.0.1")
    private RestAssuredConfig produceRestAssuredConfig() {
        return produceJSONRestAssuredConfig();
    }

    /**
     * Produce XML content setting config
     * 
     * @return XML based RestAssuredConfig
     */
    @Produces
    @XML
    private RestAssuredConfig produceXMLRestAssuredConfig() {
        return RestAssuredConfig.config().encoderConfig(
                EncoderConfig.encoderConfig().defaultCharsetForContentType(StandardCharsets.UTF_8, RequestSpecificationProducer.MEDIA_TYPE_XML));
    }
}

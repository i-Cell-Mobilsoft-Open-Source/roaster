/*-
 * #%L
 * Roaster
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

import jakarta.enterprise.inject.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;

/**
 * CDI Jackson2ObjectMapperFactory implementáció.<br>
 * <br>
 * Nagyobb scope kell rajta mint @Đependent, mert akkor nem lessz meg az objectMapper lekerésdezés lehetősége és azon állítása
 * 
 * @author imre.scheffer
 * @since 0.2.0
 * @see ObjectMapperConfigProducer
 */
@Model
public class Jackson2ObjectMapperFactoryImpl implements Jackson2ObjectMapperFactory {

    private ObjectMapper objectMapper;

    /**
     * Default constructor, constructs a new object.
     */
    public Jackson2ObjectMapperFactoryImpl() {
        super();
    }

    @Override
    public ObjectMapper create(Type cls, String charset) {
        return getObjectMapper();
    }

    /**
     * Roaster speficikus default JSON ObjectMapper
     */
    protected void initObjectMapper() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // az időt ne default "timestamp": 1600871093.907000000 formátumban írja hanem rendes ISO
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // isSet...() miatt nem tudja szépen kezelni a jackson (vagy bekerül plusz
        // propertyként az isSet éstéke, vagy nem kerülnek bele a
        // primitív típusok)
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    /**
     * Belső default ObjectMapper felülírása
     * 
     * @param objectMapper
     *            saját ObjectMapper
     * @return önmaga
     */
    public Jackson2ObjectMapperFactoryImpl withObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    /**
     * Getter az {@code objectMapper} mezőhöz, lazy init történhet
     * 
     * @return objectMapper mező értéke
     */
    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            initObjectMapper();
        }
        return objectMapper;
    }
}

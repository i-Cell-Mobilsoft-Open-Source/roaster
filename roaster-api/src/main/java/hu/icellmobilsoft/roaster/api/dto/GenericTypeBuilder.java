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
package hu.icellmobilsoft.roaster.api.dto;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateXmlUtil;
import hu.icellmobilsoft.roaster.api.Any;
import hu.icellmobilsoft.roaster.api.Condition;
import hu.icellmobilsoft.roaster.api.TestException;
import hu.icellmobilsoft.roaster.api.reflect.Setter;

/**
 * Generic class to build a dto object based on the XML annotations.
 *
 * @param <T>
 *            the xsd type
 */
public class GenericTypeBuilder<T> extends BaseTypeBuilder<T> {

    private static final int MAX_STRING_LENGTH = 30;

    private static final int MAX_ARRAY_SIZE = 100;

    private GenericTypeBuilder(Class<T> targetClass, Supplier<T> createEntity) {
        super(targetClass, createEntity);
    }

    /**
     * Creates a new {@link GenericTypeBuilder}
     * 
     * @param targetClass
     *            the class generated from xsd
     * @param createEntity
     *            factory for the xsd type
     * @return a new {@link GenericTypeBuilder}
     * @param <E>
     *            the xsd type
     */
    public static <E> GenericTypeBuilder<E> create(Class<E> targetClass, Supplier<E> createEntity) {
        Condition.expected(targetClass.getAnnotation(XmlType.class) != null, "Target type should be generated from XSD.");
        return new GenericTypeBuilder<>(targetClass, createEntity);
    }

    private static <E> List<Field> getAllFields(Class<E> clazz) {
        List<Field> result = new ArrayList<>();
        Class<?> tmp = clazz;
        while (tmp != Object.class) {
            result.addAll(Arrays.asList(tmp.getDeclaredFields()));
            tmp = tmp.getSuperclass();
        }
        return result;
    }

    /**
     * @param field
     *            the field of the entity
     * @return true if the field is annotated with {@code required=true}
     */
    private static boolean isRequiredField(final Field field) {
        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
        XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
        return (xmlElement != null && xmlElement.required()) || (xmlAttribute != null && xmlAttribute.required());
    }

    /**
     * @param field
     *            the field of the entity
     * @return true if the type of the field is an enumeration
     */
    private static boolean isEnumField(final Field field) {
        return field.getType().getAnnotation(XmlEnum.class) != null;
    }

    @Override
    protected void fillEntity(T entity) {
        for (Field field : getAllFields(getTargetClass())) {
            if (isRequiredField(field)) {
                XmlSchemaType xmlSchemaType = field.getAnnotation(XmlSchemaType.class);
                if (xmlSchemaType != null) {
                    handleXmlSchemaType(entity, field, xmlSchemaType);
                } else if (isEnumField(field)) {
                    Setter.setValue(entity, field, Any.of(field.getType()));
                } else if (field.getType().getAnnotation(XmlType.class) != null) {
                    IBaseBuilder<?> builder = Builder.get(field.getType());
                    Setter.setValue(entity, field, builder.build());
                } else {
                    setFieldValueThroughTargetType(entity, field);
                }
            } else if (field.getType().isPrimitive()) {
                Setter.setPrimitiveValue(entity, field);
            }
        }
    }

    private void handleXmlSchemaType(T entity, Field field, XmlSchemaType xmlSchemaType) {
        if (StringUtils.equals("string", xmlSchemaType.name())) {
            setFieldValueThroughTargetType(entity, field);
        } else if (StringUtils.equals("dateTime", xmlSchemaType.name())) {
            Setter.setValue(entity, field, DateXmlUtil.toXMLGregorianCalendar(Any.time()));

            Setter.setValue(entity, field, DateXmlUtil.toXMLGregorianCalendar(Any.time()));
        } else if (StringUtils.equals("date", xmlSchemaType.name())) {
            Setter.setValue(entity, field, DateXmlUtil.toXMLGregorianCalendar(DateUtil.clearTimePart(Any.time())));
        } else {
            throw new TestException(MessageFormat.format("Not supported XMLSchemaType: [{0}]", xmlSchemaType.name()));
        }
    }

    /**
     * Set the field value through the type of the field.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field to set
     * @param <T>
     *            the type of the entity
     */
    private static <T> void setFieldValueThroughTargetType(final T entity, final Field field) {
        Class<?> targetClass = field.getType();
        if (List.class.isAssignableFrom(targetClass)) {
            fillListField(entity, field);
        } else if (targetClass.isArray()) {
            fillArrayField(entity, field);
        } else if (targetClass.isEnum()) {
            Setter.setValue(entity, field, Any.enumItem(targetClass));
        } else if (String.class.isAssignableFrom(targetClass)) {
            Setter.setValue(entity, field, Any.string(MAX_STRING_LENGTH));
        } else if (targetClass.getAnnotation(XmlType.class) != null) {
            Setter.setValue(entity, field, Builder.get(targetClass).build());
        } else {
            throw new TestException(MessageFormat.format("Not supported target type: [{0}]", targetClass.getSimpleName()));
        }
    }

    /**
     * Fill the list fields with a randomly generated list
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field to set
     * @param <T>
     *            the type of the entity
     */
    private static <T> void fillListField(final T entity, final Field field) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> listTypeClass = (Class<?>) listType.getActualTypeArguments()[0];
        Setter.setValue(entity, field, AnyDto.array(listTypeClass, MAX_ARRAY_SIZE));
    }

    /**
     * Fill the array fields with a randomly generated array.
     *
     * @param entity
     *            the entity object
     * 
     * @param field
     *            the current field to set
     * @param <T>
     *            the type of the entity
     */
    private static <T> void fillArrayField(final T entity, final Field field) {
        if (Boolean.TYPE.equals(field.getType().getComponentType())) {
            Setter.setValue(entity, field, Any.booleanArray(Any.anInt(1, MAX_ARRAY_SIZE)));
        } else if (Byte.TYPE.equals(field.getType().getComponentType())) {
            Setter.setValue(entity, field, Any.byteArray(Any.anInt(1, MAX_ARRAY_SIZE)));
        } else if (Short.TYPE.equals(field.getType().getComponentType())) {
            Setter.setValue(entity, field, Any.shortArray(Any.anInt(1, MAX_ARRAY_SIZE)));
        } else if (Integer.TYPE.equals(field.getType().getComponentType())) {
            Setter.setValue(entity, field, Any.intArray(Any.anInt(1, MAX_ARRAY_SIZE)));
        } else if (Long.TYPE.equals(field.getType().getComponentType())) {
            Setter.setValue(entity, field, Any.longArray(Any.anInt(1, MAX_ARRAY_SIZE)));
        } else if (Float.TYPE.equals(field.getType().getComponentType())) {
            Setter.setValue(entity, field, Any.floatArray(Any.anInt(1, MAX_ARRAY_SIZE)));
        } else if (Double.TYPE.equals(field.getType().getComponentType())) {
            Setter.setValue(entity, field, Any.doubleArray(Any.anInt(1, MAX_ARRAY_SIZE)));
        } else {
            throw new TestException(
                    MessageFormat.format("Not supported target array type: [{0}]", field.getType().getComponentType().getSimpleName()));
        }

    }

}

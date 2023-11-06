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
package hu.icellmobilsoft.roaster.api.reflect;

import java.lang.reflect.Field;
import java.text.MessageFormat;

import hu.icellmobilsoft.roaster.api.Any;
import hu.icellmobilsoft.roaster.api.Condition;
import hu.icellmobilsoft.roaster.api.TestException;

/**
 * Class to set private field values from outside of the class.
 *
 */
public class Setter {

    /**
     * Default constructor, constructs a new object.
     */
    public Setter() {
        super();
    }

    /**
     * Set boolean field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final boolean value) {
        setValue(entity, field, field::setBoolean, value);
    }

    /**
     * Set boolean array field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the array value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final boolean[] value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set byte field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final byte value) {
        setValue(entity, field, field::setByte, value);
    }

    /**
     * Set byte array field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the array value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final byte[] value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set long field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final long value) {
        setValue(entity, field, field::setLong, value);
    }

    /**
     * Set long array field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the array value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final long[] value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set int field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final int value) {
        setValue(entity, field, field::setInt, value);
    }

    /**
     * Set int array field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the array value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final int[] value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set short field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final short value) {
        setValue(entity, field, field::setShort, value);
    }

    /**
     * Set short array field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the array value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final short[] value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set float field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final float value) {
        setValue(entity, field, field::setFloat, value);
    }

    /**
     * Set float array field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the array value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final float[] value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set double field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final double value) {
        setValue(entity, field, field::setDouble, value);
    }

    /**
     * Set double array field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the array value to set the field
     * @param <E>
     *            the type of the entity
     */
    public static <E> void setValue(final E entity, final Field field, final double[] value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set generic field value.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     * @param <V>
     *            the type of the value
     */
    public static <E, V> void setValue(final E entity, final Field field, final V value) {
        setValue(entity, field, field::set, value);
    }

    /**
     * Set generic field value with consumer
     * 
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param setter
     *            the method that set the value of the field through the entity
     * @param value
     *            the value to set the field
     * @param <E>
     *            the type of the entity
     * @param <V>
     *            the value type
     */
    public static <E, V> void setValue(final E entity, final Field field, FieldBiConsumer<E, V> setter, V value) {
        field.setAccessible(true);
        try {
            setter.accept(entity, value);
        } catch (IllegalAccessException e) {
            Condition.shouldNeverThrown("Field should be accessible.", e);
        }
        field.setAccessible(false);
    }

    /**
     * Set the fields with primitive type.
     *
     * @param entity
     *            the entity object
     * @param field
     *            the current field
     * @param <T>
     *            the type of the entity
     */
    public static <T> void setPrimitiveValue(final T entity, final Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isAssignableFrom(Boolean.TYPE)) {
            Setter.setValue(entity, field, Any.bool());
        } else if (fieldType.isAssignableFrom(Byte.TYPE)) {
            Setter.setValue(entity, field, Any.aByte());
        } else if (fieldType.isAssignableFrom(Short.TYPE)) {
            Setter.setValue(entity, field, Any.aShort());
        } else if (fieldType.isAssignableFrom(Integer.TYPE)) {
            Setter.setValue(entity, field, Any.anInt());
        } else if (fieldType.isAssignableFrom(Long.TYPE)) {
            Setter.setValue(entity, field, Any.aLong());
        } else if (fieldType.isAssignableFrom(Float.TYPE)) {
            Setter.setValue(entity, field, Any.aFloat());
        } else if (fieldType.isAssignableFrom(Double.TYPE)) {
            Setter.setValue(entity, field, Any.aDouble());
        } else {
            throw new TestException(MessageFormat.format("Unknown primitive type: [{0}].", fieldType.getSimpleName()));
        }
    }

    /**
     * Special consumer to use the methods of the {@code Field} class
     *
     * @param <E>
     *            the type of the entity
     * @param <V>
     *            the type of the value
     */
    @FunctionalInterface
    private interface FieldBiConsumer<E, V> {
        void accept(E entity, V value) throws IllegalAccessException;
    }
}

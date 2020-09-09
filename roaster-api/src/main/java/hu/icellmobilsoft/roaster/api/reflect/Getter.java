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
import java.util.ArrayList;
import java.util.List;

import hu.icellmobilsoft.roaster.api.Condition;

/**
 * Class to get private field values from outside of the class.
 *
 * NEJP-ről átemelve
 * 
 * @since 0.2.0
 */
public class Getter {

    private Getter() {
    }

    /**
     * @param ifaceClass
     *            the interface class
     * @param typeClass
     *            the class of the fields
     * @param <T>
     *            the type of the field
     * @param <I>
     *            the type of the interface
     * @return the list of the static field values with the type {@code T}
     */
    public static <T, I> List<T> getFieldsValue(Class<I> ifaceClass, Class<T> typeClass) {
        List<T> result = new ArrayList<>();
        for (Field field : ifaceClass.getDeclaredFields()) {
            if (field.getType().equals(typeClass)) {
                result.add(getFieldValue(field, typeClass));
            }
        }
        return result;
    }

    /**
     * @param field
     *            the field
     * @param typeClass
     *            the class of the fields type
     * @param <T>
     *            the type of the field
     * @return the value of the field (only if the field is static)
     */
    private static <T> T getFieldValue(final Field field, Class<T> typeClass) {
        Condition.expected(field.getType().equals(typeClass), "Field type should be the same as the typeClass.");
        Condition.expected(java.lang.reflect.Modifier.isStatic(field.getModifiers()), "Field should be static.");
        T result = null;
        boolean hasAccess = field.isAccessible();
        try {
            if (!hasAccess) {
                field.setAccessible(true);
            }
            result = (T) field.get(null);
        } catch (IllegalAccessException ex) {
            Condition.shouldNeverThrown(ex);
        } finally {
            if (!hasAccess) {
                field.setAccessible(false);
            }
        }
        return result;
    }

}

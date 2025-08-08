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
package hu.icellmobilsoft.roaster.hibernate.se;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import hu.icellmobilsoft.coffee.model.base.annotation.CreatedBy;
import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;
import hu.icellmobilsoft.coffee.model.base.audit.AuditProvider;
import hu.icellmobilsoft.coffee.model.base.exception.ProviderException;

/**
 * Test AuditProvider implementation. {@link AuditProvider} implementation which removes the use of CDI.
 *
 * @author martin.nagy
 * @since 2.6.0
 */
public class TestAuditProvider extends AuditProvider {

    private final Supplier<Object> principalSupplier;

    /**
     * Constructor for {@link TestAuditProvider}.
     * 
     * @param principalSupplier
     *            the supplier for the principal.
     */
    public TestAuditProvider(Supplier<Object> principalSupplier) {
        this.principalSupplier = principalSupplier;
    }

    @Override
    public void prePersist(Object entity) {
        Pair<List<Field>, List<Method>> pair = getAllFieldsAndMethods(entity.getClass());
        List<Field> allFields = pair.getLeft();
        for (Field field : allFields) {
            setPropertyIfAnnotated(entity, field, CreatedBy.class);
            if (field.isAnnotationPresent(ModifiedBy.class) && field.getAnnotation(ModifiedBy.class).onCreate()) {
                Object value = resolvePrincipal();
                setProperty(entity, field, value);
            }
        }
        for (Method method : pair.getRight()) {
            setPropertyIfGetterAnnotated(entity, allFields, method, CreatedBy.class);
            if (method.isAnnotationPresent(ModifiedBy.class) && method.getAnnotation(ModifiedBy.class).onCreate()) {
                Object value = resolvePrincipal();
                Field field = getFieldByMethod(method, allFields);
                setProperty(entity, field, value);
            }
        }
    }

    private void setPropertyIfGetterAnnotated(Object entity, List<Field> allFields, Method method, Class<? extends Annotation> annotationClass) {
        if (method.isAnnotationPresent(annotationClass)) {
            Object value = resolvePrincipal();
            Field field = getFieldByMethod(method, allFields);
            setProperty(entity, field, value);
        }
    }

    private void setPropertyIfAnnotated(Object entity, Field field, Class<? extends Annotation> annotationClass) {
        if (field.isAnnotationPresent(annotationClass)) {
            Object value = resolvePrincipal();
            setProperty(entity, field, value);
        }
    }

    @Override
    public void preUpdate(Object entity) {
        Pair<List<Field>, List<Method>> pair = getAllFieldsAndMethods(entity.getClass());
        List<Field> allFields = pair.getLeft();
        for (Field field : allFields) {
            setPropertyIfAnnotated(entity, field, ModifiedBy.class);
        }
        List<Method> allMethods = pair.getRight();
        for (Method method : allMethods) {
            setPropertyIfGetterAnnotated(entity, allFields, method, ModifiedBy.class);
        }
    }

    private void setProperty(Object entity, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (Exception e) {
            throw new ProviderException(
                    "Failed to write value [" + value + "] to field[" + field + "], entity [" + entity.getClass() + "]: " + e.getLocalizedMessage(),
                    e);
        }
    }

    private Object resolvePrincipal() {
        return principalSupplier.get();
    }

}

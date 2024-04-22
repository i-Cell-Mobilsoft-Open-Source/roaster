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
package hu.icellmobilsoft.roaster.weldunit;

import java.lang.annotation.Annotation;
import java.util.function.Function;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.WeldInitiator;
import org.mockito.Mockito;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.DefaultAppLoggerQualifier;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.roaster.api.Condition;
import hu.icellmobilsoft.roaster.weldunit.mock.BaseMockProxy;

/**
 * Class to help setup weld unit with our mocked classes.
 */
public class WeldMockBuilder {

    private WeldMockBuilder() {
    }

    /**
     * Create a mocked service proxy. {@code builder} method must be provided to builda the mocked proxy class.
     *
     * @param builder
     *            the mock creator
     * @param baseMockProxy
     *            the service proxy
     * @param <S>
     *            the original service type
     * @param <D>
     *            the service proxy type (test API needs it)
     * @return the mocked bean
     * @see WeldMockBuilder#mockBaseService(BaseMockProxy)
     */
    public static <S, D extends BaseMockProxy<S, D>> Bean<Object> createServiceBean(Function<BaseMockProxy<S, D>, S> builder,
            BaseMockProxy<S, D> baseMockProxy) {
        Condition.notNullAll("builder and baseMockProxy should not be null.", builder, baseMockProxy);
        return MockBean.builder().types(baseMockProxy.getServiceClass()).scope(ApplicationScoped.class).creating(builder.apply(baseMockProxy))
                .build();
    }

    /**
     * Create a mocked bean with the help of {@code builder} supplier.
     *
     * @param builder
     *            the function to mock the the Bean through Mockito
     * @param clazz
     *            the type of the bean
     * @param <T>
     *            the bean type
     * @return the bean created by the {@link MockBean#builder()}
     */
    public static <T> Bean<Object> createBean(Supplier<T> builder, Class<T> clazz) {
        Condition.notNullAll("builder and clazz should be not null.", builder, clazz);
        return MockBean.builder().types(clazz).scope(ApplicationScoped.class).creating(builder.get()).build();
    }

    /**
     * Create a mocked service class with the help of the service proxy
     *
     * @param baseMockProxy
     *            the service proxy
     * @param <S>
     *            the original service type
     * @param <D>
     *            the service proxy type (test API needs it)
     * @return the mocked, original service object
     */
    private static <S, D extends BaseMockProxy<S, D>> S mockBaseService(BaseMockProxy<S, D> baseMockProxy) {
        Condition.notNull(baseMockProxy, "The service proxy should not be null.");
        S result = null;
        try {
            result = baseMockProxy.mock();
        } catch (BaseException e) {
            Condition.shouldNeverThrown(e);
        }
        Condition.ensure(result != null, "Failed to create the service mock!");
        return result;
    }

    /**
     * Mock a bean through Mockito ({@link Mockito#mock(Class)}
     *
     * @param <T>
     *            the type of the bean
     * @param clazz
     *            the class of the bean
     * @return the mocked object
     */
    private static <T> T mockBean(Class<T> clazz) {
        Condition.notNull(clazz, "Input class should not be null.");
        return Mockito.mock(clazz);
    }

    /**
     * Create the builder object the same way as the {@link WeldInitiator#from(Weld)} method. The only difference is that it will give back our
     * Builder and not the {@link WeldInitiator.Builder}.
     *
     * @param beanClasses
     *            the class list what should be injected
     * @return the current builder object
     */
    public static WeldBuilder from(Class<?>... beanClasses) {
        Condition.notNullAll("Bean class list should not be null.", beanClasses);
        return new WeldBuilder(WeldInitiator.from(beanClasses));
    }

    /**
     * An wrapper around the base {@link WeldInitiator.Builder} class. It has some extra functionality to help mock our own classes without complex
     * class in the test classes.
     */
    public static final class WeldBuilder {

        private final WeldInitiator.Builder originalBuilder;

        /**
         * Constructor to provide a safely usable builder object. It will add always a log mocker (All Action class has Logger -> we can mock them by
         * default).
         *
         * @param originalBuilder
         *            the original WeldInitiator.Builder
         */
        WeldBuilder(WeldInitiator.Builder originalBuilder) {
            this.originalBuilder = originalBuilder;
            this.originalBuilder.addBeans(createLogBean());
        }

        /**
         * Create a mocked bean with the help of {@code builder} function.
         *
         * @param builder
         *            the function to mock the the Bean through Mockito
         * @param clazz
         *            the type of the bean
         * @param <T>
         *            the bean type
         * @return the bean created by the {@link MockBean#builder()}
         */
        private static <T> Bean<Object> createBean(Function<Class<T>, T> builder, Class<T> clazz) {
            return MockBean.builder().types(clazz).scope(ApplicationScoped.class).creating(builder.apply(clazz)).build();
        }

        /**
         * Create a mocked bean with the help of {@code builder} function with the given qualifiers
         *
         * @param builder
         *            the function to mock the the Bean through Mockito
         * @param clazz
         *            the type of the bean
         * @param qualifiers
         *            annotation
         * @param <T>
         *            the bean type
         * @return the bean created by the {@link MockBean#builder()}
         */
        private static <T> Bean<Object> createBeanWithQualifiers(Function<Class<T>, T> builder, Class<T> clazz, Annotation... qualifiers) {
            return MockBean.builder().types(clazz).scope(ApplicationScoped.class).creating(builder.apply(clazz)).qualifiers(qualifiers).build();
        }

        /**
         * Create an mock bean for the {@link AppLogger}
         *
         * @return the mocked logger bean
         */
        private static Bean<AppLogger> createLogBean() {
            return MockBean.<AppLogger> builder().types(AppLogger.class).qualifiers(new DefaultAppLoggerQualifier())
                    .creating(mockBean(AppLogger.class)).build();
        }

        /**
         * Wrapper around the default {@link WeldInitiator.Builder#addBeans(Bean[])} method.
         *
         * @param beans
         *            the bean list
         * @return the current builder object
         */
        public WeldBuilder addBeans(Bean<?>... beans) {
            Condition.notNullAll("", beans);
            originalBuilder.addBeans(beans);
            return this;
        }

        /**
         * This method will call {@link WeldInitiator.Builder#addBeans(Bean[])} method after creating the necessary mocking around the bean class.
         *
         * @param classes
         *            the bean list to add and mock
         * @return the current builder object
         */
        public final WeldBuilder addMockBeans(Class<?>... classes) {
            Condition.notNullAll("Mocked class list should not be empty", classes);
            for (Class<?> clazz : classes) {
                originalBuilder.addBeans(createBean(WeldMockBuilder::mockBean, clazz));
            }
            return this;
        }

        /**
         * This method will directly add a mocked bean
         *
         * @param clazz
         *            mocked class
         * @param mockBean
         *            mocked bean
         * @return weld builder
         */
        public final WeldBuilder addMockBean(Class<?> clazz, Object mockBean) {
            Condition.notNullAll("Mocked class should not be null", clazz);
            Condition.notNullAll("Mocked bean should not be null", mockBean);
            originalBuilder.addBeans(MockBean.builder().types(clazz).scope(ApplicationScoped.class).creating(mockBean).build());
            return this;
        }

        /**
         * This method will call {@link WeldInitiator.Builder#addBeans(Bean[])} method after creating the necessary mocking around the bean class.
         *
         * @param clazz
         *            mocked class
         * @param qualifiers
         *            annotations
         * @return weld builder
         */
        public final WeldBuilder addMockBeanWithQualifiers(Class<?> clazz, Annotation... qualifiers) {
            Condition.notNull(clazz, "Mocked class should not be empty");
            originalBuilder.addBeans(createBeanWithQualifiers(WeldMockBuilder::mockBean, clazz, qualifiers));
            return this;
        }

        /**
         * Wrapper method to add our services to the WeldInitiator. It will mock the service through the test fw.
         *
         * @param serviceProxy
         *            the service proxy
         * @param <S>
         *            the original service type
         * @param <D>
         *            the service proxy type (test API needs it)
         * @return the current builder object
         */
        public final <S, D extends BaseMockProxy<S, D>> WeldBuilder addServiceBean(BaseMockProxy<S, D> serviceProxy) {
            Condition.notNull(serviceProxy, "Service proxy should not be null.");
            originalBuilder.addBeans(WeldMockBuilder.createServiceBean(WeldMockBuilder::mockBaseService, serviceProxy));
            return this;
        }

        /**
         * Activate contexts for the given normal scopes for each test method execution.
         *
         * @param scopes
         *            cdi scopes
         * @return weld builder
         */
        @SafeVarargs
        public final WeldBuilder activate(Class<? extends Annotation>... scopes) {
            Condition.notNullAll("Scopes to active should not be null.", scopes);
            originalBuilder.activate(scopes);
            return this;
        }

        /**
         * Builds the WeldInitiator
         *
         * @return the built WeldInitiator
         */
        public final WeldInitiator build() {
            return originalBuilder.build();
        }
    }

}

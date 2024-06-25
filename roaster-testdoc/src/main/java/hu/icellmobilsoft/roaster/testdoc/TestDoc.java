/*-
 * #%L
 * Roaster
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.testdoc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import hu.icellmobilsoft.roaster.testdoc.config.TestDocConfig;

/**
 * Documentation file will be generated based on the annotated test classes.
 *
 * @author janos.hamrak
 * @since 2.3.0
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface TestDoc {

    /**
     * (Optional) The level of the generated table in the adoc file. If given it needs to be in the range of [0,5] otherwise we use fallback to
     * {@value TestDocConfig#DEFAULT_TITLE_HEADING_LEVEL}.
     *
     * @return the title level value
     * @since 2.3.0
     */
    int titleHeadingLevel() default TestDocConfig.DEFAULT_TITLE_HEADING_LEVEL;

}

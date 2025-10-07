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

/**
 * Lazy cache for {@link JaxbTool}
 * 
 * @author martin.nagy
 * @since 2.7.0
 */
public class JaxbToolCache {

    private static volatile JaxbTool jaxbTool;

    /**
     * Private constructor to avoid instantiation of this class.
     */
    private JaxbToolCache() {
    }

    /**
     * Returns the {@link JaxbTool} instance. If the instance is not yet created, it will be created and cached.
     * 
     * @return the {@link JaxbTool} instance
     */
    public static JaxbTool getJaxbTool() {
        if (jaxbTool == null) {
            synchronized (JaxbToolCache.class) {
                if (jaxbTool == null) {
                    jaxbTool = new JaxbTool();
                }
            }
        }
        return jaxbTool;
    }
}

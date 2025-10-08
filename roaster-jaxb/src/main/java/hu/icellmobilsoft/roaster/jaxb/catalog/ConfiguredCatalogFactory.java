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
package hu.icellmobilsoft.roaster.jaxb.catalog;

import java.net.URI;
import java.text.MessageFormat;

import javax.xml.catalog.Catalog;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Resolves the {@link Catalog} object using the {@value #CATALOG_XML_PATH} config property. The config property value is a list of catalog paths. The
 * catalog paths are resolved using the {@link ClassLoader#getResource(String)} method.
 *
 * @author mark.petrenyi
 * @author imre.scheffer
 * @author martin.nagy
 * @since 2.7.0
 */
public class ConfiguredCatalogFactory {
    private static final String CATALOG_XML_PATH = "roaster.xml.catalog.path";

    /**
     * Private constructor for util class.
     */
    private ConfiguredCatalogFactory() {
    }

    /**
     * Producer for {@code @Inject {@link Catalog}} feature
     *
     * @return {@code Catalog} object
     */
    public static Catalog createCatalog() {
        String[] catalogPaths = ConfigProvider.getConfig()
                .getOptionalValue(CATALOG_XML_PATH, String[].class)
                .orElseThrow(() -> new IllegalStateException(MessageFormat.format("Catalog config of key [{0}] not found!", CATALOG_XML_PATH)));

        return CatalogManager.catalog(CatalogFeatures.defaults(), getCatalogUris(catalogPaths));
    }

    private static URI[] getCatalogUris(String[] catalogPaths) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URI[] catalogUris = new URI[catalogPaths.length];
        for (int i = 0; i < catalogUris.length; i++) {
            catalogUris[i] = resolveUri(catalogPaths[i], contextClassLoader);
        }
        return catalogUris;
    }

    private static URI resolveUri(String catalogPath, ClassLoader contextClassLoader) {
        try {
            return contextClassLoader.getResource(catalogPath).toURI();
        } catch (Exception e) {
            throw new IllegalStateException(MessageFormat.format("Can not resolve catalog:[{0}], [{1}]", catalogPath, e.getLocalizedMessage()), e);
        }
    }
}

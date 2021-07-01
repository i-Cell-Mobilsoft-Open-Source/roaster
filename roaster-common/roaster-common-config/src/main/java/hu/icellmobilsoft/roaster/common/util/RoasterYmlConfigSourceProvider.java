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
package hu.icellmobilsoft.roaster.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import hu.icellmobilsoft.coffee.se.logging.Logger;

import io.smallrye.config.source.yaml.YamlConfigSource;

/**
 * Config source provider roaster-*.yml konfig fájlok beolvasásához. Inspired by {@link io.smallrye.config.source.yaml.YamlConfigSourceProvider}
 *
 * @author mark.petrenyi
 */
public class RoasterYmlConfigSourceProvider implements ConfigSourceProvider {

    private static final int BASE_ORDINAL = ConfigSource.DEFAULT_ORDINAL + 50;

    private static final String META_INF_ROASTER_CONFIG_RESOURCE_PATTERN = "META-INF/roaster-{0}.yml";
    private static final String DEFAULT_YML_SUFFIX = "defaults";
    private static final String PROFILE_KEY = "profile";

    private Logger log = Logger.getLogger(RoasterYmlConfigSourceProvider.class);

    /**
     * Gets config source.
     *
     * @param classLoader
     *            the class loader
     * @param resource
     *            the resource
     * @param ordinal
     *            the ordinal
     * @return the config source
     */
    private Optional<ConfigSource> getConfigSource(ClassLoader classLoader, String resource, int ordinal) {

        try (InputStream stream = classLoader.getResourceAsStream(resource)) {
            if (stream != null) {
                return Optional.of(new YamlConfigSource(resource, stream, ordinal));
            }
        } catch (IOException e) {
            log.trace("Could not read yaml config source.", e);
        }
        log.trace("Could not find resource:[{0}]!", resource);
        return Optional.empty();
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {

        final List<ConfigSource> configSources = new ArrayList<>();
        getConfigSource(classLoader, MessageFormat.format(META_INF_ROASTER_CONFIG_RESOURCE_PATTERN, DEFAULT_YML_SUFFIX), BASE_ORDINAL)
                .ifPresent(configSources::add);
        Optional<String> profilesString = defaultConfig().getOptionalValue(PROFILE_KEY, String.class);
        if (profilesString.isPresent()) {
            String[] profiles = profilesString.get().split(",");
            if (ArrayUtils.isNotEmpty(profiles)) {
                int offset = profiles.length;
                for (String profile : profiles) {
                    // A sorrend szerinti offset-ben állítjuk a profile yml-t.
                    // Pl.: -Dprofile=local,localartemis paraméterre a yml precendencia:
                    // roaster-local.yml > roaster-localartemis.yml > roaster-default.yml)
                    getConfigSource(classLoader, MessageFormat.format(META_INF_ROASTER_CONFIG_RESOURCE_PATTERN, profile.trim()),
                            BASE_ORDINAL + offset).ifPresent(configSources::add);
                    offset--;
                }
            }
        }

        return Collections.unmodifiableList(configSources);
    }

    private Config defaultConfig() {
        // Default config sources (sys, env, mp-c.properties)
        return ConfigProviderResolver.instance().getBuilder().addDefaultSources().build();
    }
}

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
package hu.icellmobilsoft.roaster.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

/**
 * Cache for jedis connections based on {@link RedisConfig} keys.
 *
 * @author martin.nagy
 * @since 2.7.0
 */
public class JedisConnectionCache {
    private static final Map<String, UnifiedJedis> JEDIS_BY_CONFIG_KEY = new ConcurrentHashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private JedisConnectionCache() {
    }

    /**
     * Retrieves a {@link UnifiedJedis} instance associated with the provided configuration key. If no instance exists for the given key, a new
     * {@link UnifiedJedis} instance is created based on the corresponding {@link RedisConfig}.
     *
     * @param configKey
     *            the configuration key used to retrieve or create a {@link UnifiedJedis} instance
     * @return the {@link UnifiedJedis} instance associated with the given configuration key
     */
    public static UnifiedJedis get(String configKey) {
        return JEDIS_BY_CONFIG_KEY.computeIfAbsent(configKey, key -> createJedisPooled(new RedisConfig(configKey)));
    }

    private static JedisPooled createJedisPooled(RedisConfig config) {
        return new JedisPooled(
                getConnectionPoolConfig(config),
                config.getHost(),
                config.getPort(),
                config.getTimeout(),
                config.getPassword(),
                config.getDatabase());
    }

    private static ConnectionPoolConfig getConnectionPoolConfig(RedisConfig config) {
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(config.getPoolMaxTotal());
        poolConfig.setMaxIdle(config.getPoolMaxIdle());
        return poolConfig;
    }
}

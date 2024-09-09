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
package hu.icellmobilsoft.roaster.redis;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import redis.clients.jedis.Jedis;

/**
 * Class representing REDIS functionality
 *
 * @author balazs.joo
 * @author imre.scheffer
 */
@Model
public class RedisHandler {

    /**
     * Default constructor, constructs a new object.
     */
    public RedisHandler() {
        super();
    }

    /**
     * Gets data from REDIS, identified by {@code redisConfigKey}, for given {@code valueKey}, and responses with given class
     *
     * @param <T>
     *            generic type
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKey
     *            key for value
     * @param c
     *            response class
     * @return data if found
     * @throws BaseException
     *             if data not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getRedisData(String redisConfigKey, String valueKey, Class<T> c) throws BaseException {
        checkKey(valueKey);
        RedisManager redis = getRedisManager(redisConfigKey);
        Optional<String> result = redis.runWithConnection(Jedis::get, "get", valueKey);
        if (result.isEmpty()) {
            throw new BONotFoundException("Invalid redis data found for key [" + valueKey + "] and type [" + c.getSimpleName() + "]!");
        } else if (c == String.class) {
            return (T) result.get();
        } else {
            return JsonUtil.toObject(result.get(), c);
        }
    }

    /**
     * Gets data from REDIS, identified by {@code redisConfigKey}, for given {@code valueKey}, and responses with given class wrapped in
     * {@link Optional}
     *
     * @param <T>
     *            generic type
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKey
     *            key for value
     * @param c
     *            response class
     * @return data if found wrapped in Optional, if not found, then Optional.empty()
     * @throws BaseException
     *             exception
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getRedisDataOpt(String redisConfigKey, String valueKey, Class<T> c) throws BaseException {
        checkKey(valueKey);
        RedisManager redis = getRedisManager(redisConfigKey);
        Optional<String> result = redis.runWithConnection(Jedis::get, "get", valueKey);
        if (result.isEmpty()) {
            return Optional.empty();
        } else if (c == String.class) {
            return Optional.of((T) result.get());
        } else {
            return Optional.ofNullable(JsonUtil.toObject(result.get(), c));
        }
    }

    /**
     * Puts data in given REDIS db
     *
     * @param <T>
     *            generic type
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKey
     *            key for value
     * @param redisData
     *            data to store
     * @return Status code reply
     * @throws BaseException
     *             exception
     */
    public <T> Optional<String> setRedisData(String redisConfigKey, String valueKey, T redisData) throws BaseException {
        checkKey(valueKey);
        String redisDataString = JsonUtil.toJson(redisData);
        RedisManager redis = getRedisManager(redisConfigKey);
        return redis.runWithConnection(Jedis::set, "set", valueKey, redisDataString);
    }

    /**
     * Puts data in given REDIS db, with expiration time
     *
     * @param <T>
     *            generic type
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKey
     *            key for value
     * @param secondsToExpire
     *            expire time in seconds
     * @param redisData
     *            data to store
     * @return Status code reply
     * @throws BaseException
     *             exception
     */
    public <T> Optional<String> setRedisDataExp(String redisConfigKey, String valueKey, int secondsToExpire, T redisData) throws BaseException {
        checkKey(valueKey);
        String redisDataString = JsonUtil.toJson(redisData);
        RedisManager redis = getRedisManager(redisConfigKey);
        return redis.runWithConnection(Jedis::setex, "setex", valueKey, secondsToExpire, redisDataString);
    }

    /**
     * Removes data from given REDIS db, with given key
     *
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKey
     *            key for value
     * @return number of removed keys
     * @throws BaseException
     *             exception
     */
    public Optional<Long> removeRedisData(String redisConfigKey, String valueKey) throws BaseException {
        checkKey(valueKey);
        RedisManager redis = getRedisManager(redisConfigKey);
        return redis.runWithConnection(Jedis::del, "del", valueKey);
    }

    /**
     * Removes data from given REDIS db, with given keys
     *
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKeys
     *            key list for values
     * @return number of removed keys
     * @throws BaseException
     *             exception
     */
    public Optional<Long> removeAllRedisData(String redisConfigKey, List<String> valueKeys) throws BaseException {
        if (valueKeys == null) {
            throw new BONotFoundException("valueKeys is empty.");
        } else if (valueKeys.isEmpty()) {
            return Optional.empty();
        }

        String[] keys = new String[valueKeys.size()];
        keys = valueKeys.toArray(keys);

        RedisManager redis = getRedisManager(redisConfigKey);
        return redis.runWithConnection(Jedis::del, "del", keys);
    }

    /**
     * Erases all data from given REDIS db
     *
     * @param redisConfigKey
     *            REDIS db configuration key
     * @throws BaseException
     *             exception
     */
    public void removeAllRedisData(String redisConfigKey) throws BaseException {
        RedisManager redis = getRedisManager(redisConfigKey);
        redis.runWithConnection(Jedis::flushDB, "flushDB");
    }

    private RedisManager getRedisManager(String redisConfigKey) throws BaseException {
        if (StringUtils.isBlank(redisConfigKey)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, "Redis config key is empty.");
        }
        return CDI.current().select(RedisManager.class, new RedisConnection.Literal(redisConfigKey)).get();
    }

    private void checkKey(String valueKey) throws BONotFoundException {
        if (StringUtils.isBlank(valueKey)) {
            throw new BONotFoundException("valueKey is empty!");
        }
    }
}

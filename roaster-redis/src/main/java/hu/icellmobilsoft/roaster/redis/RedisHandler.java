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
package hu.icellmobilsoft.roaster.redis;

import java.util.List;
import java.util.Optional;

import javax.enterprise.inject.Model;
import javax.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
import hu.icellmobilsoft.coffee.module.redis.producer.JedisConnectionProducer;
import hu.icellmobilsoft.coffee.module.redis.producer.JedisPoolProducer;
import hu.icellmobilsoft.coffee.module.redis.producer.RedisConfigProducer;
import hu.icellmobilsoft.coffee.module.redis.producer.RedisServiceProducer;
import hu.icellmobilsoft.coffee.module.redis.service.RedisService;

/**
 * Class representing REDIS functionality
 *
 * @author balazs.joo
 */
@Model
public class RedisHandler {

    /**
     * In case of REDIS functionality usage, this Classes are needed to pass for Weld configuration
     * <p>
     * {@code
     * 
    <p>
     * &#64;Override
     * protected void configureWeld(Weld weld) {
     * weld.addBeanClasses(RedisHandler.REDIS_CLASSES_NEEDED_FOR_WELD);
     * super.configureWeld(weld); } }
     */
    public static final Class<?>[] REDIS_CLASSES_NEEDED_FOR_WELD = { RedisHandler.class, JedisConnectionProducer.class, JedisPoolProducer.class,
            RedisService.class, RedisServiceProducer.class, RedisConfigProducer.class, ManagedRedisConfig.class };

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
    public <T> T getRedisData(String redisConfigKey, String valueKey, Class<T> c) throws BaseException {
        RedisService service = getRedisService(redisConfigKey);
        return service.getRedisData(valueKey, c);
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
    public <T> Optional<T> getRedisDataOpt(String redisConfigKey, String valueKey, Class<T> c) throws BaseException {
        RedisService service = getRedisService(redisConfigKey);
        return service.getRedisDataOpt(valueKey, c);
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
    public <T> String setRedisData(String redisConfigKey, String valueKey, T redisData) throws BaseException {
        RedisService service = getRedisService(redisConfigKey);
        return service.setRedisData(valueKey, redisData);
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
    public <T> String setRedisDataExp(String redisConfigKey, String valueKey, int secondsToExpire, T redisData) throws BaseException {
        RedisService service = getRedisService(redisConfigKey);
        return service.setRedisData(valueKey, secondsToExpire, redisData);
    }

    /**
     * Removes data from given REDIS db, with given key
     *
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKey
     *            key for value
     * @throws BaseException
     *             exception
     */
    public void removeRedisData(String redisConfigKey, String valueKey) throws BaseException {
        RedisService service = getRedisService(redisConfigKey);
        service.removeRedisData(valueKey);
    }

    /**
     * Removes data from given REDIS db, with given keys
     *
     * @param redisConfigKey
     *            REDIS db configuration key
     * @param valueKeys
     *            key list for values
     * @throws BaseException
     *             exception
     */
    public void removeAllRedisData(String redisConfigKey, List<String> valueKeys) throws BaseException {
        RedisService service = getRedisService(redisConfigKey);
        service.removeAllRedisData(valueKeys);
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
        RedisService service = getRedisService(redisConfigKey);
        service.removeAllRedisData();
    }

    private RedisService getRedisService(String redisConfigKey) throws BaseException {
        if (StringUtils.isBlank(redisConfigKey)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, "Redis config key is empty.");
        }
        return CDI.current().select(RedisService.class, new RedisConnection.Literal(redisConfigKey)).get();
    }
}

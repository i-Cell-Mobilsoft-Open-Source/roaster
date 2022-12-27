/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.testsuite.redis;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.redis.RedisHandler;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;
import redis.clients.jedis.Jedis;

/**
 * Redis docker instance tests to validate the Redis calls
 * 
 * @author imre.scheffer
 * @since 0.8.0
 */
@Tag(TestSuiteGroup.INTEGRATION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing RedisHandler")
class RedisHandlerIT extends BaseWeldUnitType {

    public static final String REDIS_KONFIG_KEY = "test";
    public static final String TEST_KEY = "testKey";
    public static final String TEST_VALUE = "test value";

    public static final GenericContainer<?> REDIS_SERVER = new GenericContainer<>(DockerImageName.parse("redis:alpine3.16")).withExposedPorts(6379)
            .withAccessToHost(true);

    private static RedisContainer redisCache;

    @Inject
    @RedisConnection(configKey = REDIS_KONFIG_KEY)
    private RedisManager redisManager;

    @Inject
    private RedisHandler redisHandler;

    @BeforeAll
    static void beforeAll() {
        REDIS_SERVER.start();
        Jedis jedis = new Jedis(REDIS_SERVER.getHost(), REDIS_SERVER.getMappedPort(6379));
        System.setProperty("coffee.redis.test.host", REDIS_SERVER.getHost());
        System.setProperty("coffee.redis.test.port", REDIS_SERVER.getMappedPort(6379).toString());
        redisCache = new RedisContainer(jedis);
    }

    @AfterAll
    static void afterAll() {
        REDIS_SERVER.close();
    }

    @Test
    @DisplayName("Testing redis info response")
    void info() throws BaseException {
        String managerInfo = redisManager.runWithConnection(Jedis::info, "info").get();
        String jedisInfo = redisCache.getJedis().info();
        Assert.assertNotNull(managerInfo);
        Assert.assertNotNull(jedisInfo);
    }

    @Test
    @DisplayName("Testing redisHandler.getRedisData")
    void getRedisData() throws BaseException {
        String key = TEST_KEY + "1";
        redisCache.getJedis().set(key, TEST_VALUE);
        String handlerData = redisHandler.getRedisData(REDIS_KONFIG_KEY, key, String.class);
        Assert.assertEquals(TEST_VALUE, handlerData);
    }

    @Test
    @DisplayName("Testing redisHandler.getRedisDataOpt")
    void getRedisDataOpt() throws BaseException {
        String key = TEST_KEY + "2";
        redisCache.getJedis().set(key, TEST_VALUE);
        String handlerData = redisHandler.getRedisDataOpt(REDIS_KONFIG_KEY, key, String.class).get();
        Assert.assertEquals(TEST_VALUE, handlerData);
    }

    @Test
    @DisplayName("Testing redisHandler.setRedisData")
    void setRedisData() throws BaseException {
        String key = TEST_KEY + "3";
        String handlerData = redisHandler.setRedisData(REDIS_KONFIG_KEY, key, TEST_VALUE).get();
        String jedisData = redisCache.getJedis().get(key);
        Assert.assertEquals("OK", handlerData);
        Assert.assertEquals(TEST_VALUE, JsonUtil.toObject(jedisData, String.class));
    }

    @Test
    @DisplayName("Testing redisHandler.setRedisDataExp")
    void setRedisDataExp() throws BaseException {
        String key = TEST_KEY + "4";
        String handlerData = redisHandler.setRedisDataExp(REDIS_KONFIG_KEY, key, 60, TEST_VALUE).get();
        String jedisData = redisCache.getJedis().get(key);
        Assert.assertEquals("OK", handlerData);
        Assert.assertEquals(TEST_VALUE, JsonUtil.toObject(jedisData, String.class));
    }

    @Test
    @DisplayName("Testing redisHandler.removeRedisData")
    void removeRedisData() throws BaseException {
        String key = TEST_KEY + "5";
        redisCache.getJedis().set(key, TEST_VALUE);
        long handlerData = redisHandler.removeRedisData(REDIS_KONFIG_KEY, key).get();
        String jedisData = redisCache.getJedis().get(key);
        Assert.assertEquals(1, handlerData);
        Assert.assertNull(jedisData);
    }

    @Test
    @DisplayName("Testing redisHandler.removeAllRedisData with valueKeys")
    void removeAllRedisDataValueKeys() throws BaseException {
        String key6 = TEST_KEY + "6";
        String key7 = TEST_KEY + "7";
        redisCache.getJedis().set(key6, TEST_VALUE);
        redisCache.getJedis().set(key7, TEST_VALUE);
        long handlerData = redisHandler.removeAllRedisData(REDIS_KONFIG_KEY, List.of(key6, key7)).get();
        String jedisData = redisCache.getJedis().get(key6);
        Assert.assertEquals(2, handlerData);
        Assert.assertNull(jedisData);
    }

    @Test
    @DisplayName("Testing redisHandler.removeAllRedisData")
    void removeAllRedisData() throws BaseException {
        String key = TEST_KEY + "8";
        redisCache.getJedis().set(key, TEST_VALUE);
        redisHandler.removeAllRedisData(REDIS_KONFIG_KEY);
        String jedisData = redisCache.getJedis().get(key);
        Assert.assertNull(jedisData);
    }
}

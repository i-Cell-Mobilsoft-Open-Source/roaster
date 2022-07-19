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

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.roaster.redis.RedisHandler;
import hu.icellmobilsoft.roaster.testsuite.tedis.RedisContainer;
import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;
import redis.clients.jedis.Jedis;

/**
 * Redis docker instance tests to validate the Redis calls
 * 
 * @author imre.scheffer
 * @since 0.8.0
 */
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisIT extends BaseWeldUnitType {

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
        // System.out.println("getContainerIpAddress:" + MOCK_SERVER.getContainerIpAddress());
        // System.out.println("getIpAddress:" + MOCK_SERVER.getIpAddress());
        // System.out.println("getHost:" + MOCK_SERVER.getHost());
        // sajnos a fenti megoldasokbol "localhost"-ot ad vissza, amire nem reagal redisManager
        // https://github.com/testcontainers/testcontainers-java/issues/1719
        String host = REDIS_SERVER.getContainerInfo().getNetworkSettings().getNetworks().entrySet().iterator().next().getValue().getIpAddress();
        System.setProperty("coffee.redis.test.host", host);
        redisCache = new RedisContainer(jedis);
    }

    @AfterAll
    static void afterAll() {
        REDIS_SERVER.close();
    }

    @Test
    void info() throws BaseException {
        String managerInfo = redisManager.runWithConnection(Jedis::info, "info").get();
        String jedisInfo = redisCache.getJedis().info();
        Assert.assertNotNull(managerInfo);
        Assert.assertNotNull(jedisInfo);
    }

    @Test
    void getRedisData() throws BaseException {
        redisCache.getJedis().set(TEST_KEY, TEST_VALUE);
        String handlerData = redisHandler.getRedisData(REDIS_KONFIG_KEY, TEST_KEY, String.class);
        Assert.assertEquals(TEST_VALUE, handlerData);
    }

    @Test
    void getRedisDataOpt() throws BaseException {
        redisCache.getJedis().set(TEST_KEY, TEST_VALUE);
        String handlerData = redisHandler.getRedisDataOpt(REDIS_KONFIG_KEY, TEST_KEY, String.class).get();
        Assert.assertEquals(TEST_VALUE, handlerData);
    }
}

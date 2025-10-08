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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.module.redisstream.common.RedisStreamUtil;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamMessageParameter;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.UnifiedJedis;

/**
 * Handles publishing messages to a Redis stream using a Jedis client. This class interacts with Redis streams to publish messages with optional
 * parameters and manage group-based stream keys.
 * 
 * @author martin.nagy
 * @since 2.7.0
 */
public class RedisStreamPublisher {
    private final UnifiedJedis jedis;
    private final String group;

    /**
     * Constructs a new instance of the RedisStreamPublisher using the provided configuration key and group.
     *
     * @param configKey
     *            the configuration key used to retrieve a Jedis connection from the JedisConnectionCache
     * @param group
     *            the group identifier used to determine the stream key for Redis operations
     */
    public RedisStreamPublisher(String configKey, String group) {
        jedis = JedisConnectionCache.get(configKey);
        this.group = group;
    }

    /**
     * Publishes a message to a Redis stream with an optional parameter map.
     *
     * @param streamMessage
     *            The message to be published to the Redis stream. Cannot be null.
     * @return An {@code Optional} containing the {@code StreamEntryID} of the newly added entry in the stream, or an empty {@code Optional} if the
     *         operation fails.
     */
    public Optional<StreamEntryID> publish(String streamMessage) {
        return publish(streamMessage, null);
    }

    /**
     * Publishes a message to a Redis stream with an optional set of parameters.
     *
     * @param streamMessage
     *            The message to be published to the Redis stream. Cannot be null.
     * @param parameters
     *            A map of additional string key-value pairs to include in the published message. Can be null.
     * @return An {@code Optional} containing the {@code StreamEntryID} of the newly added entry in the stream, or an empty {@code Optional} if the
     *         operation fails.
     */
    public Optional<StreamEntryID> publish(String streamMessage, Map<String, String> parameters) {
        Map<String, String> jedisMessage = createJedisMessage(streamMessage, parameters);
        return Optional.ofNullable(jedis.xadd(RedisStreamUtil.streamKey(group), StreamEntryID.NEW_ENTRY, jedisMessage));
    }

    /**
     * Creates a formatted message map to be used with Redis streams, including a flow ID and the provided stream message.
     *
     * @param streamMessage
     *            the core message to be included in the Redis stream. Cannot be null.
     * @param parameters
     *            an optional map of additional key-value pairs to include in the message. Can be null.
     * @return a map containing the formatted Redis stream message, including the flow ID and any additional parameters.
     */
    protected Map<String, String> createJedisMessage(String streamMessage, Map<String, String> parameters) {
        Map<String, String> keyValues = new HashMap<>();
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_FLOW_ID, getFlowIdMessage(parameters));
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_MESSAGE, streamMessage);
        if (parameters != null) {
            keyValues.putAll(parameters);
        }
        return keyValues;
    }

    /**
     * Generates a flow ID message based on the provided parameters and the current session's log context.
     *
     * @param parameters
     *            a map of key-value pairs containing additional information that may influence the flow ID. Can include a custom flow ID extension
     *            under a specific key, or be null.
     * @return a flow ID message. This will be the current session's flow ID if available, appended with an optional extension from the parameters. If
     *         neither are available, a random flow ID is generated.
     */
    protected String getFlowIdMessage(Map<String, String> parameters) {
        String flowIdMessage = MDC.get(LogConstants.LOG_SESSION_ID);
        if (parameters == null) {
            return flowIdMessage == null ? RandomUtil.generateId() : flowIdMessage;
        }
        return Optional.ofNullable(parameters.get(StreamMessageParameter.FLOW_ID_EXTENSION.getMessageKey()))
                .map(extension -> flowIdMessage + "_" + extension)
                .orElse(flowIdMessage);
    }
}

/*
 * Copyright 2018 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.redisvelocity.api;

import java.util.Optional;

public abstract class RedisVelocity {
    
    public static final String ID = "redisvelocity";
    public static final String NAME = "RedisVelocity";
    public static final String VERSION = "${version}";
    public static final String DESCRIPTION = "Redis Velocity";
    public static final String AUTHORS = "LX_Gaming";
    public static final String SOURCE = "https://github.com/LXGaming/RedisVelocity";
    public static final String WEBSITE = "https://lxgaming.github.io/";
    
    private static RedisVelocity instance;
    
    protected RedisVelocity() {
        instance = this;
    }
    
    /**
     * @param channel The channel to send this message
     * @param message The message to send
     * @throws IllegalArgumentException If the provided channel is blank
     * @throws IllegalStateException    If Redis is currently unavailable
     */
    public abstract void sendMessage(String channel, String message) throws IllegalArgumentException, IllegalStateException;
    
    /**
     * @param channels The channels to register
     * @throws IllegalArgumentException If any of the provided channels are blank
     * @throws IllegalArgumentException If any of the provided channels use the internal namespace
     */
    public abstract void registerChannels(String... channels) throws IllegalArgumentException;
    
    /**
     * @param channels The channels to unregister
     * @throws IllegalArgumentException If any of the provided channels are blank
     * @throws IllegalArgumentException If any of the provided channels use the internal namespace
     */
    public abstract void unregisterChannels(String... channels) throws IllegalArgumentException;
    
    /**
     * @return The proxy id, or {@link Optional#empty()} if unknown
     */
    public abstract Optional<String> getProxyId();
    
    /**
     * @return The proxy channel, or {@link Optional#empty()} if unknown
     */
    public Optional<String> getProxyChannel() {
        return getProxyId().map(id -> RedisVelocity.ID + "-" + id);
    }
    
    /**
     * @return The RedisVelocity instance
     */
    public static RedisVelocity getInstance() {
        return instance;
    }
}
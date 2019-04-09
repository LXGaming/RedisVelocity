/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.redisvelocity.plugin.service;

import nz.co.lolnet.redisvelocity.api.RedisVelocity;
import nz.co.lolnet.redisvelocity.api.util.Reference;
import nz.co.lolnet.redisvelocity.plugin.VelocityPlugin;
import nz.co.lolnet.redisvelocity.plugin.configuration.Config;
import nz.co.lolnet.redisvelocity.plugin.listener.RedisListener;
import nz.co.lolnet.redisvelocity.plugin.manager.ServiceManager;
import nz.co.lolnet.redisvelocity.plugin.util.Toolbox;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

public class RedisService extends AbstractService {
    
    private final Set<String> channels = Toolbox.newHashSet();
    private final RedisListener redisListener = new RedisListener();
    private JedisPool jedisPool;
    private int maximumReconnectDelay;
    private int reconnectTimeout = 2;
    
    @Override
    public boolean prepare() {
        getChannels().add(Reference.ID + "-all");
        getChannels().add(Reference.ID + "-data");
        RedisVelocity.getInstance().getProxyChannel().ifPresent(getChannels()::add);
        
        VelocityPlugin.getInstance().getConfig().map(Config::getRedis).ifPresent(redis -> {
            if (redis.isAutoReconnect()) {
                maximumReconnectDelay = redis.getMaximumReconnectDelay();
            } else {
                maximumReconnectDelay = 0;
            }
            
            if (getJedisPool() == null) {
                JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                jedisPoolConfig.setMaxTotal(redis.getMaximumPoolSize());
                jedisPoolConfig.setMaxIdle(redis.getMaximumIdle());
                jedisPoolConfig.setMinIdle(redis.getMinimumIdle());
                this.jedisPool = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getPort(), Protocol.DEFAULT_TIMEOUT, redis.getPassword());
            }
        });
        
        return getJedisPool() != null && !getJedisPool().isClosed();
    }
    
    @Override
    public void execute() throws Exception {
        try (Jedis jedis = getJedisPool().getResource()) {
            RedisVelocity.getInstance().getProxyChannel().ifPresent(jedis::clientSetname);
            VelocityPlugin.getInstance().getLogger().info("Connected to Redis");
            jedis.subscribe(getRedisListener(), getChannels().toArray(new String[0]));
        } catch (JedisConnectionException ex) {
            VelocityPlugin.getInstance().getLogger().warn("Got disconnected from Redis...");
            if (reconnect()) {
                ServiceManager.schedule(this);
            }
        }
    }
    
    private boolean reconnect() throws InterruptedException {
        if (maximumReconnectDelay <= 0) {
            return false;
        }
        
        VelocityPlugin.getInstance().getLogger().warn("Attempting to reconnect in {}", Toolbox.getTimeString(reconnectTimeout * 1000));
        while (!getJedisPool().isClosed()) {
            Thread.sleep(reconnectTimeout * 1000);
            VelocityPlugin.getInstance().getLogger().warn("Attempting to reconnect!");
            
            try (Jedis jedis = getJedisPool().getResource()) {
                reconnectTimeout = 2;
                return true;
            } catch (JedisConnectionException ex) {
                reconnectTimeout = Math.min(reconnectTimeout << 1, maximumReconnectDelay);
                VelocityPlugin.getInstance().getLogger().warn("Reconnect failed! Next attempt in {}", Toolbox.getTimeString(reconnectTimeout * 1000));
            }
        }
        
        return false;
    }
    
    public void shutdown() {
        if (getRedisListener() != null && getRedisListener().isSubscribed()) {
            getRedisListener().unsubscribe();
        }
        
        if (getJedisPool() != null && !getJedisPool().isClosed()) {
            getJedisPool().close();
        }
        
        if (isRunning()) {
            getScheduledFuture().cancel(false);
        }
    }
    
    public void publish(String channel, String message) throws IllegalStateException {
        try (Jedis jedis = getJedisPool().getResource()) {
            jedis.publish(channel, message);
        } catch (JedisConnectionException ex) {
            throw new IllegalStateException("Encountered a JedisConnection error while attempting to publish");
        }
    }
    
    public void subscribe(String... channels) {
        try {
            getChannels().addAll(Toolbox.newHashSet(channels));
            getRedisListener().subscribe(channels);
        } catch (JedisConnectionException ex) {
        }
    }
    
    public void unsubscribe(String... channels) {
        try {
            getChannels().removeAll(Toolbox.newHashSet(channels));
            getRedisListener().unsubscribe(channels);
        } catch (JedisConnectionException ex) {
        }
    }
    
    public Set<String> getChannels() {
        return channels;
    }
    
    public RedisListener getRedisListener() {
        return redisListener;
    }
    
    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
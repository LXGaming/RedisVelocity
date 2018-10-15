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
import nz.co.lolnet.redisvelocity.plugin.configuration.category.RedisCategory;
import nz.co.lolnet.redisvelocity.plugin.listener.RedisListener;
import nz.co.lolnet.redisvelocity.plugin.util.Toolbox;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.Set;

public class RedisService extends AbstractService {
    
    private final Set<String> channels = Toolbox.newHashSet();
    private final RedisListener redisListener = new RedisListener();
    private JedisPool jedisPool;
    
    public boolean prepareService() {
        getChannels().add(Reference.ID + "-all");
        getChannels().add(Reference.ID + "-data");
        RedisVelocity.getInstance().getProxyChannel().ifPresent(getChannels()::add);
        
        RedisCategory redisCategory = VelocityPlugin.getInstance().getConfig().map(Config::getRedis).orElse(null);
        if (redisCategory == null) {
            return false;
        }
        
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        this.jedisPool = new JedisPool(jedisPoolConfig, redisCategory.getHost(), redisCategory.getPort(), Protocol.DEFAULT_TIMEOUT, redisCategory.getPassword());
        return true;
    }
    
    public void executeService() {
        try (Jedis jedis = getJedisPool().getResource()) {
            RedisVelocity.getInstance().getProxyChannel().ifPresent(jedis::clientSetname);
            jedis.subscribe(getRedisListener(), getChannels().toArray(new String[0]));
        }
    }
    
    public void publish(String channel, String message) {
        try (Jedis jedis = getJedisPool().getResource()) {
            jedis.publish(channel, message);
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
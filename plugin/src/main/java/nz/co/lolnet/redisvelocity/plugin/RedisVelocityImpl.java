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

package nz.co.lolnet.redisvelocity.plugin;

import nz.co.lolnet.redisvelocity.api.RedisVelocity;
import nz.co.lolnet.redisvelocity.api.util.Reference;
import nz.co.lolnet.redisvelocity.plugin.configuration.Config;
import nz.co.lolnet.redisvelocity.plugin.util.Toolbox;

import java.util.Optional;

public final class RedisVelocityImpl extends RedisVelocity {
    
    @Override
    public void sendMessage(String channel, String message) throws IllegalArgumentException {
        if (Toolbox.isBlank(channel)) {
            throw new IllegalArgumentException("Channel cannot be blank");
        }
        
        VelocityPlugin.getInstance().getRedisService().publish(channel, message);
    }
    
    @Override
    public void registerChannels(String... channels) throws IllegalArgumentException {
        for (String channel : channels) {
            if (Toolbox.isBlank(channel)) {
                throw new IllegalArgumentException("Channel cannot be blank");
            }
            
            if (Toolbox.containsIgnoreCase(channel, Reference.ID)) {
                throw new IllegalArgumentException("You cannot register a channel with the internal prefix");
            }
        }
        
        VelocityPlugin.getInstance().getRedisService().subscribe(channels);
    }
    
    @Override
    public void unregisterChannels(String... channels) throws IllegalArgumentException {
        for (String channel : channels) {
            if (Toolbox.isBlank(channel)) {
                throw new IllegalArgumentException("Channel cannot be blank");
            }
            
            if (Toolbox.containsIgnoreCase(channel, Reference.ID)) {
                throw new IllegalArgumentException("You cannot unregister a channel with the internal prefix");
            }
        }
        
        VelocityPlugin.getInstance().getRedisService().unsubscribe(channels);
    }
    
    @Override
    public Optional<String> getProxyId() {
        return VelocityPlugin.getInstance().getConfig().map(Config::getProxyId).filter(Toolbox::isNotBlank).map(String::toLowerCase);
    }
}
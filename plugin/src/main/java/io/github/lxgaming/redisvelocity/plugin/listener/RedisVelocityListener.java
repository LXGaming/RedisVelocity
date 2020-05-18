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

package io.github.lxgaming.redisvelocity.plugin.listener;

import com.velocitypowered.api.event.Subscribe;
import io.github.lxgaming.redisvelocity.api.RedisVelocity;
import io.github.lxgaming.redisvelocity.api.event.RedisMessageEvent;
import io.github.lxgaming.redisvelocity.plugin.VelocityPlugin;
import io.github.lxgaming.redisvelocity.plugin.util.RedisVelocityCommandSource;
import io.github.lxgaming.redisvelocity.plugin.util.Toolbox;

public class RedisVelocityListener {
    
    @Subscribe
    public void onRedisMessage(RedisMessageEvent event) {
        if (event.getChannel().equals(RedisVelocity.ID + "-all") || RedisVelocity.getInstance().getProxyChannel().map(event.getChannel()::equals).orElse(false)) {
            String message = event.getMessage();
            if (Toolbox.isBlank(message) || message.equals("/")) {
                return;
            }
            
            if (message.startsWith("/")) {
                message = message.substring(1);
            }
            
            VelocityPlugin.getInstance().getLogger().info("Invoking command via RedisMessage: {}", message);
            VelocityPlugin.getInstance().getProxy().getCommandManager().execute(new RedisVelocityCommandSource(), message);
        }
    }
}
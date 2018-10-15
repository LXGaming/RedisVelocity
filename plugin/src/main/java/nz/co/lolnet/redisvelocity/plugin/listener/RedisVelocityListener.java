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

package nz.co.lolnet.redisvelocity.plugin.listener;

import com.velocitypowered.api.event.Subscribe;
import nz.co.lolnet.redisvelocity.api.RedisVelocity;
import nz.co.lolnet.redisvelocity.api.event.RedisMessageEvent;
import nz.co.lolnet.redisvelocity.api.util.Reference;
import nz.co.lolnet.redisvelocity.plugin.VelocityPlugin;
import nz.co.lolnet.redisvelocity.plugin.util.RedisVelocityCommandSource;
import nz.co.lolnet.redisvelocity.plugin.util.Toolbox;

public class RedisVelocityListener {
    
    @Subscribe
    public void onRedisMessage(RedisMessageEvent event) {
        if (event.getChannel().equals(Reference.ID + "-all") || RedisVelocity.getInstance().getProxyChannel().map(event.getChannel()::equals).orElse(false)) {
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
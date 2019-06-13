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

package io.github.lxgaming.redisvelocity.plugin.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import net.kyori.text.Component;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import io.github.lxgaming.redisvelocity.plugin.VelocityPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RedisVelocityCommandSource implements CommandSource {
    
    @SuppressWarnings("deprecation")
    @Override
    public void sendMessage(@NonNull Component component) {
        VelocityPlugin.getInstance().getLogger().info(LegacyComponentSerializer.INSTANCE.serialize(component));
    }
    
    @Override
    public @NonNull Tristate getPermissionValue(@NonNull String permission) {
        return Tristate.TRUE;
    }
    
    @Override
    public boolean hasPermission(@NonNull String permission) {
        return true;
    }
}
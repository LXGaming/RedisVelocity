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

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import nz.co.lolnet.redisvelocity.api.RedisVelocity;
import nz.co.lolnet.redisvelocity.api.util.Reference;
import nz.co.lolnet.redisvelocity.plugin.configuration.Config;
import nz.co.lolnet.redisvelocity.plugin.configuration.Configuration;
import nz.co.lolnet.redisvelocity.plugin.listener.RedisVelocityListener;
import nz.co.lolnet.redisvelocity.plugin.manager.ServiceManager;
import nz.co.lolnet.redisvelocity.plugin.service.RedisService;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = Reference.ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        description = Reference.DESCRIPTION,
        url = Reference.WEBSITE,
        authors = {Reference.AUTHORS}
)
public class VelocityPlugin {
    
    private static VelocityPlugin instance;
    
    @Inject
    ProxyServer proxy;
    
    @Inject
    private Logger logger;
    
    @Inject
    @DataDirectory
    private Path path;
    
    private Configuration configuration;
    private RedisService redisService;
    private RedisVelocity redisVelocity;
    
    @Subscribe(order = PostOrder.EARLY)
    public void onProxyInitialize(ProxyInitializeEvent event) {
        instance = this;
        this.configuration = new Configuration();
        this.redisService = new RedisService();
        this.redisVelocity = new RedisVelocityImpl();
        
        getConfiguration().loadConfiguration();
        ServiceManager.schedule(getRedisService());
        getProxy().getEventManager().register(getInstance(), new RedisVelocityListener());
        getConfiguration().saveConfiguration();
        getLogger().info("{} v{} has initialized", Reference.NAME, Reference.VERSION);
    }
    
    @Subscribe(order = PostOrder.LATE)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        getRedisService().shutdown();
        getLogger().info("{} v{} has shutdown", Reference.NAME, Reference.VERSION);
    }
    
    public static VelocityPlugin getInstance() {
        return instance;
    }
    
    public ProxyServer getProxy() {
        return proxy;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Path getPath() {
        return path;
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Optional<Config> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisService getRedisService() {
        return redisService;
    }
    
    public RedisVelocity getRedisVelocity() {
        return redisVelocity;
    }
}
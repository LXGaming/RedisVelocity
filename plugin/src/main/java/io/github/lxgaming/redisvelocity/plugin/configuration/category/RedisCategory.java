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

package io.github.lxgaming.redisvelocity.plugin.configuration.category;

public class RedisCategory {
    
    private String host = "127.0.0.1";
    private int port = 6379;
    private String password = "";
    private boolean autoReconnect = true;
    private int maximumReconnectDelay = 300;
    private int maximumPoolSize = 10;
    private int maximumIdle = 5;
    private int minimumIdle = 1;
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getPassword() {
        return password;
    }
    
    public boolean isAutoReconnect() {
        return autoReconnect;
    }
    
    public int getMaximumReconnectDelay() {
        return maximumReconnectDelay;
    }
    
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }
    
    public int getMaximumIdle() {
        return maximumIdle;
    }
    
    public int getMinimumIdle() {
        return minimumIdle;
    }
}
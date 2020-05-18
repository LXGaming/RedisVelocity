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

package io.github.lxgaming.redisvelocity.plugin.service;

import io.github.lxgaming.redisvelocity.plugin.VelocityPlugin;
import io.github.lxgaming.redisvelocity.plugin.util.Toolbox;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Service implements Runnable {
    
    private long delay;
    private long interval;
    private ScheduledFuture<?> scheduledFuture;
    
    @Override
    public final void run() {
        try {
            execute();
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error while executing {}", Toolbox.getClassSimpleName(getClass()), ex);
            getScheduledFuture().cancel(false);
        }
    }
    
    public abstract boolean prepare();
    
    public abstract void execute() throws Exception;
    
    public final long getDelay() {
        return delay;
    }
    
    protected final void delay(long delay, TimeUnit unit) {
        this.delay = unit.toMillis(delay);
    }
    
    public final long getInterval() {
        return interval;
    }
    
    protected final void interval(long interval, TimeUnit unit) {
        this.interval = unit.toMillis(interval);
    }
    
    public final ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }
    
    public final void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}
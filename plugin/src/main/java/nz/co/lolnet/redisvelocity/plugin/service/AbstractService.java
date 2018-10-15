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

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import nz.co.lolnet.redisvelocity.plugin.VelocityPlugin;

public abstract class AbstractService implements Runnable {
    
    private long delay;
    private long interval;
    private ScheduledTask scheduledTask;
    
    @Override
    public final void run() {
        try {
            executeService();
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error processing {}::run", getClass().getSimpleName(), ex);
            getScheduledTask().cancel();
        }
    }
    
    public abstract boolean prepareService();
    
    public abstract void executeService();
    
    public boolean isRunning() {
        return getScheduledTask().status() == TaskStatus.SCHEDULED;
    }
    
    public final long getDelay() {
        return delay;
    }
    
    protected final void setDelay(long delay) {
        this.delay = delay;
    }
    
    public final long getInterval() {
        return interval;
    }
    
    protected final void setInterval(long interval) {
        this.interval = interval;
    }
    
    public ScheduledTask getScheduledTask() {
        return scheduledTask;
    }
    
    public void setScheduledTask(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }
}
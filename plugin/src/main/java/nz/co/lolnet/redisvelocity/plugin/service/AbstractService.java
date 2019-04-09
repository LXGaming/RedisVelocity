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

import nz.co.lolnet.redisvelocity.plugin.VelocityPlugin;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractService implements Runnable {
    
    private long delay;
    private long interval;
    private ScheduledFuture scheduledFuture;
    
    @Override
    public final void run() {
        try {
            execute();
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error while executing {}", getClass().getSimpleName(), ex);
            getScheduledFuture().cancel(false);
        }
    }
    
    public abstract boolean prepare();
    
    public abstract void execute() throws Exception;
    
    public boolean isRunning() {
        return getScheduledFuture() != null && (!getScheduledFuture().isDone() || getScheduledFuture().getDelay(TimeUnit.MILLISECONDS) > 0L);
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
    
    public ScheduledFuture getScheduledFuture() {
        return scheduledFuture;
    }
    
    public void setScheduledFuture(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}
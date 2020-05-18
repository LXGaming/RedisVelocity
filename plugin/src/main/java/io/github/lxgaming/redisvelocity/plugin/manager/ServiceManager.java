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

package io.github.lxgaming.redisvelocity.plugin.manager;

import io.github.lxgaming.redisvelocity.plugin.VelocityPlugin;
import io.github.lxgaming.redisvelocity.plugin.service.Service;
import io.github.lxgaming.redisvelocity.plugin.util.Toolbox;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ServiceManager {
    
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Toolbox.newScheduledThreadPool(1, 1, 60000L, TimeUnit.MILLISECONDS);
    
    public static void schedule(Service service) {
        try {
            if (!service.prepare()) {
                VelocityPlugin.getInstance().getLogger().warn("{} failed to prepare", Toolbox.getClassSimpleName(service.getClass()));
                return;
            }
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error while preparing {}", Toolbox.getClassSimpleName(service.getClass()), ex);
            return;
        }
        
        ScheduledFuture<?> scheduledFuture = schedule(service, service.getDelay(), service.getInterval());
        service.setScheduledFuture(scheduledFuture);
    }
    
    public static ScheduledFuture<?> schedule(Runnable runnable) {
        return schedule(runnable, 0L, 0L);
    }
    
    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, long interval) {
        return schedule(runnable, delay, interval, TimeUnit.MILLISECONDS);
    }
    
    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, long interval, TimeUnit unit) {
        try {
            if (interval <= 0L) {
                return SCHEDULED_EXECUTOR_SERVICE.schedule(runnable, Math.max(delay, 0L), unit);
            }
            
            return SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(runnable, Math.max(delay, 0L), Math.max(interval, 0L), unit);
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error while scheduling service", ex);
            return null;
        }
    }
    
    public static void shutdown() {
        try {
            SCHEDULED_EXECUTOR_SERVICE.shutdown();
            if (!SCHEDULED_EXECUTOR_SERVICE.awaitTermination(5000L, TimeUnit.MILLISECONDS)) {
                throw new InterruptedException();
            }
            
            VelocityPlugin.getInstance().getLogger().info("Successfully terminated service, continuing with shutdown process...");
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Failed to terminate service, continuing with shutdown process...");
        }
    }
}
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

package nz.co.lolnet.redisvelocity.plugin.manager;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import nz.co.lolnet.redisvelocity.plugin.VelocityPlugin;
import nz.co.lolnet.redisvelocity.plugin.service.AbstractService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class ServiceManager {
    
    public static void schedule(AbstractService abstractService) {
        try {
            if (!abstractService.prepareService()) {
                throw new IllegalStateException("Service preparation failed");
            }
            
            schedule(abstractService, abstractService.getDelay(), abstractService.getInterval()).ifPresent(abstractService::setScheduledTask);
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error processing {}::schedule", "ServiceManager", ex);
        }
    }
    
    public static Optional<ScheduledTask> schedule(Runnable runnable, long delay, long interval) {
        try {
            Scheduler.TaskBuilder taskBuilder = VelocityPlugin.getInstance().getProxy().getScheduler().buildTask(VelocityPlugin.getInstance(), runnable);
            if (interval <= 0L) {
                return Optional.of(taskBuilder.delay(Math.max(delay, 0L), TimeUnit.MILLISECONDS).schedule());
            }
            
            return Optional.of(taskBuilder.delay(Math.max(delay, 0L), TimeUnit.MILLISECONDS).repeat(Math.max(interval, 0L), TimeUnit.MILLISECONDS).schedule());
        } catch (Exception ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error processing {}::schedule", "ServiceManager", ex);
            return Optional.empty();
        }
    }
}
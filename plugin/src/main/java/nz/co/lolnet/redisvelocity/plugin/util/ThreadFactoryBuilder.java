/*
 * Copyright 2019 lolnet.co.nz
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

package nz.co.lolnet.redisvelocity.plugin.util;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadFactoryBuilder {
    
    private final AtomicLong atomicLong = new AtomicLong(0);
    private ThreadFactory backingThreadFactory = Executors.defaultThreadFactory();
    private Boolean daemon = null;
    private String namingPattern = null;
    private Integer priority = null;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
    
    public ThreadFactory build() {
        return build(this);
    }
    
    private AtomicLong getAtomicLong() {
        return atomicLong;
    }
    
    private ThreadFactory getBackingThreadFactory() {
        return backingThreadFactory;
    }
    
    public void backingThreadFactory(ThreadFactory backingThreadFactory) {
        if (backingThreadFactory == null) {
            throw new NullPointerException();
        }
        
        this.backingThreadFactory = backingThreadFactory;
    }
    
    private Boolean getDaemon() {
        return daemon;
    }
    
    public ThreadFactoryBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }
    
    private String getNamingPattern() {
        return namingPattern;
    }
    
    public ThreadFactoryBuilder namingPattern(String namingPattern) {
        format(namingPattern, 0);
        this.namingPattern = namingPattern;
        return this;
    }
    
    private Integer getPriority() {
        return priority;
    }
    
    public ThreadFactoryBuilder priority(int priority) {
        if (priority > Thread.MAX_PRIORITY || priority < Thread.MIN_PRIORITY) {
            throw new IndexOutOfBoundsException();
        }
        
        this.priority = priority;
        return this;
    }
    
    private Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }
    
    public ThreadFactoryBuilder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }
    
    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        return (Runnable runnable) -> {
            Thread thread = builder.backingThreadFactory.newThread(runnable);
            if (builder.getDaemon() != null) {
                thread.setDaemon(builder.getDaemon());
            }
            
            if (builder.getNamingPattern() != null) {
                thread.setName(format(builder.getNamingPattern(), builder.getAtomicLong().getAndIncrement()));
            }
            
            if (builder.getPriority() != null) {
                thread.setPriority(builder.getPriority());
            }
            
            if (builder.getUncaughtExceptionHandler() != null) {
                thread.setUncaughtExceptionHandler(builder.getUncaughtExceptionHandler());
            }
            
            return thread;
        };
    }
    
    private static String format(String format, Object... args) {
        return String.format(Locale.ROOT, format, args);
    }
}
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

package nz.co.lolnet.redisvelocity.plugin.configuration;

import com.google.gson.JsonParseException;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import nz.co.lolnet.redisvelocity.plugin.VelocityPlugin;
import nz.co.lolnet.redisvelocity.plugin.util.Toolbox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Configuration {
    
    private final Toml toml = new Toml();
    private final TomlWriter tomlWriter = new TomlWriter();
    private Config config = new Config();
    
    public void loadConfiguration() {
        this.config = (Config) loadObject(getConfig(), "config.toml");
        VelocityPlugin.getInstance().getLogger().info("Loaded configuration files.");
    }
    
    public void saveConfiguration() {
        saveObject(getConfig(), "config.toml");
        VelocityPlugin.getInstance().getLogger().info("Saved configuration files.");
    }
    
    private Object loadObject(Object object, String name) {
        try {
            if (object == null || Toolbox.isBlank(name)) {
                throw new IllegalArgumentException("Supplied arguments are null!");
            }
            
            File file = VelocityPlugin.getInstance().getPath().resolve(name).toFile();
            if (!file.exists() && !saveObject(object, name)) {
                return object;
            }
            
            String string = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            if (Toolbox.isBlank(string)) {
                throw new IOException("File is blank!");
            }
            
            Object data = getToml().read(string).to(object.getClass());
            if (data == null) {
                throw new JsonParseException("Failed to parse File!");
            }
            
            return data;
        } catch (IOException | OutOfMemoryError | RuntimeException ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error processing {}::loadObject", getClass().getSimpleName(), ex);
            return object;
        }
    }
    
    private boolean saveObject(Object object, String name) {
        try {
            if (object == null || Toolbox.isBlank(name)) {
                throw new IllegalArgumentException("Supplied arguments are null!");
            }
            
            File file = VelocityPlugin.getInstance().getPath().resolve(name).toFile();
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists() && parentFile.mkdirs()) {
                VelocityPlugin.getInstance().getLogger().info("Successfully created directory {}.", parentFile.getName());
            }
            
            if (!file.exists() && file.createNewFile()) {
                VelocityPlugin.getInstance().getLogger().info("Successfully created file {}.", file.getName());
            }
            
            getTomlWriter().write(object, file);
            return true;
        } catch (IOException | OutOfMemoryError | RuntimeException ex) {
            VelocityPlugin.getInstance().getLogger().error("Encountered an error processing {}::saveObject", getClass().getSimpleName(), ex);
            return false;
        }
    }
    
    private Toml getToml() {
        return toml;
    }
    
    private TomlWriter getTomlWriter() {
        return tomlWriter;
    }
    
    public Config getConfig() {
        return config;
    }
}
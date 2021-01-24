package de.hglabor.plugins.kitapi.config;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class Config {
    private final static Config instance = new Config();

    private static File kitFile;
    private static YamlConfiguration kitConfiguration;

    private Config() {
    }

    public static Config getInstance() {
        return instance;
    }

    public void register(File pluginFolder) {
        try {
            kitFile = new File(pluginFolder, "kitConfig.yml");
            if (!kitFile.exists()) {
                kitFile.createNewFile();
            }
            kitConfiguration = YamlConfiguration.loadConfiguration(kitFile);
            kitConfiguration.addDefault("kit.amount", 1);
            kitConfiguration.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadKit(AbstractKit kit) {
        try {
            kitConfiguration.addDefault("kit" + "." + kit.getName() + "." + "enabled", kit.isEnabled());
            kitConfiguration.addDefault("kit" + "." + kit.getName() + "." + "cooldown", kit.getCooldown());
            kitConfiguration.options().copyDefaults(true);
            kitConfiguration.set("kit" + "." + kit.getName() + "." + "cooldown", kit.getCooldown());
            kitConfiguration.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInteger(String key) {
        return kitConfiguration.getInt(key);
    }

    public String getString(String key) {
        return kitConfiguration.getString(key);
    }

    public boolean getBoolean(String key) {
        return kitConfiguration.getBoolean(key);
    }

    public int getKitAmount() {
        return kitConfiguration.getInt("kit.amount");
    }
}

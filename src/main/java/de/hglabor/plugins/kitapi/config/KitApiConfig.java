package de.hglabor.plugins.kitapi.config;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.utils.noriskutils.ReflectionUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class KitApiConfig {
    private final static KitApiConfig instance = new KitApiConfig();

    private static File kitFile;
    private static YamlConfiguration kitConfiguration;

    private KitApiConfig() {
    }

    public static KitApiConfig getInstance() {
        return instance;
    }

    private static String key(AbstractKit kit, String fieldName) {
        return "kit" + "." + kit.getName() + "." + "settings" + "." + fieldName;
    }

    public void load(AbstractKit kit) {
        for (Field field : kit.getClass().getDeclaredFields()) {
            String name = field.getName();
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(IntArg.class)) {
                    ReflectionUtils.set(field, kit, kitConfiguration.get(key(kit, name)));
                } else if (annotation.annotationType().equals(FloatArg.class)) {
                    ReflectionUtils.set(field, kit, NumberConversions.toFloat(kitConfiguration.get(key(kit, name))));
                }
            }
        }
        kit.setEnabled(getBoolean("kit" + "." + kit.getName() + "." + "enabled"));
        kit.setCooldown(getInteger("kit" + "." + kit.getName() + "." + "cooldown"));
        kit.setUsable(getBoolean("kit" + "." + kit.getName() + "." + "usable"));
        loadKitEvents(kit);
    }

    //HAHAHAHAH
    @SuppressWarnings("unchecked")
    private void loadKitEvents(AbstractKit kit) {
        for (Method method : kit.getClass().getDeclaredMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (!annotation.annotationType().equals(KitEvent.class)) {
                    continue;
                }
                Class<?> clazz = ((KitEvent) annotation).clazz();
                if (!clazz.equals(Void.class)) {
                    kit.getKitEvents().add((Class<? extends Event>) clazz);
                } else {
                    for (Class<?> param : method.getParameterTypes()) {
                        if (Event.class.isAssignableFrom(param)) {
                            kit.getKitEvents().add((Class<? extends Event>) param);
                        }
                    }
                }
            }
        }
    }

    private void registerAnnotations(AbstractKit kit) {
        for (Field field : kit.getClass().getDeclaredFields()) {
            String name = field.getName();
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(IntArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ReflectionUtils.getInt(field, kit));
                } else if (annotation.annotationType().equals(FloatArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ReflectionUtils.getFloat(field, kit));
                }
            }
        }
    }

    public void register(File pluginFolder) {
        try {
            kitFile = new File(pluginFolder, "kitConfig.yml");
            if (!kitFile.exists()) {
                kitFile.createNewFile();
            }
            kitConfiguration = YamlConfiguration.loadConfiguration(kitFile);
            kitConfiguration.addDefault("kit.amount", 1);
            kitConfiguration.addDefault("debug", false);
            kitConfiguration.options().copyDefaults(true);
            kitConfiguration.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(AbstractKit kit) {
        try {
            kitConfiguration.addDefault("kit" + "." + kit.getName() + "." + "enabled", kit.isEnabled());
            kitConfiguration.addDefault("kit" + "." + kit.getName() + "." + "cooldown", kit.getCooldown());
            kitConfiguration.addDefault("kit" + "." + kit.getName() + "." + "usable", kit.isUsable());
            registerAnnotations(kit);
            kitConfiguration.options().copyDefaults(true);
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

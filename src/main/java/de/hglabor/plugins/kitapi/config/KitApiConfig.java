package de.hglabor.plugins.kitapi.config;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.*;
import de.hglabor.plugins.kitapi.util.Utils;
import de.hglabor.utils.noriskutils.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
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

    private String key(AbstractKit kit, String fieldName) {
        return "kit" + "." + kit.getName() + "." + "settings" + "." + fieldName;
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
            kitConfiguration.addDefault("kitSelectorName", "Kit Selector");
            kitConfiguration.options().copyDefaults(true);
            kitConfiguration.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(AbstractKit kit) {
        try {
            registerAnnotations(kit);
            kitConfiguration.options().copyDefaults(true);
            kitConfiguration.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAnnotations(AbstractKit kit) {
        for (Field field : Utils.getAllFields(kit)) {
            String name = field.getName();
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().equals(IntArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ReflectionUtils.getInt(field, kit));
                } else if (annotation.annotationType().equals(FloatArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ReflectionUtils.getFloat(field, kit));
                } else if (annotation.annotationType().equals(DoubleArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ReflectionUtils.getDouble(field, kit));
                } else if (annotation.annotationType().equals(LongArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ReflectionUtils.getLong(field, kit));
                } else if (annotation.annotationType().equals(MaterialArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ((Material) ReflectionUtils.get(field, kit)).name());
                } else if (annotation.annotationType().equals(PotionTypeArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ((PotionType) ReflectionUtils.get(field, kit)).name());
                } else if (annotation.annotationType().equals(EntityArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ((EntityType) ReflectionUtils.get(field, kit)).name());
                } else if (annotation.annotationType().equals(PotionEffectArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ((PotionEffectType) ReflectionUtils.get(field, kit)).getName());
                } else if (annotation.annotationType().equals(SoundArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ((Sound) ReflectionUtils.get(field, kit)).name());
                } else if (annotation.annotationType().equals(BoolArg.class)) {
                    kitConfiguration.addDefault(key(kit, name), ReflectionUtils.getBool(field, kit));
                }
            }
        }
    }

    public void load(AbstractKit kit) {
        for (Field field : Utils.getAllFields(kit)) {
            String name = field.getName();
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(IntArg.class)) {
                    ReflectionUtils.set(field, kit, NumberConversions.toInt(kitConfiguration.get(key(kit, name))));
                } else if (annotation.annotationType().equals(FloatArg.class)) {
                    ReflectionUtils.set(field, kit, NumberConversions.toFloat(kitConfiguration.get(key(kit, name))));
                } else if (annotation.annotationType().equals(DoubleArg.class)) {
                    ReflectionUtils.set(field, kit, NumberConversions.toDouble(kitConfiguration.get(key(kit, name))));
                } else if (annotation.annotationType().equals(LongArg.class)) {
                    ReflectionUtils.set(field, kit, NumberConversions.toLong(kitConfiguration.get(key(kit, name))));
                } else if (annotation.annotationType().equals(MaterialArg.class)) {
                    ReflectionUtils.set(field, kit, Material.valueOf(kitConfiguration.getString(key(kit, name))));
                } else if (annotation.annotationType().equals(PotionTypeArg.class)) {
                    ReflectionUtils.set(field, kit, PotionType.valueOf(kitConfiguration.getString(key(kit, name))));
                } else if (annotation.annotationType().equals(EntityArg.class)) {
                    ReflectionUtils.set(field, kit, EntityType.valueOf(kitConfiguration.getString(key(kit, name))));
                } else if (annotation.annotationType().equals(PotionEffectArg.class)) {
                    ReflectionUtils.set(field, kit, PotionEffectType.getByName(kitConfiguration.getString(key(kit, name))));
                } else if (annotation.annotationType().equals(SoundArg.class)) {
                    ReflectionUtils.set(field, kit, Sound.valueOf(kitConfiguration.getString(key(kit, name))));
                } else if (annotation.annotationType().equals(BoolArg.class)) {
                    ReflectionUtils.set(field, kit, Boolean.valueOf(kitConfiguration.getString(key(kit, name))));
                }
            }
        }
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

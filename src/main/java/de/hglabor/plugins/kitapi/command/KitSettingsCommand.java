package de.hglabor.plugins.kitapi.command;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.settings.*;
import de.hglabor.plugins.kitapi.util.ReflectionUtils;
import de.hglabor.plugins.kitapi.util.Utils;
import de.hglabor.utils.noriskutils.PermissionUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.NumberConversions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class KitSettingsCommand {
    private static final String PERMISSION = "hglabor.kitapi.changeKitSettings";

    public KitSettingsCommand(boolean checkForHigherRank) {
        new CommandAPICommand("kitsettings")
                .withArguments(kitArgument())
                .withArguments(kitSettings())
                .withArguments(settingValues())
                .withPermission(PERMISSION)
                .executesPlayer((player, objects) -> {
                    if (checkForHigherRank) {
                        if (PermissionUtils.checkForHigherRank(player)) {
                            player.sendMessage(ChatColor.RED + "Player with higher rank is online.");
                            return;
                        }
                    }
                    AbstractKit kit = (AbstractKit) objects[0];
                    String fieldName = (String) objects[1];
                    String value = (String) objects[2];
                    String replacement = "";
                    Field field = ReflectionUtils.getField(kit.getClass(), fieldName);
                    if (field == null) {
                        player.sendMessage(ChatColor.RED + "Incorrect field");
                        return;
                    }
                    if (ReflectionUtils.isFloat(field)) {
                        FloatArg annotation = getAnnotation(field.getDeclaredAnnotations(), FloatArg.class);
                        float max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toFloat(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isDouble(field)) {
                        DoubleArg annotation = getAnnotation(field.getDeclaredAnnotations(), DoubleArg.class);
                        double max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toDouble(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isInt(field)) {
                        IntArg annotation = getAnnotation(field.getDeclaredAnnotations(), IntArg.class);
                        int max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toInt(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isLong(field)) {
                        LongArg annotation = getAnnotation(field.getDeclaredAnnotations(), LongArg.class);
                        long max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toLong(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isBool(field)) {
                        boolean bool = Boolean.parseBoolean(value);
                        replacement = value;
                        KitApi.getInstance().enableKit(kit, bool);
                    } else if (ReflectionUtils.isMaterial(field)) {
                        Material material = Material.valueOf(value);
                        replacement = material.name();
                        ReflectionUtils.set(field, kit, material);
                    } else if (ReflectionUtils.isPotionType(field)) {
                        PotionType potionType = PotionType.valueOf(value);
                        replacement = potionType.name();
                        ReflectionUtils.set(field, kit, potionType);
                    } else if (ReflectionUtils.isEntityType(field)) {
                        EntityType entityType = EntityType.valueOf(value);
                        replacement = entityType.name();
                        ReflectionUtils.set(field, kit, entityType);
                    } else if (ReflectionUtils.isPotionEffect(field)) {
                        PotionEffectType potionEffectType = PotionEffectType.getByName(value);
                        if (potionEffectType != null) {
                            replacement = potionEffectType.getName();
                            ReflectionUtils.set(field, kit, potionEffectType);
                        }
                    } else if (ReflectionUtils.isSound(field)) {
                        Sound sound = Sound.valueOf(value);
                        replacement = sound.name();
                        ReflectionUtils.set(field, kit, sound);
                    }
                    sendMessage(player, kit, fieldName, replacement);
                })
                .register();
    }

    private void sendMessage(Player player, AbstractKit kit, String fieldName, String value) {
        String message = ChatColor.AQUA.toString() + ChatColor.BOLD + kit.getName() +
                ChatColor.RESET + " -> " + fieldName +
                " -> " + ChatColor.GOLD + value;
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (otherPlayer == player) continue;
            if (otherPlayer.hasPermission(PERMISSION)) {
                otherPlayer.sendMessage(player.getName() + " changed " + message);
            }
        }
        player.sendMessage(message);
    }

    private Argument kitArgument() {
        return new CustomArgument<>("kit", (input) -> {
            Optional<AbstractKit> kitInput = KitApi.getInstance().getAllKits().stream().filter(kit -> kit.getName().equalsIgnoreCase(input)).findFirst();
            if (kitInput.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown kit: ").appendArgInput());
            } else {
                return kitInput.get();
            }
        }).overrideSuggestions(sender -> KitApi.getInstance().getAllKits().stream().map(AbstractKit::getName).toArray(String[]::new));
    }

    private Argument kitSettings() {
        return new CustomArgument<>("settings", (input) -> input).overrideSuggestions((commandSender, objects) -> {
            AbstractKit kit = (AbstractKit) objects[0];
            String[] strings = getFieldNames(kit).toArray(String[]::new);
            return strings.length == 0 ? new String[]{"NO_SETTINGS"} : strings;
        });
    }

    private Argument settingValues() {
        return new CustomArgument<>("value", (input) -> input).overrideSuggestions((commandSender, objects) -> {
            AbstractKit kit = (AbstractKit) objects[0];
            String fieldName = (String) objects[1];
            Field field = ReflectionUtils.getField(kit.getClass(), fieldName);
            if (field == null) return new String[]{"null"};
            String currentValue = "Current Value: " + ReflectionUtils.get(field, kit).toString();
            if (ReflectionUtils.isFloat(field)) {
                FloatArg annotation = getAnnotation(field.getDeclaredAnnotations(), FloatArg.class);
                if (annotation != null) {
                    return new String[]{currentValue, "Float value from " + annotation.min() + " to " + annotation.max()};
                }
            } else if (ReflectionUtils.isDouble(field)) {
                DoubleArg annotation = getAnnotation(field.getDeclaredAnnotations(), DoubleArg.class);
                if (annotation != null)
                    return new String[]{currentValue, "Double value from " + annotation.min() + " to " + annotation.max()};
            } else if (ReflectionUtils.isInt(field)) {
                IntArg annotation = getAnnotation(field.getDeclaredAnnotations(), IntArg.class);
                if (annotation != null)
                    return new String[]{currentValue, "Double value from " + annotation.min() + " to " + annotation.max()};
            } else if (ReflectionUtils.isLong(field)) {
                LongArg annotation = getAnnotation(field.getDeclaredAnnotations(), LongArg.class);
                if (annotation != null)
                    return new String[]{currentValue, "Long value from " + annotation.min() + " to " + annotation.max()};
            } else if (ReflectionUtils.isBool(field)) {
                BoolArg annotation = getAnnotation(field.getDeclaredAnnotations(), BoolArg.class);
                if (annotation != null)
                    return new String[]{currentValue, "Boolean: true or false"};
            } else if (ReflectionUtils.isMaterial(field)) {
                List<String> list = new ArrayList<>();
                list.add(currentValue);
                Arrays.stream(Material.values()).map(Enum::name).forEach(list::add);
                return list.toArray(new String[0]);
            } else if (ReflectionUtils.isMaterial(field)) {
                List<String> list = new ArrayList<>();
                list.add(currentValue);
                Arrays.stream(Material.values()).map(Enum::name).forEach(list::add);
                return list.toArray(new String[0]);
            } else if (ReflectionUtils.isPotionType(field)) {
                List<String> list = new ArrayList<>();
                list.add(currentValue);
                Arrays.stream(PotionType.values()).map(Enum::name).forEach(list::add);
                return list.toArray(new String[0]);
            } else if (ReflectionUtils.isEntityType(field)) {
                List<String> list = new ArrayList<>();
                list.add(currentValue);
                Arrays.stream(EntityType.values()).map(Enum::name).forEach(list::add);
                return list.toArray(new String[0]);
            } else if (ReflectionUtils.isPotionEffect(field)) {
                List<String> list = new ArrayList<>();
                list.add(currentValue);
                Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).forEach(list::add);
                return list.toArray(new String[0]);
            } else if (ReflectionUtils.isSound(field)) {
                List<String> list = new ArrayList<>();
                list.add(currentValue);
                Arrays.stream(Sound.values()).map(Enum::name).forEach(list::add);
                return list.toArray(new String[0]);
            }
            return new String[]{"error haha"};
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == clazz) {
                return (T) annotation;
            }
        }
        return null;
    }

    private List<String> getFieldNames(AbstractKit kit) {
        List<Class<? extends Annotation>> kitAnnotations = List.of(
                DoubleArg.class, FloatArg.class, IntArg.class, BoolArg.class,
                LongArg.class, MaterialArg.class, StringArg.class, PotionTypeArg.class,
                EntityArg.class);
        List<String> names = new ArrayList<>();
        for (Field field : Utils.getAllFields(kit)) {
            for (Annotation annotation : field.getAnnotations()) {
                if (kitAnnotations.contains(annotation.annotationType())) {
                    names.add(field.getName());
                }
            }
        }
        return names;
    }
}

package de.hglabor.plugins.kitapi.command;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.settings.*;
import de.hglabor.utils.noriskutils.ReflectionUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class KitSettingsCommand {
    public KitSettingsCommand() {
        new CommandAPICommand("kitsettings")
                .withArguments(kitArgument())
                .withArguments(kitSettings())
                .withArguments(settingValues())
                .executesPlayer((player, objects) -> {
                    AbstractKit kit = (AbstractKit) objects[0];
                    String fieldName = (String) objects[1];
                    String value = (String) objects[2];
                    String replacement = "";
                    Field field = ReflectionUtils.getField(kit.getClass(), fieldName);
                    if (ReflectionUtils.isFloat(field)) {
                        FloatArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                        float max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toFloat(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isDouble(field)) {
                        DoubleArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                        double max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toDouble(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isInt(field)) {
                        IntArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                        int max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toInt(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isLong(field)) {
                        LongArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                        long max = Math.max(annotation.min(), Math.min(annotation.max(), NumberConversions.toLong(value)));
                        replacement = String.valueOf(max);
                        ReflectionUtils.set(field, kit, max);
                    } else if (ReflectionUtils.isMaterial(field)) {
                        Material material = Material.valueOf(value);
                        replacement = material.name();
                        ReflectionUtils.set(field, kit, material);
                    }
                    sendMessage(player, kit, fieldName, replacement);
                })
                .register();
    }

    private void sendMessage(Player player, AbstractKit kit, String fieldName, String value) {
        player.sendMessage(kit.getName() + " -> " + fieldName + " -> " + value);
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
            return getAnnotatedFieldNames(kit).toArray(String[]::new);
        });
    }

    private Argument settingValues() {
        return new CustomArgument<>("value", (input) -> input).overrideSuggestions((commandSender, objects) -> {
            AbstractKit kit = (AbstractKit) objects[0];
            String fieldName = (String) objects[1];
            Field field = ReflectionUtils.getField(kit.getClass(), fieldName);
            if (ReflectionUtils.isFloat(field)) {
                FloatArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                return new String[]{"Float value from " + annotation.min() + " to " + annotation.max()};
            } else if (ReflectionUtils.isDouble(field)) {
                DoubleArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                return new String[]{"Double value from " + annotation.min() + " to " + annotation.max()};
            } else if (ReflectionUtils.isInt(field)) {
                IntArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                return new String[]{"Double value from " + annotation.min() + " to " + annotation.max()};
            } else if (ReflectionUtils.isLong(field)) {
                LongArg annotation = getAnnotation(field.getDeclaredAnnotations(), kit.getClass());
                return new String[]{"Long value from " + annotation.min() + " to " + annotation.max()};
            } else if (ReflectionUtils.isMaterial(field)) {
                return Arrays.stream(Material.values()).map(Material::name).toArray(String[]::new);
            }
            return new String[]{"error haha"};
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<? extends AbstractKit> clazz) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == clazz) {
                return (T) annotation;
            }
        }
        return null;
    }

    private List<String> getAnnotatedFieldNames(AbstractKit kit) {
        List<Class<? extends Annotation>> kitAnnotations = List.of(
                DoubleArg.class, FloatArg.class, IntArg.class,
                LongArg.class, MaterialArg.class, StringArg.class);
        List<String> names = new ArrayList<>();
        for (Field field : kit.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (kitAnnotations.contains(annotation.getClass())) {
                    names.add(field.getName());
                }
            }
        }
        return names;
    }
}

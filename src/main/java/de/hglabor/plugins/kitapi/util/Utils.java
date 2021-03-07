package de.hglabor.plugins.kitapi.util;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import org.bukkit.block.Block;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Utils {
    private Utils() {
    }

    public static boolean isUnbreakableLaborBlock(Block b) {
        return b.hasMetadata(KitMetaData.GLADIATOR_BLOCK.getKey()) || b.hasMetadata(KitMetaData.FEAST_BLOCK.getKey()) || b.hasMetadata(KitMetaData.UNBREAKABLE_BLOCK.getKey());
    }

    public static List<Field> getAllFields(AbstractKit kit) {
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, kit.getClass().getDeclaredFields());
        Collections.addAll(fields, kit.getClass().getSuperclass().getDeclaredFields());
        return fields;
    }
}

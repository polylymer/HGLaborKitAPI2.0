package de.hglabor.plugins.kitapi.util;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Utils {
    private Utils() {
    }

    public static boolean isUnbreakableLaborBlock(Block b) {
        return b.hasMetadata(KitMetaData.GLADIATOR_BLOCK.getKey()) || b.hasMetadata(KitMetaData.FEAST_BLOCK.getKey()) || b.hasMetadata(KitMetaData.UNBREAKABLE_BLOCK.getKey()) || b.getType() == Material.BEDROCK;
    }

    public static List<Field> getAllFields(AbstractKit kit) {
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, kit.getClass().getDeclaredFields());
        Collections.addAll(fields, kit.getClass().getSuperclass().getDeclaredFields());
        return fields;
    }

    //can be a feature in InventorBuilder itself
    public static int translateGuiScale(int toTranslate) {
        if(toTranslate < 10) {
            return 9;
        } else if(toTranslate > 10 && toTranslate <= 18) {
            return 18;
        } else if(toTranslate > 18 && toTranslate <= 27) {
            return 27;
        } else if(toTranslate > 27 && toTranslate <= 36) {
            return 36;
        } else if(toTranslate > 36 && toTranslate <= 45) {
            return 45;
        } else if(toTranslate > 45) {
            return 54;
        } else {
            return 9;
        }
    }

}

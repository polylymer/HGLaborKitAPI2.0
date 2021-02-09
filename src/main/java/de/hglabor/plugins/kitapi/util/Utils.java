package de.hglabor.plugins.kitapi.util;

import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import org.bukkit.block.Block;

public final class Utils {
    private Utils() {
    }

    public static boolean isUnbreakableLaborBlock(Block b) {
        return b.hasMetadata(KitMetaData.GLADIATOR_BLOCK.getKey()) || b.hasMetadata(KitMetaData.FEAST_BLOCK.getKey()) || b.hasMetadata(KitMetaData.UNBREAKABLE_BLOCK.getKey());
    }
}

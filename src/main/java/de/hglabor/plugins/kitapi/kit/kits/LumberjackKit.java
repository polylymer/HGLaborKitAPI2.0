package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Collections;

public class LumberjackKit extends AbstractKit {
    public final static LumberjackKit INSTANCE = new LumberjackKit();
    int count = 0;
    private LumberjackKit() {
        super("Lumberjack", Material.WOODEN_AXE);
        setMainKitItem(getDisplayMaterial(), true);
        addEvents(Collections.singletonList(BlockBreakEvent.class));
    }

    @Override
    public void onBlockBreakWithKitItem(BlockBreakEvent e) {
        String blockTypeName = e.getBlock().getType().name().toLowerCase();
        if (blockTypeName.contains("wood") || blockTypeName.contains("log")) {
            count = 0;
            breakSurroundingWood(e.getBlock());
        }
    }

    public void breakSurroundingWood(Block block) {
        String blockTypeName = block.getType().name().toLowerCase();
        if (blockTypeName.contains("wood") || blockTypeName.contains("log")) {
            block.breakNaturally();
            count++;
            if (count > 1500) return;
            BlockFace[] faces = { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace face : faces) {
                breakSurroundingWood(block.getRelative(face));
            }
        }
    }
}

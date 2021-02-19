package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class LumberjackKit extends AbstractKit {
    public final static LumberjackKit INSTANCE = new LumberjackKit();

    private LumberjackKit() {
        super("Lumberjack", Material.WOODEN_AXE);
        setMainKitItem(getDisplayMaterial(), true);
        addEvents(Collections.singletonList(BlockBreakEvent.class));
    }

    @Override
    public void onBlockBreakWithKitItem(BlockBreakEvent e) {
        String blockTypeName = e.getBlock().getType().name().toLowerCase();
        if (blockTypeName.contains("wood") || blockTypeName.contains("log")) {
            KitPlayer player = KitApi.getInstance().getPlayer(e.getPlayer());
            player.putKitAttribute(this, new AtomicInteger(), AtomicInteger.class);
            breakSurroundingWood(e.getBlock(), player);
        }
    }

    public void breakSurroundingWood(Block block, KitPlayer kitPlayer) {
        String blockTypeName = block.getType().name().toLowerCase();
        if (blockTypeName.contains("wood") || blockTypeName.contains("log")) {
            block.breakNaturally();
            AtomicInteger count = kitPlayer.getKitAttribute(this, AtomicInteger.class);
            if (count.getAndIncrement() > 1500) return;
            BlockFace[] faces = {BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace face : faces) {
                breakSurroundingWood(block.getRelative(face), kitPlayer);
            }
        }
    }
}

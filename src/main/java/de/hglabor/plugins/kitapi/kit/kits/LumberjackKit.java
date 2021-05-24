package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class LumberjackKit extends AbstractKit {
    public final static LumberjackKit INSTANCE = new LumberjackKit();
    private final String logCounterKey;
    @IntArg
    private final int maxLogToBreak;

    private LumberjackKit() {
        super("Lumberjack", Material.WOODEN_AXE);
        logCounterKey = this.getName() + "logCounter";
        maxLogToBreak = 300;
        setMainKitItem(getDisplayMaterial(), true);
    }

    @KitEvent
    @Override
    public void onBlockBreakWithKitItem(BlockBreakEvent e) {
        String blockTypeName = e.getBlock().getType().name().toLowerCase();
        if ((blockTypeName.contains("wood") || blockTypeName.contains("log") || blockTypeName.contains("stem") || blockTypeName.contains("hyphae")) && !blockTypeName.contains("mushroom")) {
            KitPlayer player = KitApi.getInstance().getPlayer(e.getPlayer());
            player.putKitAttribute(logCounterKey, new AtomicInteger());
            breakSurroundingWood(e.getBlock(), player);
        }
    }

    public void breakSurroundingWood(Block block, KitPlayer kitPlayer) {
        String blockTypeName = block.getType().name().toLowerCase();
        if (blockTypeName.contains("wood") || blockTypeName.contains("log") || blockTypeName.contains("stem") || blockTypeName.contains("hyphae")) {
            block.breakNaturally();
            AtomicInteger count = kitPlayer.getKitAttribute(logCounterKey);
            if (count.getAndIncrement() > maxLogToBreak) return;
            BlockFace[] faces = {BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace face : faces) {
                breakSurroundingWood(block.getRelative(face), kitPlayer);
            }
        }
    }
}

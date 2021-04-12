package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.items.KitItemBuilder;
import de.hglabor.plugins.kitapi.kit.settings.LongArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GardenerKit extends AbstractKit {
    public final static GardenerKit INSTANCE = new GardenerKit();
    private final String lastBlocksKey;
    private final List<Material> destructibleBlocks;

    @LongArg
    private final long delayInTicks;

    private GardenerKit() {
        super("Gardener", Material.SWEET_BERRIES);
        mainKitItem = new KitItemBuilder(Material.POPPY).build();
        lastBlocksKey = this.getName() + "lastBlocks";
        destructibleBlocks = Arrays.asList(
                Material.AIR,
                Material.GRASS,
                Material.TALL_GRASS,
                Material.SNOW,
                Material.DEAD_BUSH
        );
        delayInTicks = 5;
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        if (kitPlayer.getKitAttribute(lastBlocksKey) == null) {
            kitPlayer.putKitAttribute(lastBlocksKey, new LinkedList<>());
        }
    }

    @KitEvent
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
    }

    @KitEvent
    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {
        Block fromBlock = event.getFrom().getBlock();
        if (fromBlock.equals(event.getTo().getBlock())) {
            return;
        }

        Block[] lastBlocks = kitPlayer.getKitAttribute(lastBlocksKey);
        if (lastBlocks[2] == null || !fromBlock.equals(lastBlocks[2])) {
            Block old2 = lastBlocks[2];
            Block old1 = lastBlocks[1];
            Block old0 = lastBlocks[0];
            lastBlocks[2] = fromBlock;
            lastBlocks[1] = old2;
            lastBlocks[0] = old1;
            if (old0 != null && old0.getType().equals(Material.SWEET_BERRY_BUSH)) {
                old0.setType(Material.AIR);
            }
        }

        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
            for (int i = 0; i < lastBlocks.length; i++) {
                Block block = lastBlocks[i];
                if (block != null) {
                    if (!block.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS_BLOCK)) {
                        continue;
                    }
                    if (!destructibleBlocks.contains(block.getType())) {
                        continue;
                    }
                    block.setType(Material.SWEET_BERRY_BUSH);
                    Ageable blockData = (Ageable) block.getBlockData();
                    blockData.setAge(i);
                    block.setBlockData(blockData);
                }
            }
        }, delayInTicks);
    }

    @KitEvent
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT)
            event.setCancelled(true);
    }

}

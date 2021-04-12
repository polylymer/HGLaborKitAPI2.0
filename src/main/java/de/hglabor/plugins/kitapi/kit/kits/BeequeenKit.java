package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.items.KitItemBuilder;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class BeequeenKit extends AbstractKit implements Listener {
    public final static BeequeenKit INSTANCE = new BeequeenKit();

    @IntArg
    private final int honeyDurationInSeconds;
    @FloatArg(min = 0.0F)
    private final float cooldown;
    private final String honeyTrailKey;
    private final String isHoneyBlockKey;

    private BeequeenKit() {
        super("Beequeen", Material.HONEY_BLOCK);
        mainKitItem = new KitItemBuilder(Material.HONEYCOMB).build();
        honeyDurationInSeconds = 7;
        cooldown = 35F;
        honeyTrailKey = this.getName() + "honeyTrailKey";
        isHoneyBlockKey = this.getName() + "honeyBlockKey";
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        HoneyTrail honeyTrail = kitPlayer.getKitAttribute(honeyTrailKey);
        if (honeyTrail != null) {
            honeyTrail.cancel();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().hasMetadata(isHoneyBlockKey)) {
            event.setCancelled(true);
        }
    }

    @KitEvent
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Player rightClicked) {
        kitPlayer.activateKitCooldown(this);
        HoneyTrail honeyTrail = new HoneyTrail(rightClicked);
        honeyTrail.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 10);
        kitPlayer.putKitAttribute(honeyTrailKey, honeyTrail);
    }


    @Override
    public float getCooldown() {
        return cooldown;
    }

    private final class HoneyTrail extends BukkitRunnable {
        private final Player player;
        private final KitPlayer kitPlayer;
        private final BlockFace[] directions;
        private final long endTime;
        private final Map<Block, BlockData> oldBlockData;

        private HoneyTrail(Player player) {
            this.player = player;
            this.oldBlockData = new HashMap<>();
            this.kitPlayer = KitApi.getInstance().getPlayer(player);
            this.endTime = System.currentTimeMillis() + honeyDurationInSeconds * 1000L;
            this.directions = new BlockFace[]{
                    BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.SELF,
                    BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST, BlockFace.SOUTH_EAST
            };
        }

        @Override
        public void run() {
            if (player.isDead() || !kitPlayer.isValid()) {
                stop();
                return;
            }

            if (System.currentTimeMillis() > endTime) {
                stop();
                return;
            }

            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            for (BlockFace direction : directions) {
                Block relative = block.getRelative(direction);
                if (relative.getType().isSolid() && !relative.getType().equals(Material.HONEY_BLOCK) && !Utils.isUnbreakableLaborBlock(relative)) {
                    oldBlockData.put(relative, relative.getBlockData().clone());
                    relative.setMetadata(isHoneyBlockKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
                    relative.setType(Material.HONEY_BLOCK);
                }
            }
        }

        private void stop() {
            oldBlockData.keySet().forEach(block -> {
                block.setBlockData(oldBlockData.get(block));
                block.removeMetadata(isHoneyBlockKey, KitApi.getInstance().getPlugin());
            });
            cancel();
        }
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BeequeenKit extends AbstractKit {
    public final static BeequeenKit INSTANCE = new BeequeenKit();

    @IntArg
    private final int honeyDurationInSeconds;
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private BeequeenKit() {
        super("Beequeen", Material.HONEY_BLOCK);
        mainKitItem = new KitItemBuilder(Material.HONEYCOMB).build();
        honeyDurationInSeconds = 7;
        cooldown = 35F;
    }

    @KitEvent
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Player rightClicked) {
        Player damager = (Player) event.getPlayer();
        
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    private final class HoneyTrail extends BukkitRunnable {
        private final Player player;
        private final BlockFace[] directions;
        private final long endTime;

        private HoneyTrail(Player player) {
            this.player = player;
            this.endTime = System.currentTimeMillis() + honeyDurationInSeconds * 1000L;
            this.directions = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.SELF};
        }

        @Override
        public void run() {
            if (System.currentTimeMillis() > endTime) {
                cancel();
                return;
            }

            Block block = player.getLocation().getBlock();
            for (BlockFace direction : directions) {
                Block relative = block.getRelative(direction);
                if (relative.getType().isSolid() && !relative.getType().equals(Material.HONEY_BLOCK) && !Utils.isUnbreakableLaborBlock(relative)) {
                    relative.setType(Material.HONEY_BLOCK);
                }
            }
        }
    }
}

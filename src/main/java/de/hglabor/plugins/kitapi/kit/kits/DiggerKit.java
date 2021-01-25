package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DiggerKit extends AbstractKit {
    public static final DiggerKit INSTANCE = new DiggerKit();

    protected DiggerKit() {
        super("Digger", Material.DRAGON_EGG, 12);
        setMainKitItem(getDisplayMaterial(), 16);
        addSetting(KitSettings.RADIUS, 6);
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if(block != null) {
            block.setType(Material.AIR);
            KitPlayer kitPlayer = KitManager.getInstance().getPlayer(event.getPlayer());
            kitPlayer.activateKitCooldown(this, this.getCooldown());
            new BukkitRunnable() {
                @Override
                public void run() {
                    int dist = (int) Math.ceil((double) ((Integer) getSetting(KitSettings.RADIUS) - 1) / 2);
                    for (int y = -1; y >= -(Integer) getSetting(KitSettings.RADIUS); y--) {
                        for (int x = -dist; x <= dist; x++) {
                            for (int z = -dist; z <= dist; z++) {
                                if (block.getY() + y <= 0) {
                                    continue;
                                }
                                Block b = block.getWorld().getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);
                                if (b.hasMetadata("feastBlock")) {
                                    continue;
                                }
                                if (b.hasMetadata("gladiatorBlock")) {
                                    continue;
                                }
                                if (!b.getType().equals(Material.BEDROCK)) {
                                    if (b instanceof Container) {
                                        b.breakNaturally();
                                    } else {
                                        b.setType(Material.AIR);
                                    }
                                }
                            }
                        }
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 10, 1);
                }

            }.runTaskLater(Bukkit.getPluginManager().getPlugin("HGLaborFFA2.0"), 15);
        }
    }
}

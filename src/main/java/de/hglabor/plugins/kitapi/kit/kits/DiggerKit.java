package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.kit.settings.SoundArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DiggerKit extends AbstractKit {
    public static final DiggerKit INSTANCE = new DiggerKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @IntArg
    private final int radius;
    @FloatArg(min = 0.0F, max = 15F)
    private final float volume;
    @SoundArg
    private final Sound sound;

    private DiggerKit() {
        super("Digger", Material.DRAGON_EGG);
        cooldown = 12F;
        radius = 6;
        volume = 1.8F;
        sound = Sound.BLOCK_STONE_BREAK;
        setMainKitItem(getDisplayMaterial(), 16);
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();
        if (clickedBlock != null) {

            //Reduce Digger item
            if (player.getInventory().getItemInMainHand().isSimilar(mainKitItem)) {
                player.getInventory().getItemInMainHand().subtract();
            } else
            if (player.getInventory().getItemInOffHand().isSimilar(mainKitItem)) {
                player.getInventory().getItemInOffHand().subtract();
            }

            Block block = clickedBlock.getLocation().add(0, 1, 0).getBlock();
            KitPlayer kitPlayer = KitApi.getInstance().getPlayer(event.getPlayer());
            kitPlayer.activateKitCooldown(this);
            new BukkitRunnable() {
                @Override
                public void run() {
                    int dist = (int) Math.ceil((double) (radius - 1) / 2);
                    for (int y = -1; y >= -radius; y--) {
                        for (int x = -dist; x <= dist; x++) {
                            for (int z = -dist; z <= dist; z++) {
                                if (block.getY() + y <= 0) {
                                    continue;
                                }
                                Block b = block.getWorld().getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);
                                if (b.hasMetadata("feastBlock")) {
                                    continue;
                                }
                                if (b.hasMetadata(KitMetaData.GLADIATOR_BLOCK.getKey())) {
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
                    player.getWorld().playSound(player.getLocation(), sound, volume, 1);
                }

            }.runTaskLater(KitApi.getInstance().getPlugin(), 15);
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

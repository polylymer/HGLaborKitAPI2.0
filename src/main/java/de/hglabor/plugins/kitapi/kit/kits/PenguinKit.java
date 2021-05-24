package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.events.KitEventHandler;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PenguinKit extends AbstractKit implements Listener {

    public static final PenguinKit INSTANCE = new PenguinKit();

    @FloatArg(min = 0.0F)
    private final float cooldown;

    protected PenguinKit() {
        super("Penguin", Material.ICE);
        setMainKitItem(getDisplayMaterial());
        this.cooldown = 20f;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @KitEvent
    @Override
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Player rightClicked) {
        for (int i = -1; i < 2; i++) {
            rightClicked.getLocation().clone().add(i, 0, 0).getBlock().setType(Material.ICE);
            rightClicked.getLocation().clone().add(0, i, 0).getBlock().setType(Material.ICE);
            rightClicked.getLocation().clone().add(0, 0, i).getBlock().setType(Material.ICE);
        }
        rightClicked.getWorld().playSound(rightClicked.getLocation(), Sound.BLOCK_SNOW_BREAK, 1f, 1);
        kitPlayer.activateKitCooldown(this);
    }

    @KitEvent
    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {
        Player player = event.getPlayer();
        if(player.getFallDistance() > 2 && player.isSneaking()) {
            player.setGliding(true);
            player.setVelocity(player.getLocation().getDirection().multiply(kitPlayer.isInCombat() ? 1.2 : 2.0));
            kitPlayer.activateKitCooldown(this);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!kitPlayer.isValid() || !player.isOnline()) {
                        cancel();
                    } else {
                        if(player.isGliding()) {
                            player.getLocation().clone().subtract(0,2,0).getBlock().setType(Material.ICE);
                        }
                    }
                }
            }.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 1);
        }
    }

    @KitEvent(ignoreCooldown = true)
    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player entity = (Player) event.getEntity();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(entity);
        if (!KitEventHandler.canUseKit(event, kitPlayer, this)) {
            return;
        }
        ItemStack chestplate = entity.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType().equals(Material.ELYTRA)) {
            return;
        }
        if (!(event.getEntity().isOnGround())) {
            event.setCancelled(true);
        }
    }
}

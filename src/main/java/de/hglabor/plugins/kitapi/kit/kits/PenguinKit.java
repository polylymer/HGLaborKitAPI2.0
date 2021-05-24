package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.events.KitEventHandler;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PenguinKit extends AbstractKit implements Listener {

    public static final PenguinKit INSTANCE = new PenguinKit();

    private final String snowballKey;

    @FloatArg(min = 0.0F)
    private final float cooldown;

    protected PenguinKit() {
        super("Penguin", Material.ICE);
        this.cooldown = 15f;
        this.snowballKey = "penguinSnowball";
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @KitEvent(ignoreCooldown = true)
    @Override
    public void onProjectileHitEvent(ProjectileHitEvent event, KitPlayer kitPlayer, Entity hitEntity) {
        if(kitPlayer.isValid()) {
            if (event.getEntity().getScoreboardTags().contains(snowballKey)) {
                if (hitEntity != null) {
                    for (int i = -1; i < 2; i++) {
                        hitEntity.getLocation().clone().add(i, 0, 0).getBlock().setType(Material.ICE);
                        hitEntity.getLocation().clone().add(0, i, 0).getBlock().setType(Material.ICE);
                        hitEntity.getLocation().clone().add(0, 0, i).getBlock().setType(Material.ICE);
                    }
                    hitEntity.getWorld().playSound(hitEntity.getLocation(), Sound.BLOCK_SNOW_BREAK, 1f, 1);
                }
            }
        }
    }

    @KitEvent(ignoreCooldown = true)
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }
        if (!KitEventHandler.canUseKit(event, kitPlayer, this)) {
            return;
        }
        if(!player.isGliding()) {
            return;
        }
        Snowball snowball = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(2));
        snowball.addScoreboardTag(snowballKey);
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
                            Location location = player.getLocation().clone().subtract(0,2,0);
                            Material old = location.getBlock().getType();
                            location.getBlock().setType(Material.ICE);
                            Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> location.getBlock().setType(old), 40);
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

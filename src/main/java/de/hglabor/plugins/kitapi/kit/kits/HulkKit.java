package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HulkKit extends AbstractKit {
    public final static HulkKit INSTANCE = new HulkKit();

    @FloatArg(min = 0.0F)
    private final float cooldown;
    @FloatArg(min = 0.1F)
    private final float boostPower;

    private HulkKit() {
        super("Hulk", Material.PISTON);
        cooldown = 3.0F;
        boostPower = 1;
    }

    @KitEvent
    public void onPlayerRightClickLivingEntityWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, LivingEntity entity) {
        Player player = event.getPlayer();
        if (player.getPassengers().size() > 1) {
            return;
        }
        player.addPassenger(entity);
        kitPlayer.activateKitCooldown(this);
    }

    @KitEvent
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event, KitPlayer kitPlayer) {
        Player player = event.getPlayer();
        if (player.getPassengers().size() > 0) {
            Entity hulkedEntity = player.getPassengers().get(0);
            launchEntity(hulkedEntity, player);
        }
    }

    @KitEvent
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        Player player = (Player) event.getDamager();
        if (player.getPassengers().size() > 0 && player.getPassengers().get(0).equals(entity)) {
            event.setCancelled(true);
        }
        launchEntity(entity, player);
        entity.setNoDamageTicks(10);
    }

    private void launchEntity(Entity hulkedEntity, Player player) {
        player.removePassenger(hulkedEntity);
        hulkedEntity.setVelocity(player.getLocation().getDirection().normalize().multiply(boostPower));
    }

   /* @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().leaveVehicle();
    } */

    @Override
    public float getCooldown() {
        return cooldown;
    }
}


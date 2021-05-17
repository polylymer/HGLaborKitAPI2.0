package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.List;

public class HulkKit extends AbstractKit implements Listener {
    public final static HulkKit INSTANCE = new HulkKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @FloatArg(min = 0.1F)
    private final float boostPower;

    private HulkKit() {
        super("Hulk", Material.PISTON);
        setMainKitItem(Material.AIR);
        cooldown = 3.0F;
        boostPower = 1;
    }

    @KitEvent
    public void onPlayerRightClickEntityWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Entity entity) {
        hulkEntity(event.getPlayer(), kitPlayer, entity);
    }

    @KitEvent(ignoreCooldown = true)
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event, KitPlayer kitPlayer) {
        Player player = event.getPlayer();
        if (player.getPassengers().size() > 0) {
            Entity hulkedEntity = player.getPassengers().get(0);
            launchEntity(hulkedEntity, player);
        }
    }

    @KitEvent(ignoreCooldown = true)
    public void onHitEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, Entity entity) {
        Player player = (Player) event.getDamager();
        if (player.getPassengers().size() > 0 && player.getPassengers().get(0).equals(entity)) {
            event.setCancelled(true);
        }
        launchEntity(entity, player);
    }

    //TODO made in rush dont know if theres any problem lol
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
            if (!kitPlayer.hasKit(this)) {
                return;
            }
            if (player.getPassengers().size() > 0 && player.getPassengers().get(0).equals(event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer((Player) event.getEntered());
        if (kitPlayer.hasKit(this)) {
            if (kitPlayer.getKitCooldown(this).hasCooldown()) {
                event.setCancelled(event.getVehicle() instanceof Boat || event.getVehicle() instanceof Minecart);
            }
        }
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> {
            List<Entity> passengers = player.getPassengers();
            if (passengers.size() > 0) {
                passengers.forEach(player::removePassenger);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().leaveVehicle();
    }

    private void launchEntity(Entity hulkedEntity, Player player) {
        player.removePassenger(hulkedEntity);
        hulkedEntity.setVelocity(player.getLocation().getDirection().normalize().multiply(boostPower));
        if (hulkedEntity instanceof LivingEntity) ((LivingEntity) hulkedEntity).setNoDamageTicks(10);
    }

    private void hulkEntity(Player player, KitPlayer kitPlayer, Entity entity) {
        if (player.getPassengers().size() > 1) {
            return;
        }
        player.addPassenger(entity);
        kitPlayer.activateKitCooldown(this);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}


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
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.UUID;

public class HulkKit extends AbstractKit implements Listener {

    public final static HulkKit INSTANCE = new HulkKit();

    @FloatArg(min = 0.0F)
    private final float cooldown;
    @FloatArg(min = 0.1F)
    private final float boostPower;


    private HashSet<UUID> hulkingPlayers = new HashSet<>();

    protected HulkKit() {
        super("Hulk", Material.PISTON);
        cooldown = 3.0F;
        boostPower = 1;
    }

    @KitEvent
    @EventHandler
    public void onHulkPickup(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) return;
        if (rightClicked instanceof Boat || rightClicked instanceof Minecart) return;
        if (player.getPassengers().size() > 1) return;

        player.addPassenger(rightClicked);
        hulkingPlayers.add(player.getUniqueId());
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        kitPlayer.activateKitCooldown(this);
    }

    @EventHandler
    public void onHulkLauchEntity(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)))
            return;
        if (!hulkingPlayers.contains(player.getUniqueId())) return;
        Entity hulkedEntity = player.getPassengers().get(0);
        launchEntity(hulkedEntity, player);
        event.setCancelled(true);
    }

    @EventHandler
    public void onHulkLauchEntityByHitting(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        Entity entity = event.getEntity();
        if (player.getPassengers().get(0) == entity) event.setCancelled(true);
        launchEntity(entity, player);
        if (entity instanceof LivingEntity) ((LivingEntity) entity).setNoDamageTicks(10);
    }

    private void launchEntity(Entity hulkedEntity, Player player) {
        player.removePassenger(hulkedEntity);
        hulkedEntity.setVelocity(player.getLocation().getDirection().normalize().multiply(boostPower));
        hulkingPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().leaveVehicle();
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}


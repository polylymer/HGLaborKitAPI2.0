package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static de.hglabor.plugins.kitapi.kit.config.KitSettings.USES;

public abstract class KitEvents {

    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
    }

    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
    }

    public void onAreaEffectCloudDamage(EntityDamageByEntityEvent event) {
    }

    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
    }

    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
    }

    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
    }

    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event) {
    }

    public void onPlayerMove(PlayerMoveEvent event) {
    }

    public void onEntityResurrect(EntityResurrectEvent event) {
    }

    public void onEntityDeath(EntityDeathEvent event) {
    }

    public void onPlayerKillsLivingEntity(EntityDeathEvent event) {
    }

    public void onEntityDamage(EntityDamageEvent event) {
    }

    public void onCraftItem(CraftItemEvent event) {
    }

    public void onProjectileLaunch(ProjectileLaunchEvent event) {
    }

    public void onProjectileHitEvent(ProjectileHitEvent event) {
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
    }

    public void onBlockBreak(BlockBreakEvent event) {
    }

    public void onBlockBreakWithKitItem(BlockBreakEvent event) {
    }

    protected void checkUsesForCooldown(KitPlayer kitPlayer, AbstractKit kit) {
        kitPlayer.putKitAttribute(kit, kitPlayer.getKitAttribute(kit) != null ? (Integer) kitPlayer.getKitAttribute(kit) + 1 : 0);
        if ((Integer) kitPlayer.getKitAttribute(kit) >= (Integer) kit.getSetting(USES)) {
            kitPlayer.activateKitCooldown(kit, kit.getCooldown());
            kitPlayer.putKitAttribute(kit, 0);
        }
    }
}

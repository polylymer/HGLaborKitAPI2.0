package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public abstract class KitEvents {

    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
    }

    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
    }

    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
    }

    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
    }

    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
    }

    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
    }

    public void onPlayerLeftClickKitItem(PlayerInteractEvent event, KitPlayer kitPlayer) {
    }

    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, Player rightClicked) {
    }

    public void onPlayerRightClickLivingEntityWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, LivingEntity entity) {
    }

    public void onEntityResurrect(EntityResurrectEvent event) {
    }

    public void onEntityDeath(EntityDeathEvent event) {
    }

    public void onPlayerKillsLivingEntity(EntityDeathEvent event, Player killer, Entity entity) {
    }

    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {
    }

    public void onKitPlayerDeath(PlayerDeathEvent event) {
    }

    public void onEntityDamage(EntityDamageEvent event) {
    }

    public void onCraftItem(CraftItemEvent event) {
    }

    public void onProjectileLaunch(ProjectileLaunchEvent event) {
    }

    public void onBlockBreakWithKitItem(BlockBreakEvent event) {
    }

    public void onHitEntityWithKitItem(EntityDamageEvent event, KitPlayer kitPlayer, Entity entity) {
    }

    public void onPlayerRightClickEntityWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Entity entity) {
    }
}

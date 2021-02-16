package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    public void onPlayerLeftClickKitItem(PlayerInteractEvent event) {
    }

    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event) {
    }

    public void onPlayerRightClickLivingEntityWithKitItem(PlayerInteractAtEntityEvent event) {
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

    public void onBlockBreakWithKitItem(BlockBreakEvent event) {
    }
}

package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static de.hglabor.plugins.kitapi.kit.config.KitSettings.USES;

public abstract class KitEvents {

    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
    }

    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
    }

    public void onNinjaSneak(PlayerToggleSneakEvent event) {
    }

    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
    }

    protected void checkUsesForCooldown(KitPlayer kitPlayer, AbstractKit kit) {
        kitPlayer.putKitAttribute(kit, kitPlayer.getKitAttribute(kit) != null ? (Integer) kitPlayer.getKitAttribute(kit) + 1 : 0);
        if ((Integer) kitPlayer.getKitAttribute(kit) >= (Integer) kit.getSetting(USES)) {
            kitPlayer.activateKitCooldown(kit, kit.getCooldown());
            kitPlayer.putKitAttribute(kit, 0);
        }
    }

    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    }
}

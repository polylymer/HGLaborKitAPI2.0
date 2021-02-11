package de.hglabor.plugins.kitapi.listener;

import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.LastHitInformation;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LastHitDetection implements Listener {

    @EventHandler
    public void onPlayerHitOtherPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            KitPlayer attacker = KitManager.getInstance().getPlayer((Player) event.getDamager());
            KitPlayer enemy = KitManager.getInstance().getPlayer((Player) event.getEntity());
            if (attacker.isValid() && enemy.isValid()) {
                LastHitInformation lastHitInformation = attacker.getLastHitInformation();
                lastHitInformation.setPlayerTimeStamp(System.currentTimeMillis());
                lastHitInformation.setLastPlayer((Player) event.getEntity());
                lastHitInformation.setEntityTimeStamp(System.currentTimeMillis());
                lastHitInformation.setLastEntity((LivingEntity) event.getEntity());
            }
        } else if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            KitPlayer attacker = KitManager.getInstance().getPlayer((Player) event.getDamager());
            if (attacker.isValid()) {
                LastHitInformation lastHitInformation = attacker.getLastHitInformation();
                lastHitInformation.setEntityTimeStamp(System.currentTimeMillis());
                lastHitInformation.setLastEntity((LivingEntity) event.getEntity());
            }
        }
    }
}

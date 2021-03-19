package de.hglabor.plugins.kitapi.listener;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.pvp.LastHitInformation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LastHitDetection implements Listener {

    @EventHandler
    public void onPlayerHitOtherPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            KitPlayer attacker = KitApi.getInstance().getPlayer((Player) event.getDamager());
            KitPlayer enemy = KitApi.getInstance().getPlayer((Player) event.getEntity());
            if (attacker.isValid() && enemy.isValid()) {
                LastHitInformation lastHitInformation = attacker.getLastHitInformation();
                long NOW = System.currentTimeMillis();
                lastHitInformation.setPlayerTimeStamp(NOW);
                lastHitInformation.setLastPlayer((Player) event.getEntity());
                lastHitInformation.setEntityTimeStamp(NOW);
                lastHitInformation.setLastEntity((LivingEntity) event.getEntity());

                LastHitInformation lastHitInformationEnemy = enemy.getLastHitInformation();
                lastHitInformationEnemy.setLastDamager((Player) event.getDamager());
                lastHitInformationEnemy.setLastDamagerTimestamp(NOW);
            }
        } else if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            KitPlayer attacker = KitApi.getInstance().getPlayer((Player) event.getDamager());
            if (attacker.isValid()) {
                LastHitInformation lastHitInformation = attacker.getLastHitInformation();
                lastHitInformation.setEntityTimeStamp(System.currentTimeMillis());
                lastHitInformation.setLastEntity((LivingEntity) event.getEntity());
            }
        }
    }
}

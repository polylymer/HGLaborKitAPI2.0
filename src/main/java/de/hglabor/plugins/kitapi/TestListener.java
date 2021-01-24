package de.hglabor.plugins.kitapi;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEventHandler;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.player.KitPlayerSupplier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TestListener extends KitEventHandler implements Listener {

    public TestListener(KitPlayerSupplier playerSupplier) {
        super(playerSupplier);
    }

    @EventHandler
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getDamager());
            for (AbstractKit kit : kitPlayer.getKits()) {
                if (canUseKit(kitPlayer,kit)) {
                    kit.onPlayerAttacksLivingEntity(event, kitPlayer, (LivingEntity) event.getEntity());
                }
            }
        }
    }


}

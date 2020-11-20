package de.hglabor.plugins.kitapi.kit;

import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class KitEvents {

    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
    }
}

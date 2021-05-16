package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PacifistKit extends AbstractKit {
    public static final PacifistKit INSTANCE = new PacifistKit();

    @FloatArg()
    private final float damageMultiplier;

    private PacifistKit() {
        super("Pacifist", Material.DANDELION);
        damageMultiplier = 0.75F;
    }

    @KitEvent
    public void onEntityDamage(EntityDamageEvent event) {
        event.setDamage(event.getDamage() * damageMultiplier);
    }

    @KitEvent
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        event.setDamage(event.getDamage() * damageMultiplier);
    }
}

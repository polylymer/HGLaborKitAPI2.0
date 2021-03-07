package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */
public class TurtleKit extends AbstractKit {
    public static final TurtleKit INSTANCE = new TurtleKit();

    @DoubleArg(min = 0.1)
    private final double dealingDamage, damageTrigger, damageReplacement;

    private TurtleKit() {
        super("Turtle", Material.TURTLE_HELMET);
        dealingDamage = 0.5D;
        damageTrigger = 0.5D;
        damageReplacement = 0.5D;
    }

    @KitEvent
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();
        if (event.getDamage() > damageTrigger && player.isSneaking()) {
            event.setDamage(damageReplacement);
        }
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        attacker.getBukkitPlayer().ifPresent(player -> {
            if (player.isSneaking()) event.setDamage(dealingDamage);
        });
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */

public class AnchorKit extends AbstractKit {
    public static final AnchorKit INSTANCE = new AnchorKit();

    private AnchorKit() {
        super("Anchor", Material.ANVIL);
        addEvents(Collections.singletonList(EntityDamageByEntityEvent.class));
    }

    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (entity instanceof Player) {
            if (KitApi.getInstance().getPlayer((Player) entity).hasKit(this) || attacker.hasKit(this)) {
                setKnockbackAttribute((LivingEntity) entity);
                Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> resetKnockbackAttribute((LivingEntity) entity), 1);
            }
        } else if (attacker.hasKit(this)) {
            setKnockbackAttribute((LivingEntity) entity);
            Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> resetKnockbackAttribute((LivingEntity) entity), 1);
        }

    }

    private static void setKnockbackAttribute(LivingEntity e) {
        e.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }

    private static void resetKnockbackAttribute(LivingEntity e) {
        e.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0);
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        resetKnockbackAttribute(Bukkit.getPlayer(kitPlayer.getUUID()));
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        setKnockbackAttribute(Bukkit.getPlayer(kitPlayer.getUUID()));
    }
}


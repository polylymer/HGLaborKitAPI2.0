package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;

public class StomperKit extends AbstractKit {
    public static final StomperKit INSTANCE = new StomperKit();
    protected StomperKit() {
        super("Stomper", Material.DIAMOND_BOOTS);
        addEvents(Collections.singletonList(EntityDamageEvent.class));
        addSetting(KitSettings.RADIUS, 3);
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            final double STOMPER_DAMAGE = event.getDamage();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
            if (STOMPER_DAMAGE > 4) {
                event.setDamage(4);
            }
            for (Entity entity : player.getNearbyEntities((Integer)getSetting(KitSettings.RADIUS),(Integer)getSetting(KitSettings.RADIUS),(Integer)getSetting(KitSettings.RADIUS))) {
                if(entity instanceof LivingEntity) {
                    if(entity != player) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if(entity instanceof Player) {
                            Player playerEntity = (Player) entity;
                            playerEntity.playSound(playerEntity.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                        }
                        livingEntity.damage(STOMPER_DAMAGE);
                    }
                }
            }
        }
    }
}

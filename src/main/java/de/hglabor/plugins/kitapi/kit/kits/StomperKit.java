package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

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
        if (event.getEntity() instanceof Player) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;
            Player player = (Player) event.getEntity();
            final double STOMPER_DAMAGE = event.getDamage();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
            if (STOMPER_DAMAGE > 4) {
                event.setDamage(4);
            }
            for (LivingEntity livingEntity : player.getWorld().getNearbyEntitiesByType(LivingEntity.class, player.getLocation(), ((Integer) getSetting(KitSettings.RADIUS)).doubleValue())) {
                if (livingEntity == player) {
                    continue;
                }
                if (livingEntity instanceof Player) {
                    Player playerEntity = (Player) livingEntity;
                    KitPlayer kitPlayer = KitApi.getInstance().getPlayer(playerEntity);
                    if (kitPlayer.isValid()) {
                        if (!playerEntity.isSneaking()) {
                            livingEntity.damage(STOMPER_DAMAGE);
                            livingEntity.setVelocity(livingEntity.getVelocity().setY(livingEntity.getVelocity().getY() * STOMPER_DAMAGE / 4));
                        }
                        kitPlayer.getLastHitInformation().setLastDamager(player);
                        kitPlayer.getLastHitInformation().setLastDamagerTimestamp(System.currentTimeMillis());
                        playerEntity.playSound(playerEntity.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.05f, 1);
                    }
                } else {
                    livingEntity.damage(STOMPER_DAMAGE);
                    livingEntity.setVelocity(livingEntity.getVelocity().setY(livingEntity.getVelocity().getY() * STOMPER_DAMAGE / 4));
                }
            }
        }
    }
}

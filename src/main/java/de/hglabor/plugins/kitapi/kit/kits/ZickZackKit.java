package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ZickZackKit extends AbstractKit {
    public static final ZickZackKit INSTANCE = new ZickZackKit();

    private ZickZackKit() {
        super("ZickZack", Material.DIAMOND_BLOCK);
        addEvents(ImmutableList.of(EntityDamageByEntityEvent.class));
        addSetting(KitSettings.LIKELIHOOD, 10);
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        kitPlayer.putKitAttribute(this, new HashMap<UUID, Integer>());
    }
    
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent e, KitPlayer attacker, LivingEntity entity) {
        if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) {
            return;
        }
        Player enemy = (Player) entity;
        Map<UUID, Integer> combo = attacker.getKitAttribute(this);
        int likelihood = getSetting(KitSettings.LIKELIHOOD);

        if (!combo.containsKey(enemy.getUniqueId())) {
            combo.put(enemy.getUniqueId(), 0);
        } else {
            if (combo.get(enemy.getUniqueId()) < likelihood) {
                combo.replace(enemy.getUniqueId(), combo.get(enemy.getUniqueId()) + 1);
            }
        }
    }

    @Override
    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
        if (!(attacker instanceof Player)) {
            return;
        }
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        int likelihood = getSetting(KitSettings.LIKELIHOOD);
        Map<UUID, Integer> combo = kitPlayer.getKitAttribute(this);
        if (combo.containsKey(attacker.getUniqueId())) {
            int chance = new Random().nextInt(likelihood) + 1;
            int comboAmount = combo.get(attacker.getUniqueId());
            if (chance > likelihood - comboAmount) {
                event.setCancelled(true);
                combo.replace(attacker.getUniqueId(), combo.get(attacker.getUniqueId()) - 1);
                ((Player) attacker).playSound(attacker.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 100, 100);
                player.playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 100, 100);
            } else {
                combo.replace(attacker.getUniqueId(), 0);
            }
        }
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
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
    @IntArg
    private final int likelihood,minCombo;
    private final String comboCounterKey;

    private ZickZackKit() {
        super("ZickZack", Material.DIAMOND_BLOCK);
        comboCounterKey = this.getName() + "combos";
        likelihood = 20;
        minCombo = 3;
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        kitPlayer.putKitAttribute(comboCounterKey, new HashMap<UUID, Integer>());
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent e, KitPlayer attacker, LivingEntity entity) {
        if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) {
            return;
        }
        Player enemy = (Player) entity;
        Map<UUID, Integer> combo = attacker.getKitAttribute(comboCounterKey);

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
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        Map<UUID, Integer> combo = kitPlayer.getKitAttribute(comboCounterKey);
        if (combo.containsKey(attacker.getUniqueId())) {
            int comboAmount = combo.get(attacker.getUniqueId());

            //Zickzack will only be triggered if player already combod
            if (comboAmount < minCombo) {
                return;
            }

            int chance = new Random().nextInt(likelihood) + 1;
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

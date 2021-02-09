package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class CannibalKit extends AbstractKit {
    public final static CannibalKit INSTANCE = new CannibalKit();

    private CannibalKit() {
        super("Cannibal", Material.TROPICAL_FISH);
        addSetting(KitSettings.EFFECT_DURATION,1);
        addSetting(KitSettings.EFFECT_MULTIPLIER, 1);
        addSetting(KitSettings.LIKELIHOOD,33);
        addEvents(Collections.singletonList(EntityDamageByEntityEvent.class));
    }

    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (KitManager.getInstance().getPlayer((Player) entity).hasKit(this)) {
            return;
        }
        Player attack = (Player) event.getDamager();
        Player enemy = ((Player) event.getEntity());

        int foodLevelOfPlayer = attack.getFoodLevel();
        int foodLevelOfEnemy = enemy.getFoodLevel();

        if (foodLevelOfPlayer < 20) {
            int difference = 20 - foodLevelOfPlayer;
            enemy.setFoodLevel(foodLevelOfEnemy - difference);
            attack.setFoodLevel(foodLevelOfPlayer + difference);
        }

        if (ChanceUtils.roll(getSetting(KitSettings.LIKELIHOOD))) {
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20, getSetting(KitSettings.EFFECT_MULTIPLIER)));
        }
    }
}

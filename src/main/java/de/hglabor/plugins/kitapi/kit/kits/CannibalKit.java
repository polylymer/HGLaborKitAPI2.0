package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CannibalKit extends AbstractKit {
    public final static CannibalKit INSTANCE = new CannibalKit();

    @IntArg
    private final int likelihood, effectDuration, effectMultiplier;

    private CannibalKit() {
        super("Cannibal", Material.TROPICAL_FISH);
        effectDuration = 1;
        effectMultiplier = 1;
        likelihood = 33;
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (KitApi.getInstance().getPlayer((Player) entity).hasKit(this)) {
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

        if (ChanceUtils.roll(likelihood)) {
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, effectDuration * 20, effectMultiplier));
        }
    }
}

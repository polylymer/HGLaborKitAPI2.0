package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ViperKit extends AbstractKit {
    public final static ViperKit INSTANCE = new ViperKit();
    @IntArg
    private final int likelihood, effectDuration, effectMultiplier;

    private ViperKit() {
        super("Viper", Material.SPIDER_EYE);
        likelihood = 30;
        effectDuration = 4;
        effectMultiplier = 0;
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(likelihood)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, effectDuration * 20, effectMultiplier, true, true));
        }
    }
}

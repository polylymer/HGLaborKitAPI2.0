package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class ViperKit extends AbstractKit {
    public final static ViperKit INSTANCE = new ViperKit();

    protected ViperKit() {
        super("Viper", Material.SPIDER_EYE);
        addSetting(KitSettings.EFFECT_DURATION,4);
        addSetting(KitSettings.EFFECT_MULTIPLIER, 0);
        addSetting(KitSettings.LIKELIHOOD, 30);
        addEvents(Collections.singletonList(EntityDamageByEntityEvent.class));
    }

    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(getSetting(KitSettings.LIKELIHOOD))) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20, (Integer)getSetting(KitSettings.EFFECT_MULTIPLIER), true,true));
        }
    }
}

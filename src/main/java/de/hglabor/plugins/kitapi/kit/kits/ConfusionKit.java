package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.ChanceUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ConfusionKit extends AbstractKit {

    private final static ConfusionKit instance = new ConfusionKit();

    public static ConfusionKit getInstance() {return instance;}

    protected ConfusionKit() {
        super("Confusion", Material.SUGAR);
        addSetting(KitSettings.EFFECT_DURATION,2);
        addSetting(KitSettings.LIKELIHOOD,33);
    }

    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(getSetting(KitSettings.LIKELIHOOD))) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20, 0));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20, 0));
        }
    }

}

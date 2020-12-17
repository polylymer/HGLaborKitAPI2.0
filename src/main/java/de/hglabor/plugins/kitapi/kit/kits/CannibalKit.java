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

public class CannibalKit extends AbstractKit {

    private final static CannibalKit instance = new CannibalKit();

    public static CannibalKit getInstance() {return instance;}

    protected CannibalKit() {
        super("Cannibal", Material.ROTTEN_FLESH);
        addSetting(KitSettings.EFFECT_DURATION,2);
        addSetting(KitSettings.LIKELIHOOD,33);
    }

    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(getSetting(KitSettings.LIKELIHOOD))) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20, 0));
        }
    }

}

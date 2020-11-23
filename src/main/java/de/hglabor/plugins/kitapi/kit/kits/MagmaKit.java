package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.ChanceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MagmaKit extends AbstractKit {
    private final static MagmaKit instance = new MagmaKit();

    private MagmaKit() {
        super("Magma", Material.MAGMA_BLOCK);
        addSetting(KitSettings.EFFECT_DURATION,2);
        addSetting(KitSettings.LIKELIHOOD,33);
    }

    public static MagmaKit getInstance() {
        return instance;
    }

    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(getSetting(KitSettings.LIKELIHOOD))) {
            entity.setFireTicks((Integer) getSetting(KitSettings.EFFECT_DURATION) * 20);
        }
    }
}

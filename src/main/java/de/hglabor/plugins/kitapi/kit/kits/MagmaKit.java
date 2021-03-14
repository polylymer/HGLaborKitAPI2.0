package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MagmaKit extends AbstractKit {
    public final static MagmaKit INSTANCE = new MagmaKit();
    @IntArg
    private final int likelihood, effectDuration;

    private MagmaKit() {
        super("Magma", Material.MAGMA_BLOCK);
        likelihood = 33;
        effectDuration = 2;
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(likelihood)) {
            entity.setFireTicks(effectDuration * 20);
        }
    }
}

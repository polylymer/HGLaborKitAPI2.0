package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.SoundArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class AnchorKit extends AbstractKit {
    public static final AnchorKit INSTANCE = new AnchorKit();
    @SoundArg
    private final Sound hitSound;
    @FloatArg
    private final float soundVolume;

    private AnchorKit() {
        super("Anchor", Material.ANVIL);
        hitSound = Sound.BLOCK_ANVIL_PLACE;
        soundVolume = 0.3F;
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        handleAnchor(event);
    }

    @KitEvent
    @Override
    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
        handleAnchor(event);
    }

    private void handleAnchor(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        Entity damager = event.getDamager();
        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
            entity.setVelocity(new Vector(0, 0, 0));
        }, 1L);
        damager.getLocation().getWorld().playSound(
                damager.getLocation(),
                hitSound,
                soundVolume,
                1F
        );
        entity.getLocation().getWorld().playSound(
                entity.getLocation(),
                hitSound,
                soundVolume,
                1F
        );
    }
}


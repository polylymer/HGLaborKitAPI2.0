package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GravityKit extends AbstractKit {
    public static final GravityKit INSTANCE = new GravityKit();

    protected GravityKit() {
        super("Gravity", Material.MAGENTA_GLAZED_TERRACOTTA, 30);
        setMainKitItem(getDisplayMaterial());
        addSetting(KitSettings.EFFECT_MULTIPLIER, 3);
        addSetting(KitSettings.EFFECT_DURATION, 1);
        addSetting(KitSettings.USES, 3);
        addEvents(ImmutableList.of(PlayerInteractEvent.class, EntityDamageByEntityEvent.class));
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player != null) {
            if (player.hasPotionEffect(PotionEffectType.LEVITATION)) {
                player.removePotionEffect(PotionEffectType.LEVITATION);
            }
        }
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(event.getPlayer());
        Player player = event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.LEVITATION)) {
            kitPlayer.activateKitCooldown(this, this.getCooldown());
            player.removePotionEffect(PotionEffectType.LEVITATION);
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER)));
        }
    }

    @Override
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * (Integer) getSetting(KitSettings.EFFECT_DURATION), getSetting(KitSettings.EFFECT_MULTIPLIER)));
        KitManager.getInstance().checkUsesForCooldown(attacker, this);
    }
}

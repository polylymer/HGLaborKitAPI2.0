package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
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
    @IntArg
    private final int maxUses;
    @IntArg(min = 0)
    private final int gravityAmplifier;
    @IntArg(min = 1)
    private final int gravityDuration;
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private GravityKit() {
        super("Gravity", Material.MAGENTA_GLAZED_TERRACOTTA);
        setMainKitItem(getDisplayMaterial());
        cooldown = 30F;
        maxUses = 3;
        gravityAmplifier = 3;
        gravityDuration = 1;
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

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(event.getPlayer());
        Player player = event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.LEVITATION)) {
            kitPlayer.activateKitCooldown(this);
            player.removePotionEffect(PotionEffectType.LEVITATION);
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, gravityAmplifier));
        }
    }

    @KitEvent
    @Override
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * gravityDuration, gravityAmplifier));
        KitApi.getInstance().checkUsesForCooldown(attacker, this, maxUses);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

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
    /**
     * Duration of the gravity effect applied to a player on hit
     */
    @IntArg(min = 1)
    private final int targetGravityDuration;
    /**
     * The duration of the effect when using item normally
     */
    @IntArg(min = 20)
    private final int gravityDuration;
    /**
     * The duration of the effect applied when using the item
     * and being in combat
     */
    @IntArg(min = 0)
    private final int combatGravityDuration;
    @FloatArg(min = 0.0F)
    private final float cooldown;
    /**
     * Time how long the combat block
     * for activating levitation should last
     * 10 is the maximum value because it's the default
     * for the check
     */
    @IntArg(min = 0, max = 10)
    private final int inCombatCheckTimeout;


    private GravityKit() {
        super("Gravity", Material.MAGENTA_GLAZED_TERRACOTTA);
        this.setMainKitItem(this.getDisplayMaterial());
        this.cooldown = 30F;
        this.maxUses = 3;
        this.gravityAmplifier = 3;
        this.targetGravityDuration = 1;
        this.inCombatCheckTimeout = 5;
        this.gravityDuration = 30;
        this.combatGravityDuration = 4;
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
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
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        //Cancel if effect is present
        if (player.hasPotionEffect(PotionEffectType.LEVITATION)) {
            kitPlayer.activateKitCooldown(this);
            player.removePotionEffect(PotionEffectType.LEVITATION);
        }
        //Shorten levitation effect in combat to avoid running away
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.LEVITATION,
                (kitPlayer.isInCombat(this.inCombatCheckTimeout) ? this.combatGravityDuration : this.gravityDuration) * 20,
                this.gravityAmplifier
        ));
    }

    @KitEvent
    @Override
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * this.targetGravityDuration,
                this.gravityAmplifier));
        KitApi.getInstance().checkUsesForCooldown(attacker, this, this.maxUses);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */
public class BerserkerKit extends AbstractKit {
    public static final BerserkerKit INSTANCE = new BerserkerKit();
    @IntArg
    private final int playerStrengthDuration, playerStrengthAmplifier, playerSpeedDuration, playerSpeedAmplifier;
    @IntArg
    private final int mobStrengthDuration, mobStrengthAmplifier, mobSpeedDuration, mobSpeedAmplifier;

    private BerserkerKit() {
        super("Berserker", Material.BLAZE_POWDER);
        playerStrengthDuration = 12;
        playerStrengthAmplifier = 1;
        playerSpeedDuration = 8;
        playerSpeedAmplifier = 2;
        mobStrengthDuration = 4;
        mobStrengthAmplifier = 1;
        mobSpeedDuration = 4;
        mobSpeedAmplifier = 1;
    }

    @KitEvent
    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        Player killerPlayer = Bukkit.getPlayer(killer.getUUID());
        if (killerPlayer != null) {
            killerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * playerStrengthDuration, playerStrengthAmplifier, true, true));
            killerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * playerSpeedDuration, playerSpeedAmplifier, true, true));
        }
    }

    @KitEvent
    @Override
    public void onPlayerKillsLivingEntity(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * mobStrengthDuration, mobStrengthAmplifier, true, true));
            killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * mobSpeedDuration, mobSpeedAmplifier, true, true));
        }
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Optional;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */
public class BerserkerKit extends AbstractKit {
    public static final BerserkerKit INSTANCE = new BerserkerKit();

    private BerserkerKit() {
        super("Berserker", Material.BLAZE_POWDER);
        //TODO addSetting for Effects...
        addEvents(List.of(EntityDeathEvent.class, PlayerDeathEvent.class));
    }

    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        Player killerPlayer = Bukkit.getPlayer(killer.getUUID());
        if (killerPlayer != null) {
            killerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 12, 1, true, true));
            killerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 8, 2, true, true));
        }
    }

    @Override
    public void onPlayerKillsLivingEntity(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 4, 1, true, true));
            killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 4, 1, true, true));
        }
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;


public class SquidKit extends AbstractKit {
    public final static SquidKit INSTANCE = new SquidKit();

    private SquidKit() {
        super("Squid", Material.SQUID_SPAWN_EGG, 13);
        addEvents(ImmutableList.of(PlayerToggleSneakEvent.class));
        addSetting(KitSettings.RADIUS, 5);
        addSetting(KitSettings.EFFECT_DURATION, 3);
        addSetting(KitSettings.EFFECT_MULTIPLIER, 3);
    }

    @Override
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        int counter = 0;
        for (KitPlayer enemyKitPlayer : getKitPlayerInRadius(player)) {
            Player nearbyPlayer = Bukkit.getPlayer(enemyKitPlayer.getUUID());
            if (nearbyPlayer != player && nearbyPlayer != null) {
                if (enemyKitPlayer.isValid()) {
                    counter++;
                    nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * (Integer) getSetting(KitSettings.EFFECT_DURATION), (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER)));
                    Squid squid = (Squid) nearbyPlayer.getWorld().spawnEntity(nearbyPlayer.getEyeLocation(), EntityType.SQUID);
                    Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), squid::remove, 2 * 20);
                }
            }
        }
        if (counter > 0) {
            kitPlayer.activateKitCooldown(this, this.getCooldown());
        }
    }

    private List<KitPlayer> getKitPlayerInRadius(Player player) {
        List<KitPlayer> enemies = new ArrayList<>();
        for (Player nearbyPlayer : player.getWorld().getNearbyEntitiesByType(Player.class, player.getLocation(), ((Integer) getSetting(KitSettings.RADIUS)).doubleValue())) {
            KitPlayer nearbyKitPlayer = KitApi.getInstance().getPlayer(nearbyPlayer);
            if (nearbyKitPlayer.isValid()) {
                enemies.add(nearbyKitPlayer);
            }
        }
        return enemies;
    }
}



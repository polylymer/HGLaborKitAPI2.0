package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import static de.hglabor.utils.localization.Localization.t;

public class VampireKit extends AbstractKit {
    public final static VampireKit INSTANCE = new VampireKit();

    @DoubleArg
    private final double heartsProKill, defaultHealth, maxHealth;
    private final String currentHealthKey;

    private VampireKit() {
        super("Vampire", Material.POPPY);
        this.heartsProKill = 2D;
        this.defaultHealth = 20D;
        this.maxHealth = 40D;
        this.currentHealthKey = this.getName() + "currentHealth";
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> player.setMaxHealth(kitPlayer.getKitAttributeOrDefault(currentHealthKey, defaultHealth)));
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> player.setMaxHealth(defaultHealth));
    }

    @KitEvent(clazz = PlayerDeathEvent.class)
    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        killer.getBukkitPlayer().ifPresent(player -> {
            player.sendMessage(Localization.INSTANCE.getMessage("vampire.extraHealth", ImmutableMap.of("hearts", String.valueOf(heartsProKill)), ChatUtils.locale(player)));
            double newHealth = player.getMaxHealth() + heartsProKill;
            if (newHealth <= maxHealth) {
                player.setMaxHealth(newHealth);
                player.setHealth(player.getMaxHealth());
                killer.putKitAttribute(currentHealthKey, player.getMaxHealth());
            } else {
                player.sendMessage(t("vampire.maxHealth", ChatUtils.locale(player)));
            }
        });
    }

    @KitEvent
    @Override
    public void onPlayerKillsLivingEntity(EntityDeathEvent event, Player killer, Entity entity) {
        killer.sendMessage(Localization.INSTANCE.getMessage("vampire.restoredHealth", ChatUtils.locale(killer)));
        killer.setHealth(killer.getMaxHealth());
    }
}

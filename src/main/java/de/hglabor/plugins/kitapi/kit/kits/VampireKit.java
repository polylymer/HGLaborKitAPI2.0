package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class VampireKit extends AbstractKit {
    public final static VampireKit INSTANCE = new VampireKit();

    @DoubleArg
    private final double heartsProKill, defaultHealth;
    private final String maxHealthKey;

    private VampireKit() {
        super("Vampire", Material.POPPY);
        this.heartsProKill = 2D;
        this.defaultHealth = 20D;
        this.maxHealthKey = this.getName() + "maxHealth";
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> player.setMaxHealth(kitPlayer.getKitAttributeOrDefault(maxHealthKey, defaultHealth)));
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> player.setMaxHealth(defaultHealth));
    }

    @KitEvent(clazz = PlayerDeathEvent.class)
    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        killer.getBukkitPlayer().ifPresent(player -> {
            player.setMaxHealth(player.getMaxHealth() + heartsProKill);
            player.setHealth(player.getMaxHealth());
            killer.putKitAttribute(maxHealthKey, player.getMaxHealth());
        });
    }

    @KitEvent
    @Override
    public void onPlayerKillsLivingEntity(EntityDeathEvent event, Player killer, Entity entity) {
        killer.setHealth(killer.getMaxHealth());
    }
}

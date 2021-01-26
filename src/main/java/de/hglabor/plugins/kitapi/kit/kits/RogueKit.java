package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RogueKit extends AbstractKit {
    public static final RogueKit INSTANCE = new RogueKit();

    private RogueKit() {
        super("Rogue", Material.GRAY_DYE);
        setCooldown(40);
        setMainKitItem(getDisplayMaterial());
        addSetting(KitSettings.RADIUS, 10);
        addSetting(KitSettings.EFFECT_DURATION, 15);
        addEvents(Collections.singletonList(PlayerInteractEvent.class));
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        int counter = 0;
        for (KitPlayer nearbyPlayerKitOwner : getKitPlayerInRadius(player)) {
            Player nearbyPlayer = Bukkit.getPlayer(nearbyPlayerKitOwner.getUUID());
            if (nearbyPlayer != player) {
                counter++;
                if (!nearbyPlayerKitOwner.areKitsDisabled() && nearbyPlayerKitOwner.isValid()) {
                    nearbyPlayerKitOwner.disableKits(true);
                    Bukkit.getScheduler().runTaskLater(KitManager.getInstance().getPlugin(), () -> nearbyPlayerKitOwner.disableKits(false), (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20);
                }
            }
        }
        player.sendMessage("You disabled the kits of " + counter + " players");
        kitPlayer.activateKitCooldown(this, this.getCooldown());
    }

    private List<KitPlayer> getKitPlayerInRadius(Player player) {
        List<KitPlayer> enemies = new ArrayList<>();
        for (Player nearbyPlayer : player.getWorld().getNearbyEntitiesByType(Player.class, player.getLocation(), ((Integer) getSetting(KitSettings.RADIUS)).doubleValue())) {
            KitPlayer nearbyKitPlayer = KitManager.getInstance().getPlayer(nearbyPlayer);
            if (nearbyKitPlayer.isValid()) {
                enemies.add(nearbyKitPlayer);
            }
        }
        return enemies;
    }
}

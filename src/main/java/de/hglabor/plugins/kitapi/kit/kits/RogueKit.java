package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class RogueKit extends AbstractKit {
    public static final RogueKit INSTANCE = new RogueKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @IntArg
    private final int effectDuration;
    @DoubleArg
    private final double radius;

    private RogueKit() {
        super("Rogue", Material.GRAY_DYE);
        cooldown = 40F;
        radius = 10D;
        effectDuration = 15;
        setMainKitItem(getDisplayMaterial());
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        int counter = 0;
        for (KitPlayer nearbyPlayerKitOwner : getKitPlayersInRadius(player, radius)) {
            Player nearbyPlayer = Bukkit.getPlayer(nearbyPlayerKitOwner.getUUID());
            if (nearbyPlayer != player) {
                counter++;
                if (!nearbyPlayerKitOwner.areKitsDisabled() && nearbyPlayerKitOwner.isValid()) {
                    nearbyPlayerKitOwner.disableKits(true);
                    Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> nearbyPlayerKitOwner.disableKits(false), effectDuration * 20L);
                }
            }
        }
        player.sendMessage("You disabled the kits of " + counter + " players");
        kitPlayer.activateKitCooldown(this);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

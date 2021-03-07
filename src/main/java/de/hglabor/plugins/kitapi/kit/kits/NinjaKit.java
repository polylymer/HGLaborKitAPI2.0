package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class NinjaKit extends AbstractKit {
    private final static NinjaKit instance = new NinjaKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private NinjaKit() {
        super("Ninja", Material.INK_SAC);
        cooldown = 13F;
        addSetting(KitSettings.RADIUS, (int) Math.pow(30, 2));
    }

    public static NinjaKit getInstance() {
        return instance;
    }

    @KitEvent
    @Override
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        KitPlayer attacker = KitApi.getInstance().getPlayer(player);
        if (attacker == null || attacker.getLastHitInformation() == null || attacker.getLastHitInformation().getLastPlayer() == null)
            return;
        KitPlayer lastHittedPlayer = KitApi.getInstance().getPlayer(attacker.getLastHitInformation().getLastPlayer());
        if (lastHittedPlayer == null) {
            return;
        }
        Player toTeleport = Bukkit.getPlayer(lastHittedPlayer.getUUID());
        if (toTeleport != null) {
            if (!toTeleport.isOnline()) return;
            if (!lastHittedPlayer.isValid()) return;
            if (attacker.getLastHitInformation().getPlayerTimeStamp() + this.getCooldown() * 1000L > System.currentTimeMillis()) {
                if (distanceBetweenPlayers(player, toTeleport) < (int) getSetting(KitSettings.RADIUS)) {
                    player.teleport(calculateNinjaBehind(toTeleport));
                    attacker.activateKitCooldown(this);
                    attacker.getLastHitInformation().setPlayerTimeStamp(0);
                    attacker.getLastHitInformation().setLastPlayer(null);
                }
            }
        }
    }

    private Location calculateNinjaBehind(Entity entity) {
        float nang = entity.getLocation().getYaw() + 90;
        if (nang < 0) nang += 360;
        double nX = Math.cos(Math.toRadians(nang));
        double nZ = Math.sin(Math.toRadians(nang));
        return entity.getLocation().clone().subtract(nX, 0, nZ);
    }

    private int distanceBetweenPlayers(Player player, Entity entity) {
        Location ninjaLocation = player.getLocation().clone();
        Location entityLocation = entity.getLocation().clone();
        ninjaLocation.setY(0);
        entityLocation.setY(0);
        return (int) ninjaLocation.distanceSquared(entityLocation);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

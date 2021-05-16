package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.LongArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class NinjaKit extends AbstractKit {
    public final static NinjaKit INSTANCE = new NinjaKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @DoubleArg
    private final double radius;
    @LongArg
    private final long lastHitExpiration;

    private NinjaKit() {
        super("Ninja", Material.INK_SAC);
        cooldown = 13F;
        radius = 30D;
        lastHitExpiration = 15L;
    }

    @KitEvent
    @Override
    public void onPlayerIsSneakingEvent(PlayerToggleSneakEvent event, KitPlayer attacker) {
        Player player = event.getPlayer();
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
            if (attacker.getLastHitInformation().getPlayerTimeStamp() + lastHitExpiration * 1000L > System.currentTimeMillis()) {
                if (distanceBetweenPlayers(player, toTeleport) < radius * radius) {
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

    private double distanceBetweenPlayers(Player player, Entity entity) {
        Location ninjaLocation = player.getLocation().clone();
        Location entityLocation = entity.getLocation().clone();
        ninjaLocation.setY(0);
        entityLocation.setY(0);
        return ninjaLocation.distanceSquared(entityLocation);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

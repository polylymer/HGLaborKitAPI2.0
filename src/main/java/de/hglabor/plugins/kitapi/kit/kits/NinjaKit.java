package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class NinjaKit extends AbstractKit {
    private final static NinjaKit instance = new NinjaKit();

    private NinjaKit() {
        super("Ninja", Material.INK_SAC, 13);
    }

    public static NinjaKit getInstance() {
        return instance;
    }

    @Override
    public void onNinjaSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        KitPlayer attacker = KitManager.getInstance().getPlayer(player);
        KitPlayer lastHittedPlayer = attacker.getLastHittedPlayer();
        if (lastHittedPlayer == null) {
            return;
        }
        Player toTeleport = Bukkit.getPlayer(lastHittedPlayer.getUUID());
        if (toTeleport != null) {
            if (!toTeleport.isOnline()) return;
            if (!lastHittedPlayer.isValid()) return;
            if (attacker.getLastHitTimeStamp() + this.getCooldown() * 1000L > System.currentTimeMillis()) {
                //TODO distance check einbauen
                player.teleport(calculateNinjaBehind(toTeleport));
                attacker.activateKitCooldown(this, this.getCooldown());
                attacker.setLastHittedTimeStamp((long) 0);
                attacker.setLastHittedPlayer(null);
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
}

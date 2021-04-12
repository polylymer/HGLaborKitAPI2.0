package de.hglabor.plugins.kitapi.pvp;

import org.bukkit.entity.Player;

import java.util.List;

public class SkyBorder {
    private final int damage;

    public SkyBorder(int damage) {
        this.damage = damage;
    }

    public void tick(List<Player> players) {
        for (Player player : players) {
            if (player.getLocation().getY() >= player.getWorld().getMaxHeight()) {
                player.damage(damage);
            }
        }
    }
}

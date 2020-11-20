package de.hglabor.plugins.kitapi.player;

import org.bukkit.entity.Player;

public interface KitPlayerSupplier {
    KitPlayer getKitPlayer(Player player);
}

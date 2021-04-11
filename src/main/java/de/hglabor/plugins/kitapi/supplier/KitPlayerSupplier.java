package de.hglabor.plugins.kitapi.supplier;

import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.Player;

public interface KitPlayerSupplier {
    KitPlayer getKitPlayer(Player player);

    KitPlayer getRandomAlivePlayer();
}

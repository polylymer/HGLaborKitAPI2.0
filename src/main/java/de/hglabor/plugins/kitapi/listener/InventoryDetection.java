package de.hglabor.plugins.kitapi.listener;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryDetection implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer((Player) event.getWhoClicked());
        Logger.debug(String.format("%s opened inventory", event.getWhoClicked().getName()));
        if (!kitPlayer.isInInventory()) kitPlayer.setInInventory(true);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer((Player) event.getPlayer());
        Logger.debug(String.format("%s closed inventory", event.getPlayer().getName()));
        kitPlayer.setInInventory(false);
    }
}

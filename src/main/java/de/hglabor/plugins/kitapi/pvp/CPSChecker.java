package de.hglabor.plugins.kitapi.pvp;

import de.hglabor.plugins.kitapi.KitApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CPSChecker implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            KitApi.getInstance().getPlayer(event.getPlayer()).addLeftClick(System.currentTimeMillis());
        }
    }
}

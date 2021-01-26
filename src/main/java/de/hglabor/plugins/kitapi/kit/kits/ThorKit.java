package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;

public class ThorKit extends AbstractKit {
    public static final ThorKit INSTANCE = new ThorKit();
    protected ThorKit() {
        super("Thor", Material.WOODEN_AXE, 10);
        setMainKitItem(getDisplayMaterial(),true);
        addEvents(Collections.singletonList(PlayerInteractEvent.class));
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;
        Location location = event.getClickedBlock().getLocation();
        if(!location.getWorld().isClearWeather()) {
            location.getWorld().strikeLightning(location.add(1, 0, 0));
            location.getWorld().strikeLightning(location.add(0, 0, 1));
            location.getWorld().strikeLightning(location.add(1, 0, 1));
        } else {
            location.getWorld().strikeLightning(location);
        }
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(event.getPlayer());
        kitPlayer.activateKitCooldown(this, this.getCooldown());
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class NeoKit extends AbstractKit implements Listener {
    public static final NeoKit INSTANCE = new NeoKit();

    private NeoKit() {
        super("Neo", Material.ENDER_EYE);
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        Entity hitEntity = event.getHitEntity();
        if (!(hitEntity instanceof Player)) return;
        Player player = (Player) hitEntity;
        if (KitApi.getInstance().getPlayer(player).hasKit(this)) {
            event.getEntity().remove();
            event.setCancelled(true);
            player.launchProjectile(event.getEntity().getClass(), event.getEntity().getVelocity().multiply(-1));
        }
    }


}

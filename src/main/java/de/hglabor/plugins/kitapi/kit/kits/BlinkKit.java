package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;

public class BlinkKit extends AbstractKit {
    public static final BlinkKit INSTANCE = new BlinkKit();

    private BlinkKit() {
        super("Blink", Material.NETHER_STAR, 15);
        setMainKitItem(getDisplayMaterial());
        addSetting(KitSettings.USES, 4);
        addSetting(KitSettings.RADIUS, 5);
        addEvents(Collections.singletonList(PlayerInteractEvent.class));
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.teleport(player.getLocation().add(player.getLocation().getDirection().normalize().multiply((Integer) getSetting(KitSettings.RADIUS))));
        player.getLocation().subtract(0, 1, 0).getBlock().setType(Material.OAK_LEAVES);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 100, 100);
        KitManager.getInstance().checkUsesForCooldown(player, this);
    }
}

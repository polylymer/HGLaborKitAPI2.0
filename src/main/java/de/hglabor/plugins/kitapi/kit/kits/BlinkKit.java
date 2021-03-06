package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlinkKit extends AbstractKit {
    public static final BlinkKit INSTANCE = new BlinkKit();
    @IntArg
    private final int maxUses;
    @IntArg
    private final int blinkDistance;

    private BlinkKit() {
        super("Blink", Material.NETHER_STAR, 15);
        setMainKitItem(getDisplayMaterial());
        maxUses = 4;
        blinkDistance = 4;
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.teleport(player.getLocation().add(player.getLocation().getDirection().normalize().multiply(blinkDistance)));
        player.getLocation().subtract(0, 1, 0).getBlock().setType(Material.OAK_LEAVES);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 100, 100);
        KitApi.getInstance().checkUsesForCooldown(player, this, maxUses);
    }
}

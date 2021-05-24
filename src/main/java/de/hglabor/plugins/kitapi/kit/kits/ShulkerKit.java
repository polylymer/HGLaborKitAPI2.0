package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShulkerKit extends AbstractKit {

    public static final ShulkerKit INSTANCE = new ShulkerKit();

    @FloatArg(min = 0.0F)
    private final float cooldown;

    protected ShulkerKit() {
        super("Shulker", Material.SHULKER_SHELL);
        this.cooldown = 7f;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ShulkerBullet shulkerBullet = player.launchProjectile(ShulkerBullet.class, player.getLocation().getDirection().multiply(2));
        KitApi.getInstance().getPlayer(player).activateKitCooldown(this);
    }
}

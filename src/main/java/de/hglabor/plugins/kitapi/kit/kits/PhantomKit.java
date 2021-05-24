package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PhantomKit extends AbstractKit implements Listener {
    public static final PhantomKit INSTANCE = new PhantomKit();

    @FloatArg(min = 0.0F)
    private final float cooldown;
    @FloatArg(min = 0.0F)
    private final float defaultBoost;
    @FloatArg(min = 0.0F)
    private final float inCombatBoost;

    private PhantomKit() {
        super("Phantom", Material.FEATHER);
        setMainKitItem(getDisplayMaterial());
        cooldown = 40;
        defaultBoost = 2.5F;
        inCombatBoost = 1.0F;
    }

    @KitEvent
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(event.getPlayer());
        Player player = event.getPlayer();
        if (player.isInsideVehicle()) return;
        if (kitPlayer.isInCombat()) {
            player.setVelocity(player.getVelocity().setY(inCombatBoost));
            player.sendMessage(Localization.INSTANCE.getMessage("phantom.inCombat", ChatUtils.locale(player)));
        } else {
            player.setVelocity(player.getVelocity().setY(defaultBoost));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(KitApi.getInstance().getPlugin(), () -> player.setGliding(true), 5);
        kitPlayer.activateKitCooldown(this);
    }

    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player entity = (Player) event.getEntity();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(entity);
        if (!kitPlayer.hasKit(this)) {
            return;
        }
        ItemStack chestplate = entity.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType().equals(Material.ELYTRA)) {
            return;
        }
        if (!(event.getEntity().isOnGround())) {
            event.setCancelled(true);
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

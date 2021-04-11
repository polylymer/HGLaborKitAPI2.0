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
        if (kitPlayer.isInCombat()) {
            player.setVelocity(player.getVelocity().setY(inCombatBoost));
            player.sendMessage(Localization.INSTANCE.getMessage("phantom.inCombat", ChatUtils.getPlayerLocale(player)));
        } else {
            player.setVelocity(player.getVelocity().setY(defaultBoost));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(KitApi.getInstance().getPlugin(), () -> player.setGliding(true), 5);
        kitPlayer.activateKitCooldown(this);
    }

    @KitEvent
    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer((Player) event.getEntity());
        if (!kitPlayer.hasKit(this)) return;
        if (!(event.getEntity().isOnGround())) {
            event.setCancelled(true);
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

}

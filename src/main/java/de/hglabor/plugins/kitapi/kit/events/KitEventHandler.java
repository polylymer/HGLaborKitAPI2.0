package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.supplier.KitPlayerSupplier;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class KitEventHandler extends KitEvents {
    protected final KitPlayerSupplier playerSupplier;

    public KitEventHandler(KitPlayerSupplier playerSupplier) {
        this.playerSupplier = playerSupplier;
    }

    public boolean canUseKit(Event event, KitPlayer kitPlayer, AbstractKit kit) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return false;
        }

        if (!kitPlayer.isValid()) {
            return false;
        }

        if (!kit.getKitEvents().contains(event.getClass())) {
            return false;
        }
        //Player doesnt have kit
        if (!kitPlayer.hasKit(kit)) {
            return false;
        }
        //Players kits are disabled
        if (kitPlayer.areKitsDisabled()) {
            player.sendActionBar(Localization.INSTANCE.getMessage("kit.disabled", ChatUtils.getPlayerLocale(player)));
            return false;
        }
        //Player is on kitcooldown
        if (KitApi.getInstance().sendCooldownMessage(kitPlayer, kit)) {
            return false;
        }

        return true;
    }

    public boolean canUseKitItem(Event event, KitPlayer kitPlayer, AbstractKit kit) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return false;
        }

        //Kit has no kititem
        if (kit.getMainKitItem() == null) {
            return false;
        }

        if (!KitApi.getInstance().hasKitItemInAnyHand(player, kit)) {
            return false;
        }

        if (canUseKit(event, kitPlayer, kit)) {
            return true;
        }

        return false;
    }
}

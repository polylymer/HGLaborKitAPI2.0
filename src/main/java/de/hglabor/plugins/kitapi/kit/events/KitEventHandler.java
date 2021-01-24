package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.player.KitPlayerSupplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class KitEventHandler extends KitEvents {
    protected final KitPlayerSupplier playerSupplier;

    public KitEventHandler(KitPlayerSupplier playerSupplier) {
        this.playerSupplier = playerSupplier;
    }

    public boolean canUseKit(KitPlayer kitPlayer, AbstractKit kit) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return false;
        }
        //Player doesnt have kit
        if (!kitPlayer.hasKit(kit)) {
            return false;
        }
        //Players kits are disabled
        if (kitPlayer.areKitsDisabled()) {
            //TODO  player.sendActionBar(Localization.getMessage("kit.disabled", player));
            return false;
        }
        //Player is on kitcooldown
        if (KitManager.getInstance().sendCooldownMessage(kitPlayer,kit)) {
            return false;
        }

        return true;
    }

    public boolean canUseKitItem(KitPlayer kitPlayer, AbstractKit kit) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return false;
        }

        //Kit has no kititem
        if (kit.getMainKitItem() == null) {
            return false;
        }

        if (!KitManager.getInstance().hasKitItemInAnyHand(player, kit)) {
            return false;
        }

        if (canUseKit(kitPlayer, kit)) {
            return true;
        }

        return false;
    }
}

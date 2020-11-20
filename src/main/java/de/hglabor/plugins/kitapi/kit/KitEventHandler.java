package de.hglabor.plugins.kitapi.kit;

import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.player.KitPlayerSupplier;

public abstract class KitEventHandler extends KitEvents {
    protected final KitPlayerSupplier playerSupplier;

    public KitEventHandler(KitPlayerSupplier playerSupplier) {
        this.playerSupplier = playerSupplier;
    }

    public boolean canUseKit(KitPlayer kitPlayer, AbstractKit kit) {
        if (!kitPlayer.hasKit(kit)) return false;
        if (kitPlayer.areKitsDisabled()) {
            //TODO  player.sendActionBar(Localization.getMessage("kit.disabled", player));
            return false;
        }
        return true;
    }
}

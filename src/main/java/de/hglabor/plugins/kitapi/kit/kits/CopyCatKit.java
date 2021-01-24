package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class CopyCatKit extends AbstractKit {
    public final static CopyCatKit INSTANCE = new CopyCatKit();

    private CopyCatKit() {
        super("CopyCat", Material.CAT_SPAWN_EGG);
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        AbstractKit copiedKit = kitPlayer.getKitAttribute(this);
        if (copiedKit != null && copiedKit != this) {
            copiedKit.disable(kitPlayer);
        }
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        AbstractKit copiedKit = kitPlayer.getKitAttribute(this);
        if (copiedKit != null) {
            if (copiedKit.equals(this)) {
                kitPlayer.putKitAttribute(this, SurpriseKit.INSTANCE);
                ((AbstractKit) kitPlayer.getKitAttribute(this)).enable(kitPlayer);
            } else {
                copiedKit.enable(kitPlayer);
            }
        }
    }

    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        AbstractKit oldCopiedKit = killer.getKitAttribute(this);
        AbstractKit newKit = dead.getKits().get(0);
        //DISABLE OLD KIT
        if (oldCopiedKit != null) {
            oldCopiedKit.disable(killer);
            KitManager.getInstance().removeKitItems(oldCopiedKit, Bukkit.getPlayer(killer.getUUID()));
        }

        //CopyCat(NewKit)
        killer.putKitAttribute(this, newKit);

        //ENABLE NEW KIT
        KitManager.getInstance().giveKitItemsIfSlotEmpty(killer, newKit);
        newKit.enable(killer);
    }
}

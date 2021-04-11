package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

public class CopyCatKit extends AbstractKit {
    public final static CopyCatKit INSTANCE = new CopyCatKit();
    private final String copiedKitKey;

    private CopyCatKit() {
        super("CopyCat", Material.CAT_SPAWN_EGG);
        copiedKitKey = this.getName() + "copiedKit";
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        AbstractKit copiedKit = kitPlayer.getKitAttribute(copiedKitKey);
        if (copiedKit != null && copiedKit != this) {
            copiedKit.onDeactivation(kitPlayer);
        }
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        AbstractKit copiedKit = kitPlayer.getKitAttribute(copiedKitKey);
        if (copiedKit != null) {
            if (copiedKit.equals(this)) {
                AbstractKit randomKit = SurpriseKit.INSTANCE.getRandomEnabledKit();
                kitPlayer.putKitAttribute(copiedKitKey, randomKit);
                randomKit.onEnable(kitPlayer);
            } else {
                copiedKit.onEnable(kitPlayer);
            }
        }
    }

    @KitEvent(clazz = PlayerDeathEvent.class)
    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        AbstractKit oldCopiedKit = killer.getKitAttribute(copiedKitKey);
        AbstractKit newKit = dead.getKits().get(0);
        //DISABLE OLD KIT

        if (oldCopiedKit != null) {
            oldCopiedKit.onDeactivation(killer);
            Optional.ofNullable(Bukkit.getPlayer(killer.getUUID())).ifPresent(player -> KitApi.getInstance().removeKitItems(oldCopiedKit, player));
        }

        //CopyCat(NewKit)
        killer.putKitAttribute(copiedKitKey, newKit);

        //ENABLE NEW KIT
        KitApi.getInstance().giveKitItemsIfInvFull(killer, newKit);
        newKit.onEnable(killer);
    }

    public String getKitAttributeKey() {
        return copiedKitKey;
    }
}

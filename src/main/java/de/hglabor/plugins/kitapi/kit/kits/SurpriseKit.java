package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;

import java.util.Random;

public class SurpriseKit extends AbstractKit {
    public final static SurpriseKit INSTANCE = new SurpriseKit();

    private SurpriseKit() {
        super("Surprise", Material.PUFFERFISH);
    }

    @Override
    public void enable(KitPlayer kitplayer) {
        AbstractKit randomKit = NoneKit.getInstance();
        int kitSlot = 0;
        //TODO copycat surprise
        for (AbstractKit kit : kitplayer.getKits()) {
            if (kit.equals(this)) {
                randomKit = getRandomEnabledKit();
                kitplayer.getKits().set(kitSlot, randomKit);
                KitApi.getInstance().giveKitItemsIfSlotEmpty(kitplayer, randomKit);
                randomKit.enable(kitplayer);
            }
            kitSlot++;
        }
    }

    public AbstractKit getRandomEnabledKit() {
        int randomNumber = new Random().nextInt(KitApi.getInstance().getEnabledKits().size());
        int i = 0;
        for (AbstractKit enabledKit : KitApi.getInstance().getEnabledKits()) {
            if (i == randomNumber) {
                return enabledKit;
            }
            i++;
        }
        return NoneKit.getInstance();
    }
}

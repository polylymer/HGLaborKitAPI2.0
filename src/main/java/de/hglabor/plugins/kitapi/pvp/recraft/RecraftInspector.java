package de.hglabor.plugins.kitapi.pvp.recraft;


import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.List;

import static de.hglabor.utils.localization.Localization.t;

public class RecraftInspector {
    private final int maxRecraftAmount;

    public RecraftInspector(int maxRecraftAmount) {
        this.maxRecraftAmount = maxRecraftAmount;
    }

    public void tick(List<Player> players) {
        for (Player player : players) {
            Recraft recraft = new Recraft();
            recraft.calcRecraft(player.getInventory().getContents());
            if (recraft.getRecraftPoints() > maxRecraftAmount) {
                player.sendMessage(t("recraftNerf.tooMuch", ChatUtils.locale(player)));
                while (recraft.getRecraftPoints() > maxRecraftAmount) {
                    recraft.decrease(player, 1);
                }
            }
        }
    }
}

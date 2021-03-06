package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Logger;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class AutomaticKit extends AbstractKit {
    public final static AutomaticKit INSTANCE = new AutomaticKit();
    @DoubleArg(min = 0.1D)
    private final double soupHealValue;

    private AutomaticKit() {
        super("Automatic", Material.MUSHROOM_STEW);
        soupHealValue = 5D;
    }

    @KitEvent
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        Logger.debug(String.format("%s %s", player.getName(), this.getName()));
        Logger.debug(String.format("%s is in inventory? %s", player.getName(), kitPlayer.isInInventory()));
        if (kitPlayer.isInInventory()) return;
        if (player.getHealth() < 14) {
            for (int i = 1; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) continue;
                if (item.getType().equals(Material.MUSHROOM_STEW)) {
                    if ((player.getHealth() + soupHealValue) > 20) {
                        double difference = 20 - player.getHealth();
                        player.setHealth(difference + player.getHealth());
                    } else {
                        player.setHealth(soupHealValue + player.getHealth());
                    }
                    item.setAmount(0);
                    break;
                }
            }
        }
    }
}

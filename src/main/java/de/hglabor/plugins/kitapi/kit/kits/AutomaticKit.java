package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.pvp.SoupHealing;
import de.hglabor.plugins.kitapi.util.Logger;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
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
        if (kitPlayer.isInInventory()) {
            return;
        }
        if (player.getHealth() >= 14) {
            return;
        }

        /*
         * Soup can also be in first slot
         * There are actually people having their sword in
         * another slot
         */
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) {
                continue;
            }
            if (!SoupHealing.SOUP_MATERIAL.contains(item.getType())) {
                continue;
            }
            player.setHealth(Math.min(player.getHealth() + this.soupHealValue
                    , player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            item.setAmount(0);
            break;
        }
    }
}

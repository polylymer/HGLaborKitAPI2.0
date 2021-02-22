package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AutomaticKit extends AbstractKit {
    public final static AutomaticKit INSTANCE = new AutomaticKit();

    private AutomaticKit() {
        super("Automatic", Material.MUSHROOM_STEW);
        addSetting(KitSettings.NUMBER, 5);
        addEvents(List.of(EntityDamageEvent.class));
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        Logger.debug(String.format("%s %s", player.getName(), this.getName()));
        int toHeal = getSetting(KitSettings.NUMBER);
        Logger.debug(String.format("%s is in inventory? %s", player.getName(), kitPlayer.isInInventory()));
        if (kitPlayer.isInInventory()) return;
        if (player.getHealth() < 14) {
            for (int i = 1; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) continue;
                if (item.getType().equals(Material.MUSHROOM_STEW)) {
                    if ((player.getHealth() + toHeal) > 20) {
                        double difference = 20 - player.getHealth();
                        player.setHealth(difference + player.getHealth());
                    } else {
                        player.setHealth(toHeal + player.getHealth());
                    }
                    item.setAmount(0);
                    break;
                }
            }
        }
    }
}

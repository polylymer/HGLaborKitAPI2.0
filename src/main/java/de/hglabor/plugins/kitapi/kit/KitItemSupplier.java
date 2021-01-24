package de.hglabor.plugins.kitapi.kit;

import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface KitItemSupplier {
    void giveKitItems(KitPlayer kitPlayer, AbstractKit kit);

    void giveItems(KitPlayer kitPlayer, List<ItemStack> items);
}

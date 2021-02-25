package de.hglabor.plugins.kitapi.supplier;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface KitItemSupplier {
    void giveKitItems(KitPlayer kitPlayer, AbstractKit kit);

    void giveKitItems(KitPlayer kitPlayer, AbstractKit kit, List<ItemStack> items);

    void giveItems(KitPlayer kitPlayer, List<ItemStack> items);

    void giveKitItemsDirectly(KitPlayer kitPlayer, AbstractKit kit);
}

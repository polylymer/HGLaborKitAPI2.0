package de.hglabor.plugins.kitapi.kit.items;

import org.bukkit.inventory.ItemStack;

public class KitItemAction {
    private final ItemStack item;
    private final String localizationKey;

    public KitItemAction(ItemStack item, String localizationKey) {
        this.item = item;
        this.localizationKey = localizationKey;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getLocalizationKey() {
        return localizationKey;
    }
}

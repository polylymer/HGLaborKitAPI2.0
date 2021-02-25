package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */

public class GrandpaKit extends AbstractKit {
    public static final GrandpaKit INSTANCE = new GrandpaKit();

    protected GrandpaKit() {
        super("Grandpa", Material.STICK);
        ItemStack kitItem = new ItemStack(Material.STICK);
        kitItem.addEnchantment(Enchantment.KNOCKBACK,2);
        setMainKitItem(kitItem);
    }
}

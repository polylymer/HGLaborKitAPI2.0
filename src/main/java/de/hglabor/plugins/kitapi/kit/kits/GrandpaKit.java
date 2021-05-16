package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */
public class GrandpaKit extends AbstractKit {
    public static final GrandpaKit INSTANCE = new GrandpaKit();

    private GrandpaKit() {
        super("Grandpa", new ItemBuilder(Material.STICK).setEnchantment(Enchantment.KNOCKBACK, 2).build());
        setMainKitItem(new ItemBuilder(Material.STICK).setEnchantment(Enchantment.KNOCKBACK, 2).build());
    }
}

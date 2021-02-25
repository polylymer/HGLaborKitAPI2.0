package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */

public class BarbarianKit extends AbstractKit {
    public static final BarbarianKit INSTANCE = new BarbarianKit();

    HashMap<UUID, Integer> playerBarbarianLevel = new HashMap<UUID, Integer>();

    private BarbarianKit() {

        super("Barbarian", Material.WOODEN_SWORD);

        ItemStack kitItem = new ItemStack(Material.WOODEN_SWORD);
        kitItem.addEnchantment(Enchantment.DURABILITY, 3);
        ItemMeta kitItemMeta = kitItem.getItemMeta();
        kitItemMeta.setUnbreakable(true);
        kitItemMeta.setLocalizedName("Barbarian");
        kitItem.setItemMeta(kitItemMeta);

        setMainKitItem(kitItem);

        addEvents(Collections.singletonList(PlayerDeathEvent.class));
    }

    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        if (!killer.hasKit(this)) return;
        if (!Bukkit.getPlayer(killer.getUUID()).getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals(getMainKitItem().getItemMeta().getLocalizedName()))
            return;
        if (!playerBarbarianLevel.containsKey(killer.getUUID())) playerBarbarianLevel.put(killer.getUUID(), 1);
        else playerBarbarianLevel.put(killer.getUUID(), playerBarbarianLevel.get(killer.getUUID()) + 1);
        Player p = Bukkit.getPlayer(killer.getUUID());
        switch (playerBarbarianLevel.get(killer.getUUID())) {
            case 1:
                p.getInventory().getItemInMainHand().setType(Material.STONE_SWORD);
                break;
            case 2:
                p.getInventory().getItemInMainHand().setType(Material.WOODEN_SWORD);
                p.getInventory().getItemInMainHand().addEnchantment(Enchantment.DAMAGE_ALL, 1);
                break;
            case 3:
                p.getInventory().getItemInMainHand().removeEnchantment(Enchantment.DAMAGE_ALL);
                p.getInventory().getItemInMainHand().setType(Material.IRON_SWORD);
                break;
            case 4:
                p.getInventory().getItemInMainHand().setType(Material.STONE_SWORD);
                p.getInventory().getItemInMainHand().addEnchantment(Enchantment.DAMAGE_ALL, 2);
                break;
            case 5:
                p.getInventory().getItemInMainHand().addEnchantment(Enchantment.FIRE_ASPECT, 1);
                break;
            case 6:
                p.getInventory().getItemInMainHand().removeEnchantment(Enchantment.FIRE_ASPECT);
                p.getInventory().getItemInMainHand().removeEnchantment(Enchantment.DAMAGE_ALL);
                p.getInventory().getItemInMainHand().setType(Material.DIAMOND_SWORD);
                break;
            case 7:
                p.getInventory().getItemInMainHand().addEnchantment(Enchantment.DAMAGE_ALL, 1);
                break;
            case 8:
                p.getInventory().getItemInMainHand().addEnchantment(Enchantment.DAMAGE_ALL, 2);
                break;
            case 9:
                p.getInventory().getItemInMainHand().addEnchantment(Enchantment.DAMAGE_ALL, 3);
                break;
        }

    }
}

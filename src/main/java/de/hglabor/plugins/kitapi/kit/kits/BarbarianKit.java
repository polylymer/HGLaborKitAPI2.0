package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */
public class BarbarianKit extends AbstractKit {
    public static final BarbarianKit INSTANCE = new BarbarianKit();

    private final Map<UUID, Integer> playerBarbarianLevel;

    private BarbarianKit() {
        super("Barbarian", Material.WOODEN_SWORD);
        playerBarbarianLevel = new HashMap<>();
        setMainKitItem(new ItemBuilder(Material.WOODEN_SWORD).setLocalizedName(this.getName()).setUnbreakable(true).build());
        addEvents(Collections.singletonList(PlayerDeathEvent.class));
    }

    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        Player player = Bukkit.getPlayer(killer.getUUID());
        UUID killerUUID = killer.getUUID();
        if (player == null) {
            return;
        }
        ItemStack barbarianSword = player.getInventory().getItemInMainHand();
        if (!isKitItem(barbarianSword)) {
            return;
        }
        if (!playerBarbarianLevel.containsKey(killerUUID)) playerBarbarianLevel.put(killerUUID, 1);
        else playerBarbarianLevel.put(killerUUID, playerBarbarianLevel.get(killerUUID) + 1);
        //TODO KitAttribute and dynamic values
        switch (playerBarbarianLevel.get(killerUUID)) {
            case 1:
                barbarianSword.setType(Material.STONE_SWORD);
                break;
            case 2:
                barbarianSword.setType(Material.WOODEN_SWORD);
                barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                break;
            case 3:
                barbarianSword.removeEnchantment(Enchantment.DAMAGE_ALL);
                barbarianSword.setType(Material.IRON_SWORD);
                break;
            case 4:
                barbarianSword.setType(Material.STONE_SWORD);
                barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                break;
            case 5:
                barbarianSword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
                break;
            case 6:
                barbarianSword.removeEnchantment(Enchantment.FIRE_ASPECT);
                barbarianSword.removeEnchantment(Enchantment.DAMAGE_ALL);
                barbarianSword.setType(Material.DIAMOND_SWORD);
                break;
            case 7:
                barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                break;
            case 8:
                barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                break;
            case 9:
                barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
                break;
        }
    }

    @Override
    public boolean isKitItem(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasLocalizedName()) {
                return itemMeta.getLocalizedName().equalsIgnoreCase(this.getName());
            }
        }
        return false;
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */
public class BarbarianKit extends AbstractKit {
    public static final BarbarianKit INSTANCE = new BarbarianKit();
    @IntArg
    private final int moduloSteps;
    private final String killsKey;
    private final String levelKey;

    private BarbarianKit() {
        super("Barbarian", Material.WOODEN_SWORD);
        moduloSteps = 1;
        killsKey = this.getName() + "kills";
        levelKey = this.getName() + "level";
        setMainKitItem(new ItemBuilder(Material.WOODEN_SWORD).setLocalizedName(this.getName()).setUnbreakable(true).build());
    }

    @KitEvent(clazz = PlayerDeathEvent.class)
    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        Player player = Bukkit.getPlayer(killer.getUUID());
        if (player == null) {
            return;
        }
        ItemStack barbarianSword = player.getInventory().getItemInMainHand();
        if (!isKitItem(barbarianSword)) {
            return;
        }
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        kitPlayer.putKitAttribute(killsKey, kitPlayer.getKitAttributeOrDefault(killsKey, 0) + 1);
        if ((int) kitPlayer.getKitAttribute(killsKey) % moduloSteps == 0) {
            switch (kitPlayer.getKitAttributeOrDefault(levelKey, 1)) {
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
                    barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    break;
                case 5:
                    barbarianSword.setType(Material.DIAMOND_SWORD);
                    break;
                case 6:
                    barbarianSword.setType(Material.IRON_SWORD);
                    barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    break;
                case 7:
                    barbarianSword.setType(Material.DIAMOND_SWORD);
                    barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    break;
                case 8:
                    barbarianSword.setType(Material.DIAMOND_SWORD);
                    barbarianSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                    break;
                case 9:
                    barbarianSword.setType(Material.DIAMOND_SWORD);
                    barbarianSword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
                    break;
            }
            kitPlayer.putKitAttribute(levelKey, kitPlayer.getKitAttributeOrDefault(levelKey, 1) + 1);
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

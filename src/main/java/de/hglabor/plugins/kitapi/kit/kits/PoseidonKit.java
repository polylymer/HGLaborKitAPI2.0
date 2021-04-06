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
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PoseidonKit extends AbstractKit {

    public static final PoseidonKit INSTANCE = new PoseidonKit();

    @IntArg
    private final int rainTime;
    private final String isInWaterKey;

    private PoseidonKit() {
        super("Poseidon", Material.TRIDENT);
        setMainKitItem(new ItemBuilder(getMainKitItem()).setEnchantment(Enchantment.LOYALTY, 3).setUnbreakable(true).build());
        rainTime = 10;
        isInWaterKey = this.getName() + "isInWater";
    }

    @KitEvent
    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {
        Player player = event.getPlayer();

        if (kitPlayer.getKitAttributeOrDefault(isInWaterKey, false)) setRainEnchantments(getMainKitItem()); // nicht sicher ob das besonders effizient ist bitte besser machen wenn geht
        else setNormalEnchantments(getMainKitItem());

        if (player.getWorld().hasStorm()) {
            kitPlayer.putKitAttribute(isInWaterKey, true);
            return;
        }

        Material eyeLocationBlock = player.getEyeLocation().getBlock().getType();
        Material locationBlock = player.getLocation().getBlock().getType();

        if (eyeLocationBlock == Material.WATER || locationBlock == Material.WATER) kitPlayer.putKitAttribute(isInWaterKey, true);
        else kitPlayer.putKitAttribute(isInWaterKey, false);
    }

    @KitEvent
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer victim) {
        if (killer.getBukkitPlayer().isEmpty()) return;
        Player player = killer.getBukkitPlayer().get();
        boolean weatherClearBefore = player.getWorld().isClearWeather();
        player.getWorld().setStorm(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(KitApi.getInstance().getPlugin(), () -> player.getWorld().setStorm(!weatherClearBefore), rainTime * 20L);
    }


    private void setRainEnchantments(ItemStack trident) {
        if (trident.getType() != Material.TRIDENT) return;
        ItemMeta tridentMeta = trident.getItemMeta();
        if (tridentMeta == null) return;
        clearEnchants(tridentMeta);
        tridentMeta.addEnchant(Enchantment.RIPTIDE, 3, true);
        trident.setItemMeta(tridentMeta);
    }

    private void setNormalEnchantments(ItemStack trident) {
        if (trident.getType() != Material.TRIDENT) return;
        ItemMeta tridentMeta = trident.getItemMeta();
        if (tridentMeta == null) return;
        clearEnchants(tridentMeta);
        tridentMeta.addEnchant(Enchantment.LOYALTY, 3, true);
        trident.setItemMeta(tridentMeta);
    }

    private void clearEnchants(ItemMeta itemMeta) {
        if (itemMeta == null) return;
        if (itemMeta.hasEnchants()) {
            for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                itemMeta.removeEnchant(enchantment);
            }
        }
    }
}

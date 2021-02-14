package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.supplier.KitPlayerSupplier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class KitItemHandler implements Listener {
    protected final KitPlayerSupplier playerSupplier;

    public KitItemHandler() {
        this.playerSupplier = KitApi.getInstance().getPlayerSupplier();
    }

    @EventHandler
    public void disableHandSwapForOffHandKits(PlayerSwapHandItemsEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            if (kit.isUsingOffHand()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void disableOffHandInventoryClick(InventoryClickEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getWhoClicked());
        if (event.getRawSlot() == 45) {
            for (AbstractKit kit : kitPlayer.getKits()) {
                if (kit.isUsingOffHand()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void cancelKitItemPlace(BlockPlaceEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            for (ItemStack kitItem : kit.getKitItems()) {
                if (event.getItemInHand().isSimilar(kitItem)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void cancelKitItemDrop(PlayerDropItemEvent event) {
        KitPlayer KitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : KitPlayer.getKits()) {
            for (ItemStack kitItem : kit.getKitItems()) {
                if (kitItem.isSimilar(event.getItemDrop().getItemStack())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void avoidKitItemDropOnPlayerDeath(ItemSpawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        for (AbstractKit enabledKit : KitApi.getInstance().getEnabledKits()) {
            for (ItemStack kitItem : enabledKit.getKitItems()) {
                if (kitItem.isSimilar(itemStack)) {
                    itemStack.setType(Material.AIR);
                    event.getEntity().remove();
                }
            }
        }
    }
}

package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.supplier.KitPlayerSupplier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
        for (AbstractKit kit : kitPlayer.getKits())
            if (kit.isUsingOffHand())
                event.setCancelled(true);
    }

    @EventHandler
    public void disableOffHandInventoryClick(InventoryClickEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getWhoClicked());
        if (event.getRawSlot() != 45)
            return;
        for (AbstractKit kit : kitPlayer.getKits())
            if (kit.isUsingOffHand())
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            if (kit.isKitItemPlaceable())
                continue;
            for (ItemStack kitItem : kit.getKitItems())
                if (event.getItem().isSimilar(kitItem) || kit.isKitItem(event.getItem()))
                    event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelKitItemPlace(BlockPlaceEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            if (kit.isKitItemPlaceable())
                continue;
            for (ItemStack kitItem : kit.getKitItems())
                if (event.getItemInHand().isSimilar(kitItem) || kit.isKitItem(event.getItemInHand()))
                    event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelKitItemDrop(PlayerDropItemEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        for (AbstractKit kit : kitPlayer.getKits())
            for (ItemStack kitItem : kit.getKitItems())
                if (kitItem.isSimilar(droppedItem) || kit.isKitItem(droppedItem))
                    event.setCancelled(true);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getWhoClicked());
        for (ItemStack ingridient : event.getInventory().getMatrix()) {
            if (ingridient == null) continue;
            for (AbstractKit kit : kitPlayer.getKits())
                for (ItemStack kitItem : kit.getKitItems())
                    if (kitItem.isSimilar(ingridient) || kit.isKitItem(ingridient))
                        event.setCancelled(true);
        }
    }

    @EventHandler
    public void avoidKitItemDropOnPlayerDeath(ItemSpawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        for (AbstractKit kit : KitApi.getInstance().getEnabledKits()) {
            for (ItemStack kitItem : kit.getKitItems()) {
                if (kitItem.isSimilar(itemStack) || kit.isKitItem(itemStack)) {
                    itemStack.setType(Material.AIR);
                    itemStack.setAmount(0);
                    event.getEntity().remove();
                }
            }
        }
    }
}

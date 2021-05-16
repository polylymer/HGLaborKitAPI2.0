package de.hglabor.plugins.kitapi.kit.items;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.supplier.KitPlayerSupplier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
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
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            if (kit.isKitItemPlaceable())
                continue;
            if (kit.isKitItem(event.getItem()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelKitItemPlace(BlockPlaceEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            if (kit.isKitItemPlaceable())
                continue;
            if (kit.isKitItem(event.getItemInHand()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelKitItemDrop(PlayerDropItemEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        for (AbstractKit kit : kitPlayer.getKits())
            if (kit.isKitItem(droppedItem))
                event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !clickedInventory.getType().equals(InventoryType.GRINDSTONE)) {
            return;
        }
        if (event.getSlot() != 2) {
            return;
        }
        for (int i = 0; i <= 1; i++) {
            ItemStack item = clickedInventory.getItem(i);
            if (item == null || !item.hasItemMeta()) {
                continue;
            }
            if (KitApi.getInstance().getAllKits().stream().anyMatch(kit -> kit.isKitItem(item))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        for (ItemStack ingridient : event.getInventory().getMatrix()) {
            if (ingridient == null) continue;
            //TODO I dont know if this could lag?
            if (ingridient.hasItemMeta() && KitApi.getInstance().getAllKits().stream().anyMatch(kit -> kit.isKitItem(ingridient))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void avoidKitItemDropOnPlayerDeath(ItemSpawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        for (AbstractKit kit : KitApi.getInstance().getEnabledKits()) {
            if (kit.isKitItem(itemStack)) {
                itemStack.setType(Material.AIR);
                itemStack.setAmount(0);
                event.getEntity().remove();
            }
        }
    }
}

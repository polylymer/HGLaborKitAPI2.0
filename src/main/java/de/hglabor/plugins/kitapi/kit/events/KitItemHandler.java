package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.player.KitPlayerSupplier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public abstract class KitItemHandler {
    protected final KitPlayerSupplier playerSupplier;

    public KitItemHandler(KitPlayerSupplier playerSupplier) {
        this.playerSupplier = playerSupplier;
    }

    public void disableHandSwapForOffHandKits(PlayerSwapHandItemsEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            if (kit.isUsingOffHand()) {
                event.setCancelled(true);
            }
        }
    }

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

    public void cancelKitItemPlace(BlockPlaceEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        for (AbstractKit kit : kitPlayer.getKits()) {
            for (ItemStack kitItem : kit.getKitItems()) {
                if (event.getItemInHand().isSimilar(kitItem)) {
                    if (!kit.isPlaceable()) {
                        event.setCancelled(true);
                    } else {
                        event.setCancelled(kitPlayer.hasKitCooldown(kit));
                    }
                }
            }
        }
    }

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

    public void avoidKitItemDropOnPlayerDeath(ItemSpawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        for (AbstractKit enabledKit : KitManager.getInstance().getEnabledKits()) {
            for (ItemStack kitItem : enabledKit.getKitItems()) {
                if (kitItem.isSimilar(itemStack)) {
                    itemStack.setType(Material.AIR);
                    event.getEntity().remove();
                }
            }
        }
    }
}

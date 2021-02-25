package de.hglabor.plugins.kitapi.supplier;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class KitItemSupplierImpl implements KitItemSupplier {
    public final static KitItemSupplierImpl INSTANCE = new KitItemSupplierImpl();

    private KitItemSupplierImpl() {
    }


    @Override
    public void giveKitItems(KitPlayer kitPlayer, AbstractKit abstractKit) {
        addKitItemsToInventory(kitPlayer, abstractKit);
    }

    @Override
    public void giveKitItems(KitPlayer kitPlayer, AbstractKit abstractKit, List<ItemStack> list) {
        addKitItemsToInventory(kitPlayer, abstractKit, list);
    }

    @Override
    public void giveItems(KitPlayer kitPlayer, List<ItemStack> list) {
        addItemsToInventory(kitPlayer, list);
    }

    @Override
    public void giveKitItemsDirectly(KitPlayer kitPlayer, AbstractKit kit) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player != null) if (kit.isUsingOffHand()) {
            player.getInventory().setItemInOffHand(kit.getMainKitItem());
        } else {
            kit.getKitItems().forEach(kitItem -> player.getInventory().addItem(kitItem));
        }
    }

    private void addKitItemsToInventory(KitPlayer kitPlayer, AbstractKit kit) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return;
        }
        List<ItemStack> kitItems = new ArrayList<>(kit.getKitItems());

        //EDGECASE FÜR REVIVE
        if (kit.isUsingOffHand()) {
            player.getInventory().setItemInOffHand(kit.getMainKitItem());
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!kitPlayer.isValid() || !player.isValid() || !kitPlayer.hasKit(kit)) {
                    cancel();
                    return;
                }
                while (player.getInventory().firstEmpty() != -1) {
                    if (kitItems.size() > 0) {
                        if (!player.getInventory().contains(kitItems.get(0))) {
                            player.getInventory().addItem(kitItems.get(0));
                        }
                        kitItems.remove(0);
                    } else {
                        break;
                    }
                }
                if (kitItems.isEmpty()) {
                    cancel();
                }
            }
        }.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
    }

    private void addKitItemsToInventory(KitPlayer kitPlayer, AbstractKit kit, List<ItemStack> extraItems) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return;
        }
        List<ItemStack> kitItems = new ArrayList<>(kit.getKitItems());
        kitItems.addAll(extraItems);

        //EDGECASE FÜR REVIVE
        if (kit.isUsingOffHand()) {
            player.getInventory().setItemInOffHand(kit.getMainKitItem());
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!kitPlayer.isValid() || !player.isValid() || !kitPlayer.hasKit(kit)) {
                    cancel();
                    return;
                }
                while (player.getInventory().firstEmpty() != -1) {
                    if (kitItems.size() > 0) {
                        if (!player.getInventory().contains(kitItems.get(0))) {
                            player.getInventory().addItem(kitItems.get(0));
                        }
                        kitItems.remove(0);
                    } else {
                        break;
                    }
                }
                if (kitItems.isEmpty()) {
                    cancel();
                }
            }
        }.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
    }

    private void addItemsToInventory(KitPlayer kitPlayer, List<ItemStack> itemStacks) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return;
        }
        List<ItemStack> itemsToGive = new ArrayList<>(itemStacks);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isValid() || !kitPlayer.isValid()) {
                    cancel();
                    return;
                }
                while (player.getInventory().firstEmpty() != -1) {
                    if (itemsToGive.size() > 0) {
                        player.getInventory().addItem(itemsToGive.get(0));
                        itemsToGive.remove(0);
                    } else {
                        break;
                    }
                }
                if (itemsToGive.isEmpty()) {
                    cancel();
                }
            }
        }.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
    }
}

package de.hglabor.plugins.kitapi.kit.selector;

import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.util.ItemBuilder;
import de.hglabor.plugins.kitapi.util.Localization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class KitSelector {
    protected List<ItemStack> kitSelectorItems;
    protected Map<Locale, List<Inventory>> kitPages;
    protected final int MAX_AMOUNT_OF_KITS = 35;
    protected final String kitSelectorTitle = "KitSelector";
    protected final ItemStack LAST_PAGE_ITEM = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "<-").build();
    protected final ItemStack NEXT_PAGE_ITEM = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName(ChatColor.GREEN + "->").build();

    public KitSelector() {
        this.kitPages = new HashMap<>();
        this.kitSelectorItems = new ArrayList<>();
    }

    protected abstract void onKitSelectorClick(PlayerInteractEvent event);

    protected abstract void onInventoryClick(InventoryClickEvent event);

    public void register() {
        kitSelectorItems.clear();
        for (int i = 0; i < KitApiConfig.getInstance().getInteger("kit.amount"); i++) {
            kitSelectorItems.add(i, new ItemBuilder(Material.CHEST).setName("KitSelector " + (i + 1)).build());
        }
        this.createKitPages();
    }

    protected boolean isKitSelectorItem(ItemStack itemStack) {
        return kitSelectorItems.stream().anyMatch(itemStack::isSimilar);
    }

    public void createKitPages() {
        this.resetKitPages();
        for (Locale language : Localization.getSupportedLanguages()) {
            int LAST_PAGE_SLOT = 18;
            int NEXT_PAGE_SLOT = 26;
            for (int i = 0; i < pageAmount(); i++) {
                Inventory kitSelectorPage = Bukkit.createInventory(null, 45, kitSelectorTitle + " " + (i + 1));
                int inventorySlot = 1;
                int start = i * MAX_AMOUNT_OF_KITS;
                int end = Math.min(i * MAX_AMOUNT_OF_KITS + MAX_AMOUNT_OF_KITS, KitManager.getInstance().getEnabledKits().size());
                for (int j = start; j < end; j++) {
                    inventorySlot = inventorySlotNumber(inventorySlot);
                    AbstractKit kit = KitManager.getInstance().getAlphabeticallyKit(j);
                    kitSelectorPage.setItem(inventorySlot, kit.getDisplayItem(language));
                    inventorySlot++;
                }
                kitSelectorPage.setItem(LAST_PAGE_SLOT, LAST_PAGE_ITEM);
                kitSelectorPage.setItem(NEXT_PAGE_SLOT, NEXT_PAGE_ITEM);
                List<Inventory> kitPages = this.kitPages.get(language);
                kitPages.add(kitSelectorPage);
            }
            if (pageAmount() == 0) {
                Inventory kitSelectorPage = Bukkit.createInventory(null, 45, kitSelectorTitle + " " + (1));
                kitSelectorPage.setItem(LAST_PAGE_SLOT, LAST_PAGE_ITEM);
                kitSelectorPage.setItem(NEXT_PAGE_SLOT, NEXT_PAGE_ITEM);
                List<Inventory> kitPages = this.kitPages.get(language);
                kitPages.add(kitSelectorPage);
            }
        }
    }

    public Inventory getPage(int index, Locale locale) {
        return (index >= 0) && (index < kitPages.get(locale).size()) ? kitPages.get(locale).get(index) : null;
    }

    private int pageAmount() {
        int enabledKits = KitManager.getInstance().getEnabledKits().size();
        int safeAmount = enabledKits / MAX_AMOUNT_OF_KITS;
        int rest = enabledKits % MAX_AMOUNT_OF_KITS;
        if (rest > 0) safeAmount++;
        return safeAmount;
    }

    private void resetKitPages() {
        kitPages.clear();
        Localization.getSupportedLanguages().forEach(supportedLanguage -> kitPages.put(supportedLanguage, new ArrayList<>()));
    }

    private int inventorySlotNumber(int slot) {
        switch (slot) {
            case 8:
                return 10;
            case 17:
                return 19;
            case 26:
                return 28;
            case 35:
                return 37;
            default:
                break;
        }
        return slot;
    }

    protected void openFirstPage(Player player) {
        Inventory page = getPage(0, Localization.getPlayerLocale(player.getUniqueId()));
        if (page != null) {
            player.openInventory(page);
        }
    }

    protected boolean nextPage(String title, ItemStack clickedItem, Player player) {
        if (clickedItem.isSimilar(NEXT_PAGE_ITEM)) {
            String pageNumber = title.substring(title.length() - 1);
            Inventory page = getPage(Integer.parseInt(pageNumber), Localization.getPlayerLocale(player.getUniqueId()));
            if (page != null) {
                player.openInventory(page);
            }
            return true;
        }
        return false;
    }

    protected boolean lastPage(String title, ItemStack clickedItem, Player player) {
        if (clickedItem.isSimilar(LAST_PAGE_ITEM)) {
            String pageNumber = title.substring(title.length() - 1);
            Inventory page = getPage(Integer.parseInt(pageNumber) - 1, Localization.getPlayerLocale(player.getUniqueId()));
            if (page != null) {
                player.openInventory(page);
            }
            return true;
        }
        return false;
    }

    public List<ItemStack> getKitSelectorItems() {
        return kitSelectorItems;
    }
}

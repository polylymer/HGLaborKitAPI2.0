package de.hglabor.plugins.kitapi.kit.selector;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class KitSelector implements Listener {
    protected final int MAX_AMOUNT_OF_KITS;
    protected final String KIT_SELECTOR_TITLE;
    protected final ItemStack LAST_PAGE_ITEM;
    protected final ItemStack NEXT_PAGE_ITEM;
    protected List<ItemStack> kitSelectorItems;
    protected Map<Locale, List<Inventory>> kitPages;

    public KitSelector(String kitSelectorTitle) {
        this.KIT_SELECTOR_TITLE = kitSelectorTitle;
        this.MAX_AMOUNT_OF_KITS = 35;
        this.LAST_PAGE_ITEM = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "<-").build();
        this.NEXT_PAGE_ITEM = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName(ChatColor.GREEN + "->").build();
        this.kitPages = new HashMap<>();
        this.kitSelectorItems = new ArrayList<>();
    }

    protected abstract void onKitSelectorClick(PlayerInteractEvent event);

    protected abstract void onInventoryClick(InventoryClickEvent event);

    public void load() {
        kitSelectorItems.clear();
        for (int i = 0; i < KitApiConfig.getInstance().getKitAmount(); i++) {
            kitSelectorItems.add(i, new ItemBuilder(Material.CHEST).setName(KIT_SELECTOR_TITLE + " " + (i + 1)).build());
        }
        kitPages.clear();
        KitApi.getInstance().getSupportedLanguages().forEach(supportedLanguage -> kitPages.put(supportedLanguage, new ArrayList<>()));
        this.createKitPages();
    }

    protected boolean isKitSelectorItem(ItemStack itemStack) {
        return kitSelectorItems.stream().anyMatch(itemStack::isSimilar);
    }

    private void createKitPages() {
        for (Locale language : KitApi.getInstance().getSupportedLanguages()) {
            int LAST_PAGE_SLOT = 18;
            int NEXT_PAGE_SLOT = 26;
            for (int i = 0; i < pageAmount(); i++) {
                Inventory kitSelectorPage = Bukkit.createInventory(null, 45, KIT_SELECTOR_TITLE + " " + (i + 1));
                int inventorySlot = 1;
                int start = i * MAX_AMOUNT_OF_KITS;
                int end = Math.min(i * MAX_AMOUNT_OF_KITS + MAX_AMOUNT_OF_KITS, KitApi.getInstance().getEnabledKits().size());
                for (int j = start; j < end; j++) {
                    inventorySlot = inventorySlotNumber(inventorySlot);
                    AbstractKit kit = KitApi.getInstance().getAlphabeticallyKit(j);
                    kitSelectorPage.setItem(inventorySlot, kit.getDisplayItem(language));
                    inventorySlot++;
                }
                kitSelectorPage.setItem(LAST_PAGE_SLOT, LAST_PAGE_ITEM);
                kitSelectorPage.setItem(NEXT_PAGE_SLOT, NEXT_PAGE_ITEM);
                List<Inventory> kitPages = this.kitPages.get(language);
                kitPages.add(kitSelectorPage);
            }
            if (pageAmount() == 0) {
                Inventory kitSelectorPage = Bukkit.createInventory(null, 45, KIT_SELECTOR_TITLE + " " + (1));
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
        int enabledKits = KitApi.getInstance().getEnabledKits().size();
        int safeAmount = enabledKits / MAX_AMOUNT_OF_KITS;
        int rest = enabledKits % MAX_AMOUNT_OF_KITS;
        if (rest > 0) safeAmount++;
        return safeAmount;
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
        Inventory page = getPage(0, ChatUtils.locale(player.getUniqueId()));
        if (page != null) {
            player.openInventory(page);
        }
    }

    protected boolean nextPage(String title, ItemStack clickedItem, Player player) {
        if (clickedItem.isSimilar(NEXT_PAGE_ITEM)) {
            String pageNumber = title.substring(title.length() - 1);
            Inventory page = getPage(Integer.parseInt(pageNumber), ChatUtils.locale(player));
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
            Inventory page = getPage(Integer.parseInt(pageNumber) - 1 - 1, ChatUtils.locale(player));
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

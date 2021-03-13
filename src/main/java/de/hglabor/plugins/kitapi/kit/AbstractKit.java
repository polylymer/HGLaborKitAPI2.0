package de.hglabor.plugins.kitapi.kit;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.kit.events.KitEvents;
import de.hglabor.plugins.kitapi.kit.settings.BoolArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class AbstractKit extends KitEvents {
    private static final String KIT_ITEM_DESC = ChatColor.RED + "Kititem";
    private final String name;
    /**
     * Some kits get items which are placeable for everyone
     * e.g. Redstoner
     */
    private final List<ItemStack> additionalKitItems;
    /**
     * Used for customizing specific values of a kit
     * e.g. likelihood, cooldown, radius
     */
    private final Map<KitSettings, Object> settings;
    /**
     * Each language has a different itemstack
     */
    private final Map<Locale, ItemStack> displayItems;
    /**
     * Register the events the kit is using,
     * so the KitEventHandler can handle them
     */
    private final Set<Class<? extends Event>> kitEvents;
    private ItemStack mainKitItem;
    /**
     * use this to toggle gamemode specific kits
     */
    @BoolArg
    private boolean isEnabled = true;

    /**
     * enable this to activate a kit in a specific phase
     */
    @BoolArg
    private boolean isUsable = true;

    /**
     * Edgecase since Revive is using Offhand -> conflict with giving kititems
     */
    private boolean usesOffHand;

    protected AbstractKit(String name, Material material) {
        this(name, new ItemStack(material));
    }

    protected AbstractKit(String name, Material material, List<ItemStack> additionalKitItems) {
        this(name, new ItemStack(material));
        this.additionalKitItems.addAll(additionalKitItems);
    }

    protected AbstractKit(String name, ItemStack displayItem) {
        this.name = name;
        this.settings = new HashMap<>();
        this.displayItems = new HashMap<>();
        this.kitEvents = new HashSet<>();
        this.additionalKitItems = new ArrayList<>();
        this.setDisplayItem(displayItem);
    }

    /**
     * some kits don't require an eventlistener so you can activate them here
     */
    public void enable(KitPlayer kitPlayer) {
    }

    /**
     * some kits don't require an eventlistener so you can activate disable them here
     * also used for kit rogue which disables all kits
     */
    public void disable(KitPlayer kitPlayer) {
    }

    public Material getDisplayMaterial() {
        return displayItems.get(Locale.ENGLISH).getType();
    }

    public ItemStack getDisplayItem(Locale locale) {
        return displayItems.getOrDefault(locale, new ItemStack(Material.AIR));
    }

    /**
     * kititem which will be shown in the kitselector
     */
    protected final void setDisplayItem(ItemStack item) {
        for (Locale supportedLanguage : KitApi.getInstance().getSupportedLanguages()) {
            String[] description = Localization.INSTANCE.getMessage(name.toLowerCase() + "." + "description", supportedLanguage).split("#");
            displayItems.put(supportedLanguage, new ItemBuilder(item.clone()).setName(ChatColor.RED + name).setDescription(description).build());
        }
    }

    public void setMainKitItem(Material material, boolean unbreakable) {
        mainKitItem = new ItemBuilder(material).setDescription(KIT_ITEM_DESC).setUnbreakable(unbreakable).build();
    }

    public void setMainKitItem(Material material, int size) {
        mainKitItem = new ItemBuilder(material).setDescription(KIT_ITEM_DESC).setAmount(size).build();
    }

    public void setMainKitItem(ItemStack item, int size) {
        mainKitItem = new ItemBuilder(item.clone()).setDescription(KIT_ITEM_DESC).setAmount(size).build();
    }

    public void setMainKitItem(Material material, String name) {
        mainKitItem = new ItemBuilder(material).setDescription(KIT_ITEM_DESC).setName(ChatColor.BLUE + name).build();
    }

    public ItemStack getMainKitItem() {
        return mainKitItem;
    }

    public void setMainKitItem(Material material) {
        mainKitItem = new ItemBuilder(material).setDescription(KIT_ITEM_DESC).build();
    }

    public void setMainKitItem(ItemStack item) {
        mainKitItem = new ItemBuilder(item.clone()).setDescription(KIT_ITEM_DESC).build();
    }

    /**
     * used for checking in controller if kit has event
     */
    public void addEvents(List<Class<? extends Event>> events) {
        kitEvents.addAll(events);
    }

    public List<ItemStack> getKitItems() {
        List<ItemStack> kitItems = new ArrayList<>();
        if (mainKitItem != null) {
            kitItems.add(mainKitItem);
        }
        if (!additionalKitItems.isEmpty()) {
            kitItems.addAll(additionalKitItems);
        }
        return kitItems;
    }

    public void setUsesOffHand(boolean usesOffHand) {
        this.usesOffHand = usesOffHand;
    }

    /**
     * use this for settings as likelyhood, radius or...
     * used later in kitsettings menu for configuring
     */
    @Deprecated
    public <V> void addSetting(KitSettings settings, V value) {
        this.settings.put(settings, value);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> T getSetting(KitSettings settings) {
        return (T) this.settings.getOrDefault(settings, null);
    }

    public float getCooldown() {
        return 0.0F;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public List<ItemStack> getDisplayItems() {
        return new ArrayList<>(displayItems.values());
    }

    public boolean isUsingOffHand() {
        return usesOffHand;
    }

    public boolean isUsable() {
        return isUsable;
    }

    public void setUsable(boolean usable) {
        isUsable = usable;
    }

    public Set<Class<? extends Event>> getKitEvents() {
        return kitEvents;
    }

    public boolean isKitItem(ItemStack itemStack) {
        return false;
    }
}

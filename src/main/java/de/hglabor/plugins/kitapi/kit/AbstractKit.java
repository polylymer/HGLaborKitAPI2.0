package de.hglabor.plugins.kitapi.kit;

import de.hglabor.Localization.Localization;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.kit.events.KitEvents;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class AbstractKit extends KitEvents {
    private final String name;
    private final Map<KitSettings, Object> settings = new HashMap<>();
    private final Map<Locale, ItemStack> displayItems = new HashMap<>();
    private List<ItemStack> additionalKitItems = new ArrayList<>();
    private Set<Class<? extends Event>> kitEvents = new HashSet<>();
    private Set<Class<? extends Event>> cooldownLessEvents = new HashSet<>();
    private boolean sendCooldownMessage = true;
    private ItemStack mainKitItem;

    /**
     * use this to toggle gamemode specific kits
     */
    private boolean isEnabled = true;
    /**
     * enable this to activate a kit in a specific phase
     */
    private boolean isUsable;

    private boolean usesOffHand;
    private boolean isThrowable;
    private boolean isPlaceable;

    protected AbstractKit(String name, Material material) {
        this(name, new ItemStack(material));
    }

    public void setUsesOffHand(boolean usesOffHand) {
        this.usesOffHand = usesOffHand;
    }

    protected AbstractKit(String name, Material material, int cooldown) {
        this(name, new ItemStack(material));
        this.setCooldown(cooldown);
    }

    protected AbstractKit(String name, ItemStack displayItem) {
        this.name = name;
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
    private void setDisplayItem(ItemStack item) {
        for (Locale supportedLanguage : KitManager.getInstance().getSupportedLanguages()) {
            String[] description = Localization.INSTANCE.getMessage(name.toLowerCase() + "." + "description", supportedLanguage).split("#");
            displayItems.put(supportedLanguage, new ItemBuilder(item.clone()).setName(ChatColor.RED + name).setDescription(description).build());
        }
    }

    public void setMainKitItem(Material material, boolean unbreakable) {
        if (unbreakable) {
            mainKitItem = new ItemBuilder(material).setDescription(ChatColor.RED + "Kititem").setUnbreakable().build();
        } else {
            mainKitItem = new ItemBuilder(material).setDescription(ChatColor.RED + "Kititem").build();
        }
    }

    public void setMainKitItem(Material material, int size) {
        mainKitItem = new ItemBuilder(material).setDescription(ChatColor.RED + "Kititem").setAmount(size).build();
    }

    public ItemStack getMainKitItem() {
        return mainKitItem;
    }

    public void setMainKitItem(Material material) {
        mainKitItem = new ItemBuilder(material).setDescription(ChatColor.RED + "Kititem").build();
    }

    /**
     * used for checking in controller if kit has event
     */
    public void addEvents(List<Class<? extends Event>> events) {
        kitEvents.addAll(events);
    }

    public void addCoolDownLessEvents(List<Class<? extends Event>> events) {
        cooldownLessEvents.addAll(events);
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

    /**
     * use this for settings as likelyhood, radius or...
     * used later in kitsettings menu for configuring
     */
    public <V> void addSetting(KitSettings settings, V value) {
        this.settings.put(settings, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(KitSettings settings) {
        return (T) this.settings.getOrDefault(settings, null);
    }

    public int getCooldown() {
        return (int) settings.getOrDefault(KitSettings.COOLDOWN, 0);
    }

    public void setCooldown(int seconds) {
        settings.put(KitSettings.COOLDOWN, seconds);
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public List<ItemStack> getDisplayItems() {
        return new ArrayList<>(displayItems.values());
    }

    public boolean isUsingOffHand() {
        return usesOffHand;
    }

    public boolean isPlaceable() {
        return isPlaceable;
    }
}

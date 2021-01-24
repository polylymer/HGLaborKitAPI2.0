package de.hglabor.plugins.kitapi.kit;

import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.kits.*;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.player.KitPlayerSupplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class KitManager {
    private final static KitManager instance = new KitManager();
    public final List<AbstractKit> kits;
    private KitPlayerSupplier playerSupplier;
    private KitItemSupplier itemSupplier;
    private JavaPlugin plugin;

    private KitManager() {
        this.kits = new ArrayList<>();
    }

    public static KitManager getInstance() {
        return instance;
    }

    public List<AbstractKit> empty() {
        int kitAmount = KitApiConfig.getInstance().getInteger("kit.amount");
        List<AbstractKit> emptyKitList = new ArrayList<>(kitAmount);
        for (int i = 0; i < kitAmount; i++) {
            emptyKitList.add(NoneKit.getInstance());
        }
        return emptyKitList;
    }

    public void register(KitPlayerSupplier kitPlayerSupplier, KitItemSupplier kitItemSupplier, JavaPlugin plugin) {
        this.playerSupplier = kitPlayerSupplier;
        this.itemSupplier = kitItemSupplier;
        this.plugin = plugin;
        register(MagmaKit.getInstance());
        register(NinjaKit.getInstance());
        register(NoneKit.getInstance());
        register(BlinkKit.INSTANCE);
        register(SurpriseKit.INSTANCE);
        register(CopyCatKit.INSTANCE);
        register(GladiatorKit.INSTANCE);
        register(GamblerKit.INSTANCE);
        register(SmogmogKit.INSTANCE);
        register(RogueKit.INSTANCE);
        register(SnailKit.getInstance());
    }

    public void register(AbstractKit kit) {
        System.out.println(kit.getName());
        kits.add(kit);
        KitApiConfig kitApiConfig = KitApiConfig.getInstance();
        kitApiConfig.loadKit(kit);
        kit.setEnabled(kitApiConfig.getBoolean("kit" + "." + kit.getName() + "." + "enabled"));
        kit.setCooldown(kitApiConfig.getInteger("kit" + "." + kit.getName() + "." + "cooldown"));
    }

    public List<AbstractKit> getEnabledKits() {
        return kits.stream().filter(AbstractKit::isEnabled).collect(Collectors.toList());
    }

    public AbstractKit getAlphabeticallyKit(int index) {
        List<AbstractKit> kits = new ArrayList<>(getEnabledKits());
        kits.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return kits.get(index);
    }

    public AbstractKit byName(String name) {
        for (AbstractKit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public AbstractKit byItem(ItemStack itemStack) {
        for (AbstractKit kit : kits) {
            if (kit.getDisplayItems().stream().anyMatch(displayItem -> displayItem.isSimilar(itemStack))) {
                return kit;
            }
        }
        return null;
    }

    public KitPlayer getPlayer(Player player) {
        return playerSupplier.getKitPlayer(player);
    }

    public boolean hasKitItemInAnyHand(Player player, AbstractKit kit) {
        return player.getInventory().getItemInOffHand().isSimilar(kit.getMainKitItem()) || player.getInventory().getItemInMainHand().isSimilar(kit.getMainKitItem());
    }

    public void giveKitItemsIfSlotEmpty(KitPlayer kitPlayer, AbstractKit kit) {
        itemSupplier.giveKitItems(kitPlayer, kit);
    }

    public void giveKitItemsIfSlotEmpty(KitPlayer kitPlayer, AbstractKit kit, List<ItemStack> items) {
        itemSupplier.giveKitItems(kitPlayer,kit ,items);
    }

    public void giveItemsIfSlotEmpty(KitPlayer kitPlayer, List<ItemStack> kits) {
        itemSupplier.giveItems(kitPlayer, kits);
    }

    public void removeKitItems(AbstractKit kit, Player player) {
        player.getInventory().removeItem(kit.getKitItems().toArray(new ItemStack[0]));
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public boolean sendCooldownMessage(KitPlayer kitPlayer, AbstractKit kit) {
        if (kit.getCooldown() > 0) {
            Cooldown kitCooldown = kitPlayer.getKitCooldown(kit);
            Player player = Bukkit.getPlayer(kitPlayer.getUUID());
            if (kitCooldown.hasCooldown()) {
                long cooldown = (kitCooldown.getStartTime() + (kit.getCooldown() * 1000L + kitCooldown.getAdditionalTime() * 1000L)) - System.currentTimeMillis();
                assert player != null;
                if (kit.getMainKitItem() != null && hasKitItemInAnyHand(player, kit)) {
                    player.sendMessage("Cooldown: " + (cooldown) / 1000D);
                } else if (kit.getMainKitItem() == null) {
                    player.sendMessage("Cooldown: " + (cooldown) / 1000D);
                }
                return true;
            }
        }
        return false;
    }
}

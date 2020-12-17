package de.hglabor.plugins.kitapi.kit;

import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.kits.MagmaKit;
import de.hglabor.plugins.kitapi.kit.kits.NoneKit;
import de.hglabor.plugins.kitapi.kit.kits.ViperKit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class KitManager {
    public final static KitManager instance = new KitManager();
    public final List<AbstractKit> kits;

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

    public void register() {
        register(ViperKit.getInstance());
        register(MagmaKit.getInstance());
        register(NoneKit.getInstance());
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
}

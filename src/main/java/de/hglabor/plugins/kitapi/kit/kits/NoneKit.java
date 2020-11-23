package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import org.bukkit.Material;

public class NoneKit extends AbstractKit {
    private final static NoneKit instance = new NoneKit();

    private NoneKit() {
        super("None", Material.STONE_SWORD);
    }

    public static NoneKit getInstance() {
        return instance;
    }
}

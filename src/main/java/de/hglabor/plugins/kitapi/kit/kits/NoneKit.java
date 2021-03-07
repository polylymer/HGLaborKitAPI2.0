package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import org.bukkit.Material;

public class NoneKit extends AbstractKit {
    public final static NoneKit INSTANCE = new NoneKit();

    private NoneKit() {
        super("None", Material.STONE_SWORD);
    }
}

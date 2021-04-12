package de.hglabor.plugins.kitapi.kit;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import org.bukkit.Material;

import java.util.Map;

public abstract class MultipleCooldownsKit<T> extends AbstractKit {
    protected final Map<T, Float> cooldowns;

    protected MultipleCooldownsKit(String name, Material material, Map<T, Float> cooldowns) {
        super(name, material);
        this.cooldowns = cooldowns;
    }

    public Map<T, Float> getCooldowns() {
        return cooldowns;
    }
}

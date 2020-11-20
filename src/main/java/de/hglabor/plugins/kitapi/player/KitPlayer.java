package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.kit.AbstractKit;

import java.util.List;

public interface KitPlayer {
    List<AbstractKit> getKits();

    boolean hasKit(AbstractKit kit);

    boolean areKitsDisabled();

    void setKit(AbstractKit kit, int index);

    boolean hasKitCooldown(AbstractKit kit);
}

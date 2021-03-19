
package de.hglabor.plugins.kitapi.supplier;

import org.bukkit.entity.Entity;

import java.util.List;

public interface IPlayerList extends KitPlayerSupplier {
    List<Entity> getTrackingTargets();
}


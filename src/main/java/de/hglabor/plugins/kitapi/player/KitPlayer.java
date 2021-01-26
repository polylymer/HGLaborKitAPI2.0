package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.config.LastHitInformation;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.UUID;

public interface KitPlayer {
    List<AbstractKit> getKits();

    boolean hasKit(AbstractKit kit);

    boolean areKitsDisabled();

    void setKit(AbstractKit kit, int index);

    void setKits(List<AbstractKit> kits);

    boolean hasKitCooldown(AbstractKit kit);

    LastHitInformation getLastHitInformation();

    UUID getUUID();

    boolean isValid();

    void disableKits(boolean shouldDisable);

    void activateKitCooldown(AbstractKit kit, int cooldown);

    Cooldown getKitCooldown(AbstractKit kit);

    <T> T getKitAttribute(AbstractKit kit);

    <T> void putKitAttribute(AbstractKit kit, T value);
}

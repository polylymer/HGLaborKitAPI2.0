package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.config.LastHitInformation;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KitPlayer {
    List<AbstractKit> getKits();

    void setKits(List<AbstractKit> kits);

    boolean hasKit(AbstractKit kit);

    boolean areKitsDisabled();

    boolean isInInventory();

    void setInInventory(boolean value);

    void setKit(AbstractKit kit, int index);

    LastHitInformation getLastHitInformation();

    UUID getUUID();

    Optional<Player> getBukkitPlayer();

    boolean isValid();

    boolean isInCombat();

    void disableKits(boolean shouldDisable);

    void activateKitCooldown(AbstractKit kit);

    void clearCooldown(AbstractKit kit);

    Cooldown getKitCooldown(AbstractKit kit);

    <T> T getKitAttribute(String key);

    <T> T getKitAttributeOrDefault(String key, T defaultValue);

    <T> void putKitAttribute(String key, T value);

    String printKits();
}

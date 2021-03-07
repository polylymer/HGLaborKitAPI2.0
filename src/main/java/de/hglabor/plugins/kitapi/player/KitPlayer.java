package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitProperties;
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

    boolean hasKitCooldown(AbstractKit kit);

    LastHitInformation getLastHitInformation();

    <T extends KitProperties> T getKitProperty(KitMetaData key);

    <T extends KitProperties> void putKitPropety(KitMetaData key, T property);

    UUID getUUID();

    Optional<Player> getBukkitPlayer();

    boolean isValid();

    boolean isInCombat();

    void disableKits(boolean shouldDisable);

    void activateKitCooldown(AbstractKit kit, int cooldown);

    Cooldown getKitCooldown(AbstractKit kit);

    <T> T getKitAttribute(String key);

    <T> T getKitAttributeOrDefault(String key, T defaultValue);

    <T> void putKitAttribute(String key, T value);

    String printKits();
}

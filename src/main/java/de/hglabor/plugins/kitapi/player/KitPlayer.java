package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;

import java.util.List;
import java.util.UUID;

public interface KitPlayer {
    List<AbstractKit> getKits();

    boolean hasKit(AbstractKit kit);

    boolean areKitsDisabled();

    void setKit(AbstractKit kit, int index);

    boolean hasKitCooldown(AbstractKit kit);

    KitPlayer getLastHittedPlayer();

    void setLastHittedPlayer(KitPlayer player);

    long getLastHitTimeStamp();

    UUID getUUID();

    boolean isValid();

    void disableKits(boolean shouldDisable);

    void activateKitCooldown(AbstractKit kit, int cooldown);

    Cooldown getKitCooldown(AbstractKit kit);

    void setLastHittedTimeStamp(Long timeStamp);

    <T> T getKitAttribute(AbstractKit kit);

    <T> void putKitAttribute(AbstractKit kit, T value);
}

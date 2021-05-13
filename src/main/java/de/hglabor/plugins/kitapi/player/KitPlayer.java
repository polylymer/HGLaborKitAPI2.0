package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.pvp.LastHitInformation;
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

    /**
     * Check if a player is in combat
     * for specific amount of time
     *
     * @param combatTimeLimit The duration limit of the combat in seconds
     * @return The result of the check
     */
    boolean isInCombat(int combatTimeLimit);

    /**
     * Added this method as default
     * implementation calling the old declaring
     * of this method to avoid compatibility issues with non-project classes
     * using this interface
     *
     * Please remove and implement in sub-classes if possible
     * Comment, create an issue or change this yourself if the only time this gets used
     * is in {@see KitPlayerImpl}
     * Disclaimer when changing: You may need to refactor every use of this method even
     * outside this project
     *
     * @return The return of the check with default value
     */
    default boolean isInCombat() {
        //Using default value and maximum of old isInCombat
        return this.isInCombat(10);
    }

    void disableKits(boolean shouldDisable);

    void activateKitCooldown(AbstractKit kit);

    void clearCooldown(AbstractKit kit);

    Cooldown getKitCooldown(AbstractKit kit);

    <T> T getKitAttribute(String key);

    <T> T getKitAttributeOrDefault(String key, T defaultValue);

    <T> void putKitAttribute(String key, T value);

    <T> void updateKitAttribute(String key, T value);

    String printKits();

    int getLeftCps();

    void addLeftClick(long value);
}

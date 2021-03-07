package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitProperties;
import de.hglabor.plugins.kitapi.kit.config.LastHitInformation;
import de.hglabor.plugins.kitapi.kit.kits.CopyCatKit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class KitPlayerImpl implements KitPlayer {
    protected final UUID uuid;
    protected final List<AbstractKit> kits;
    protected final Map<String, Object> kitAttributes;
    protected final Map<AbstractKit, Cooldown> kitCooldowns;
    protected final Map<KitMetaData, KitProperties> kitProperties;
    protected final LastHitInformation lastHitInformation;
    protected boolean kitsDisabled;
    protected boolean inInventory;

    public KitPlayerImpl(UUID uuid) {
        this.uuid = uuid;
        this.kitAttributes = new HashMap<>();
        this.kitCooldowns = new HashMap<>();
        this.kitProperties = new HashMap<>();
        this.lastHitInformation = new LastHitInformation();
        this.kits = KitApi.getInstance().emptyKitList();
    }

    @Override
    public List<AbstractKit> getKits() {
        AbstractKit copyCatKit = this.getKitAttribute(CopyCatKit.INSTANCE.getKitAttributeKey());
        if (copyCatKit != null) {
            List<AbstractKit> kitList = new ArrayList<>(this.kits);
            kitList.add(copyCatKit);
            return kitList;
        } else {
            return this.kits;
        }
    }

    @Override
    public void setKits(List<AbstractKit> list) {
        this.kits.clear();
        this.kits.addAll(list);
    }

    @Override
    public boolean hasKit(AbstractKit kit) {
        AbstractKit copyCatKit = this.getKitAttribute(CopyCatKit.INSTANCE.getKitAttributeKey());
        return copyCatKit != null && copyCatKit.equals(kit) || this.kits.contains(kit);
    }

    @Override
    public boolean areKitsDisabled() {
        return kitsDisabled;
    }

    @Override
    public void setKit(AbstractKit abstractKit, int i) {
        kits.set(i, abstractKit);
    }

    @Override
    public boolean hasKitCooldown(AbstractKit kit) {
        return kitCooldowns.getOrDefault(kit, new Cooldown(false)).hasCooldown();
    }

    @Override
    public LastHitInformation getLastHitInformation() {
        return lastHitInformation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends KitProperties> T getKitProperty(KitMetaData kitMetaData) {
        return (T) kitProperties.getOrDefault(kitMetaData, null);
    }

    @Override
    public <T extends KitProperties> void putKitPropety(KitMetaData kitMetaData, T t) {
        kitProperties.put(kitMetaData, t);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public abstract boolean isValid();

    @Override
    public boolean isInCombat() {
        Optional<Player> lastDamager = lastHitInformation.getLastDamager();
        if (lastDamager.isPresent()) {
            KitPlayer damager = KitApi.getInstance().getPlayer(lastDamager.get());
            return damager.isValid() && lastHitInformation.getLastDamagerTimestamp() + 10 * 1000L > System.currentTimeMillis();
        }
        return false;
    }

    @Override
    public void disableKits(boolean kitsDisabled) {
        this.kitsDisabled = kitsDisabled;
    }

    @Override
    public void activateKitCooldown(AbstractKit kit) {
        if (hasKit(kit) && !kitCooldowns.getOrDefault(kit, new Cooldown(false)).hasCooldown()) { 
            kitCooldowns.put(kit, new Cooldown(true, kit.getCooldown()));
        }
    }

    @Override
    public void clearCooldown(AbstractKit kit) {
        kitCooldowns.remove(kit);
    }

    @Override
    public Cooldown getKitCooldown(AbstractKit abstractKit) {
        return kitCooldowns.getOrDefault(abstractKit, new Cooldown(false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getKitAttribute(String key) {
        return (T) kitAttributes.get(key);
    }

    @Override
    public <T> T getKitAttributeOrDefault(String key, T defaultValue) {
        return getKitAttribute(key) == null ? defaultValue : getKitAttribute(key);
    }

    @Override
    public <T> void putKitAttribute(String key, T value) {
        kitAttributes.put(key, value);
    }

    @Override
    public boolean isInInventory() {
        return inInventory;
    }

    @Override
    public void setInInventory(boolean value) {
        this.inInventory = value;
    }

    @Override
    public Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public void resetKitAttributes() {
        this.kitAttributes.clear();
    }

    public void resetKitCooldowns() {
        this.kitCooldowns.clear();
    }

    public void resetKitProperties() {
        this.kitProperties.clear();
    }

    public String printKits() {
        StringBuilder stringBuilder = new StringBuilder();
        this.kits.forEach((kit) -> stringBuilder.append(kit.getName()).append(","));
        stringBuilder.delete(stringBuilder.lastIndexOf(","), stringBuilder.length());
        return stringBuilder.toString();
    }
}

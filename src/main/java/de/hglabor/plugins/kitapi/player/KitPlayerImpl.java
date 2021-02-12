package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitProperties;
import de.hglabor.plugins.kitapi.kit.config.LastHitInformation;
import de.hglabor.plugins.kitapi.kit.kits.CopyCatKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;

import java.util.*;

public abstract class KitPlayerImpl implements KitPlayer {
    protected final UUID uuid;
    protected final List<AbstractKit> kits;
    protected final Map<AbstractKit, Map<Class<?>, Object>> kitAttributes;
    protected final Map<AbstractKit, Cooldown> kitCooldowns;
    protected final Map<KitMetaData, KitProperties> kitProperties;
    protected final LastHitInformation lastHitInformation;
    protected boolean kitsDisabled;

    public KitPlayerImpl(UUID uuid) {
        this.uuid = uuid;
        this.kitAttributes = new HashMap<>();
        this.kitCooldowns = new HashMap<>();
        this.kitProperties = new HashMap<>();
        this.lastHitInformation = new LastHitInformation();
        this.kits = KitManager.getInstance().emptyKitList();
    }

    @Override
    public List<AbstractKit> getKits() {
        AbstractKit copyCatKit = this.getKitAttribute(CopyCatKit.INSTANCE);
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
        AbstractKit copyCatKit = this.getKitAttribute(CopyCatKit.INSTANCE);
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
    public void disableKits(boolean kitsDisabled) {
        this.kitsDisabled = kitsDisabled;
    }

    @Override
    public void activateKitCooldown(AbstractKit kit, int seconds) {
        if (hasKit(kit) && !kitCooldowns.getOrDefault(kit, new Cooldown(false)).hasCooldown()) {
            kitCooldowns.put(kit, new Cooldown(true, System.currentTimeMillis()));
            Bukkit.getScheduler().runTaskLater(KitManager.getInstance().getPlugin(), () -> kitCooldowns.put(kit, new Cooldown(false)),// (seconds + additionalKitCooldowns.getOrDefault(kit, 0)) * 20);
                    (seconds) * 20L);
        }
    }

    @Override
    public Cooldown getKitCooldown(AbstractKit abstractKit) {
        return kitCooldowns.getOrDefault(abstractKit, new Cooldown(false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getKitAttribute(AbstractKit kit, Class<?> clazz) {
        return (T) kitAttributes.getOrDefault(kit, new HashMap<>()).getOrDefault(clazz, null);
    }

    @Override
    public <T> void putKitAttribute(AbstractKit kit, T t, Class<?> clazz) {
        if (!kitAttributes.containsKey(kit)) {
            kitAttributes.put(kit, new HashMap<>());
        }
        kitAttributes.get(kit).put(clazz, t);
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

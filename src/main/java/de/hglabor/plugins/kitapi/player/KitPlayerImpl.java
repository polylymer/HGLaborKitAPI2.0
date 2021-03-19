package de.hglabor.plugins.kitapi.player;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.kits.CopyCatKit;
import de.hglabor.plugins.kitapi.pvp.LastHitInformation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class KitPlayerImpl implements KitPlayer {
    protected final UUID uuid;
    protected final List<AbstractKit> kits;
    protected final Map<String, Object> kitAttributes;
    protected final LastHitInformation lastHitInformation;
    private final List<Long> leftClicks;
    protected boolean kitsDisabled;
    protected boolean inInventory;

    public KitPlayerImpl(UUID uuid) {
        this.uuid = uuid;
        this.leftClicks = new ArrayList<>();
        this.kitAttributes = new HashMap<>();
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
    public LastHitInformation getLastHitInformation() {
        return lastHitInformation;
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
        if (kitsDisabled) {
            kits.forEach(kit -> kit.onDeactivation(this));
        } else {
            kits.forEach(kit -> kit.onEnable(this));
        }
    }

    @Override
    public void activateKitCooldown(AbstractKit kit) {
        if (hasKit(kit) && !getKitCooldown(kit).hasCooldown()) {
            kitAttributes.put(KitApi.getInstance().cooldownKey(kit), new Cooldown(true, kit.getCooldown()));
        }
    }

    @Override
    public void clearCooldown(AbstractKit kit) {
        kitAttributes.remove(KitApi.getInstance().cooldownKey(kit));
    }

    @Override
    public Cooldown getKitCooldown(AbstractKit kit) {
        return (Cooldown) kitAttributes.getOrDefault(KitApi.getInstance().cooldownKey(kit), new Cooldown(false));
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

    public String printKits() {
        StringBuilder stringBuilder = new StringBuilder();
        this.kits.forEach((kit) -> stringBuilder.append(kit.getName()).append(","));
        stringBuilder.delete(stringBuilder.lastIndexOf(","), stringBuilder.length());
        return stringBuilder.toString();
    }

    @Override
    public int getLeftCps() {
        final long time = System.currentTimeMillis();
        leftClicks.removeIf(value -> value + 1000 < time);
        return leftClicks.size();
    }

    public void addLeftClick(long value) {
        leftClicks.add(value);
    }
}

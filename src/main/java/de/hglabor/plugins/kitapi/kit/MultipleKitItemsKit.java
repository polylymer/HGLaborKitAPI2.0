package de.hglabor.plugins.kitapi.kit;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.kit.config.Cooldown;
import de.hglabor.plugins.kitapi.kit.items.KitItemAction;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static de.hglabor.utils.localization.Localization.t;

public abstract class MultipleKitItemsKit extends MultipleCooldownsKit<KitItemAction> {
    protected MultipleKitItemsKit(String name, Material displayMaterial) {
        super(name, displayMaterial, new HashMap<>());
    }

    protected void setItemsAndCooldown(Map<KitItemAction, Float> map) {
        cooldowns.putAll(map);
    }

    @Override
    public List<ItemStack> getKitItems() {
        return cooldowns.keySet().stream().map(KitItemAction::getItem).collect(Collectors.toList());
    }

    private KitItemAction byItemStack(ItemStack itemStack) {
        return cooldowns.keySet().stream().filter(kitItemAction -> kitItemAction.getItem().isSimilar(itemStack)).findFirst().orElse(null);
    }

    public boolean isKitItem(ItemStack itemStack) {
        return getKitItems().stream().anyMatch(item -> item.isSimilar(itemStack));
    }

    protected String getCooldownKey(ItemStack itemStack) {
        return this.getName() + "." + itemStack.getType() + "." + "cooldown";
    }

    protected void activateCooldown(KitPlayer kitPlayer, ItemStack itemStack) {
        if (!kitPlayer.getKitAttributeOrDefault(getCooldownKey(itemStack), new Cooldown(false)).hasCooldown()) {
            kitPlayer.putKitAttribute(getCooldownKey(itemStack), new Cooldown(true, cooldowns.getOrDefault(byItemStack(itemStack), 0F)));
        }
    }

    /**
     * @param kitPlayer kitplayer
     * @param itemStack kititem
     * @return if player has cooldown or not
     * if true = sends message with localized key of action
     * if false = nothing
     */
    public boolean sendCooldownMessage(KitPlayer kitPlayer, ItemStack itemStack) {
        KitItemAction kitItemAction = byItemStack(itemStack);
        if (kitItemAction == null) {
            return false;
        }

        if (cooldowns.get(kitItemAction) == 0) {
            return false;
        }

        String cooldownKey = getCooldownKey(itemStack);
        Cooldown kitCooldown = kitPlayer.getKitAttributeOrDefault(cooldownKey,new Cooldown(false));
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player == null) {
            return false;
        }

        if (kitCooldown.hasCooldown()) {
            long timeLeft = (kitCooldown.getEndTime()) - System.currentTimeMillis();
            if (timeLeft <= 0) {
                kitPlayer.putKitAttribute(cooldownKey,null);
                return false;
            }
            Locale locale = ChatUtils.getPlayerLocale(player);
            player.sendActionBar(t("kit.multipleCooldown",
                    ImmutableMap.of(
                            "numberInSeconds", String.valueOf(timeLeft / 1000D),
                            "action", t(kitItemAction.getLocalizationKey(), locale)),
                    locale
            ));
            return true;
        }
        return false;
    }
}

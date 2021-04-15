package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.items.KitItemBuilder;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static de.hglabor.utils.localization.Localization.t;

public class ArcherKit extends AbstractKit implements Listener {
    public final static ArcherKit INSTANCE = new ArcherKit();

    @IntArg(min = 0)
    private final int effectDurationInSeconds, effectAmplifier;
    @IntArg(min = 0, max = 64)
    private final int arrows;
    @IntArg(min = 0, max = 100)
    private final int effectLikelihood;
    private final List<PotionEffectType> potionEffectTypes;

    private ArcherKit() {
        super("Archer", Material.BOW);
        setKitItemPlaceable(true);
        effectDurationInSeconds = 4;
        arrows = 6;
        effectAmplifier = 0;
        potionEffectTypes = Arrays.asList(
                PotionEffectType.BLINDNESS,
                PotionEffectType.SLOW_FALLING,
                PotionEffectType.LEVITATION,
                PotionEffectType.REGENERATION,
                PotionEffectType.SLOW_DIGGING,
                PotionEffectType.HUNGER,
                PotionEffectType.CONFUSION,
                PotionEffectType.GLOWING,
                PotionEffectType.WITHER,
                PotionEffectType.POISON,
                PotionEffectType.INCREASE_DAMAGE);
        effectLikelihood = 50;
        mainKitItem = new KitItemBuilder(Material.BOW).setUnbreakable(true).build();
        addAdditionalKitItems(new ItemStack(Material.ARROW, arrows));
    }

    @KitEvent
    public void onProjectileHitEvent(ProjectileHitEvent event, KitPlayer kitPlayer, Entity hitEntity) {
        if (hitEntity instanceof LivingEntity) {
            kitPlayer.getBukkitPlayer().ifPresent(player -> {
                KitApi.getInstance().giveItemsIfSlotEmpty(kitPlayer, List.of(new ItemStack(Material.ARROW, 1)));

                Map<String, String> hearts = Map.of(
                        "hearts", String.valueOf((int) ((LivingEntity) hitEntity).getHealth()),
                        "name", hitEntity.getName());
                String key = "archer.hit";
                Locale playerLocale = ChatUtils.locale(player);
                player.sendMessage(t(key, hearts, playerLocale));
            });
        }
    }

    @KitEvent
    public void onKitPlayerShootBow(EntityShootBowEvent event, KitPlayer kitPlayer, Entity projectile) {
        if (!ChanceUtils.roll(effectLikelihood)) {
            return;
        }
        if (!(event.getProjectile() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) event.getProjectile();
        PotionEffectType type = potionEffectTypes.get(ThreadLocalRandom.current().nextInt(potionEffectTypes.size()));
        PotionEffect potionEffect = new PotionEffect(type, effectDurationInSeconds * 20, effectAmplifier);
        arrow.addCustomEffect(potionEffect, false);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }
}

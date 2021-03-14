package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class DannyKit extends AbstractKit {
    public static final DannyKit INSTANCE = new DannyKit();
    private final PotionEffectType[] potionEffects;
    private final String attributeKey;
    @IntArg(min = 0, max = 100)
    private final int dannyHeadLikelihood;
    @IntArg
    private final int potionDuration, potionAmplifier;

    private DannyKit() {
        super("Danny", Material.PLAYER_HEAD);
        this.potionEffects = new PotionEffectType[]{PotionEffectType.BLINDNESS, PotionEffectType.WITHER, PotionEffectType.POISON, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.CONFUSION};
        attributeKey = this.getName() + "Runnable";
        dannyHeadLikelihood = 22;
        potionDuration = 3;
        potionAmplifier = 1;
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        BukkitTask task;
        task = Bukkit.getScheduler().runTaskTimer(KitApi.getInstance().getPlugin(), () -> {
            if (kitPlayer.isValid()) {
                DannyAction action = DannyAction.values()[new Random().nextInt(DannyAction.values().length)];
                Player player = Bukkit.getPlayer(kitPlayer.getUUID());
                if (player == null) return;
                switch (action) {
                    case EAT_ITEM:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_EAT, 10, 1);
                        player.getInventory().setItem(new Random().nextInt(player.getInventory().getSize()), new ItemStack(Material.AIR));
                        break;
                    case NEGATIVE_EFFECT:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_HURT, 10, 1);
                        PotionEffect effect = new PotionEffect(potionEffects[new Random().nextInt(potionEffects.length)], potionDuration * 20, potionAmplifier);
                        player.addPotionEffect(effect);
                        break;
                    case SKULL_ESCALATION:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_AMBIENT, 10, 1);
                        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, new Random().nextInt(12));
                        SkullMeta meta = (SkullMeta) stack.getItemMeta();
                        meta.setOwningPlayer(Bukkit.getOfflinePlayer("Daannyy"));
                        stack.setItemMeta(meta);
                        for (int i = 0; i < new Random().nextInt(6); i++) {
                            player.getWorld().dropItemNaturally(player.getLocation(), stack);
                        }
                        break;
                    case DROP_ITEM:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_DEATH, 10, 1);
                        if (player.getItemInHand().getType() != Material.AIR) {
                            player.dropItem(false);
                        }
                        break;
                }
            }
        }, 0, 10 * 20L);
        kitPlayer.putKitAttribute(attributeKey, task);
    }

    @KitEvent
    @Override
    public void onCraftItem(CraftItemEvent event) {
        if (ChanceUtils.roll(dannyHeadLikelihood)) {
            ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("Daannyy"));
            stack.setItemMeta(meta);
            event.setCurrentItem(stack);
        }
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        BukkitTask task = kitPlayer.getKitAttribute(attributeKey);
        if (task != null) {
            task.cancel();
        }
    }

    public enum DannyAction {
        EAT_ITEM,
        NEGATIVE_EFFECT,
        SKULL_ESCALATION,
        DROP_ITEM
    }
}

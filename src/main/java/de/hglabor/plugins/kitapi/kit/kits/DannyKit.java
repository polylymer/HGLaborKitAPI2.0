package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class DannyKit extends AbstractKit {
    public static final DannyKit INSTANCE = new DannyKit();
    private Plugin plugin = KitManager.getInstance().getPlugin();
    private PotionEffectType[] potionEffects = {PotionEffectType.BLINDNESS, PotionEffectType.WITHER, PotionEffectType.POISON, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.CONFUSION};

    protected DannyKit() {
        super("Danny", Material.PLAYER_HEAD);
        addSetting(KitSettings.EFFECT_MULTIPLIER, 1);
        addSetting(KitSettings.EFFECT_DURATION, 3);
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        BukkitTask task;
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(kitPlayer.isValid()) {
                DannyAction action = DannyAction.values()[new Random().nextInt(DannyAction.values().length)];
                Player player = Bukkit.getPlayer(kitPlayer.getUUID());
                if(player == null) return;
                switch (action) {
                    case EAT_ITEM:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_EAT, 10, 1);
                        player.getInventory().setItem(new Random().nextInt(player.getInventory().getSize()), new ItemStack(Material.AIR));
                        break;
                    case NEGATIVE_EFFECT:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_HURT, 10, 1);
                        player.addPotionEffect(new PotionEffect(potionEffects[new Random().nextInt(potionEffects.length)], (int)getSetting(KitSettings.EFFECT_DURATION)*20, getSetting(KitSettings.EFFECT_MULTIPLIER)));
                        break;
                    case SKULL_ESCALATION:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_AMBIENT, 10, 1);
                        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, new Random().nextInt(12));
                        SkullMeta meta = (SkullMeta) stack.getItemMeta();
                        meta.setOwningPlayer(Bukkit.getOfflinePlayer("Daannyy"));
                        stack.setItemMeta(meta);
                        for (int i = 0; i < new Random().nextInt(6); i++) {
                            player.getWorld().dropItemNaturally( player.getLocation(), stack);
                        }
                        break;
                    case DROP_ITEM:
                        player.playSound(player.getLocation(), Sound.ENTITY_DONKEY_DEATH, 10, 1);
                        player.dropItem(false);
                        break;
                }
            }
        }, 0, 10*20L);
        kitPlayer.putKitAttribute(this, task);
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        BukkitTask task = kitPlayer.getKitAttribute(this);
        task.cancel();
    }

    public enum DannyAction {

        EAT_ITEM,
        NEGATIVE_EFFECT,
        SKULL_ESCALATION,
        DROP_ITEM

    }

}

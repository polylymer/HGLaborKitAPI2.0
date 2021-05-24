package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.LongArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author freshkenny
 * @since 2021-04-18
 *
 * When in a fight you gain half a heart every second for each enemy in a radius of 5 blocks and each enemy loses half a heart.
 */
public class HemomancerKit extends AbstractKit {
    public static final HemomancerKit INSTANCE = new HemomancerKit();

    /** Delay (ticks) before start of task execution */
    @LongArg
    private final long startDelay;
    /** Ticks between executions of the task */
    @LongArg
    private final long period;
    @DoubleArg
    private final double radius;
    @DoubleArg
    private final double healthGain;
    @DoubleArg
    private final double healthLoss;

    private BukkitTask task;

    private HemomancerKit() {
        super("Hemomancer", Material.CRIMSON_FUNGUS);
        startDelay = 0;
        period = 20; // 20 ticks = 1 second
        radius = 5D;
        healthGain = healthLoss = 1;
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        task = Bukkit.getScheduler().runTaskTimer(KitApi.getInstance().getPlugin(), () -> {
            if (kitPlayer.isInCombat() && kitPlayer.getBukkitPlayer().isPresent()) {
                Player player = kitPlayer.getBukkitPlayer().get();
                // Get players in radius
                for (KitPlayer enemy : getKitPlayersInRadius(player, radius)) {
                    if (enemy.getBukkitPlayer().isPresent()) {
                        Player enemyPlayer = enemy.getBukkitPlayer().get();
                        // Remove health from enemy
                        safeSetHealth(enemyPlayer, enemyPlayer.getHealth()-healthLoss);
                        // Add health to player
                        safeSetHealth(player, player.getHealth()+healthGain);
                    }
                }
            }
        }, startDelay, period);
    }

    /** Set the health of a player safely */
    private void safeSetHealth(Player player, double health) {
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute != null) {
            double maxHealth = maxHealthAttribute.getValue();
            // Set health to max when over max
            if (health > maxHealth) {
                player.setHealth(maxHealth);
                return;
            }
            // Don't set if smaller than 0
            if (health < 0) return;
            // Everything is fine
            player.setHealth(health);
        }
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        if (task != null) Bukkit.getScheduler().cancelTask(task.getTaskId());
    }
}

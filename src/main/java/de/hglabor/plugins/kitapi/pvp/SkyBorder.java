package de.hglabor.plugins.kitapi.pvp;

import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static de.hglabor.utils.localization.Localization.t;

public class SkyBorder implements Listener {
    private final int damage;
    private final JavaPlugin plugin;
    private final int maxHeight;

    public SkyBorder(JavaPlugin plugin, int maxHeight, int damage) {
        this.damage = damage;
        this.plugin = plugin;
        this.maxHeight = maxHeight;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void tick(List<Player> players) {
        for (Player player : players) {
            if (player.getLocation().getY() >= maxHeight) {
                player.damage(damage);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getY() >= maxHeight) {
            event.getPlayer().sendMessage(t("skyBorder.limit", ChatUtils.locale(event.getPlayer())));
            event.setCancelled(true);
        }
    }

    public int getDamage() {
        return damage;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}

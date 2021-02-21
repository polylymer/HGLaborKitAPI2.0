package de.hglabor.plugins.kitapi.util;

import de.hglabor.plugins.kitapi.config.KitApiConfig;
import org.bukkit.Bukkit;

public final class Logger {
    private Logger() {
    }

    public static void debug(double message) {
        if (KitApiConfig.getInstance().getBoolean("debug")) {
            System.out.println(message);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("hglabor.debug") || player.isOp()).forEach(player -> player.sendMessage(String.valueOf(message)));
        }
    }

    public static void debug(int message) {
        if (KitApiConfig.getInstance().getBoolean("debug")) {
            System.out.println(message);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("hglabor.debug") || player.isOp()).forEach(player -> player.sendMessage(String.valueOf(message)));
        }
    }

    public static void debug(String message) {
        if (KitApiConfig.getInstance().getBoolean("debug")) {
            System.out.println(message);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("hglabor.debug") || player.isOp()).forEach(player -> player.sendMessage(message));
        }
    }
}

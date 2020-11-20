package de.hglabor.plugins.kitapi.util;

import lombok.Getter;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public final class Localization {
    private Localization() {
    }

    @Getter
    public static final List<Locale> supportedLanguages = Arrays.asList(Locale.ENGLISH, Locale.GERMAN);

    public static String getMessage(String key, Player player) {
        Locale locale = getPlayerLocale(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("hglaborMessages", locale);
        return resourceBundle.getString(key).replaceAll("&", "§");
    }

    public static String getMessage(String key, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("hglaborMessages", locale);
        return resourceBundle.getString(key).replaceAll("&", "§");
    }

    public static String getKitDescription(String key, Locale locale) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("hglaborMessages", locale);
            return resourceBundle.getString(key).replaceAll("&", "§");
        } catch (MissingResourceException ignored) {
            return "";
        }
    }

    public static String getMessage(String key, Map<String, String> values, Player player) {
        Locale locale = getPlayerLocale(player.getUniqueId());
        ResourceBundle resourceBundle = ResourceBundle.getBundle("hglaborMessages", locale);
        return StrSubstitutor.replace(resourceBundle.getString(key), values).replaceAll("&", "§");
    }

    public static String getMessage(String key, Map<String, String> values, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("hglaborMessages", locale);
        return StrSubstitutor.replace(resourceBundle.getString(key), values).replaceAll("&", "§");
    }

    public static void broadcastMessage(String key, Map<String, String> values) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(getMessage(key, values, getPlayerLocale(player.getUniqueId()))));
    }

    public static Locale getPlayerLocale(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            Locale playerLocale = new Locale(player.getLocale());

            if (playerLocale.getLanguage().startsWith("de")) {
                return Locale.GERMAN;
            }

            if (playerLocale.getLanguage().startsWith("en")) {
                return Locale.ENGLISH;
            }

        }
        return Locale.ENGLISH;
    }
}

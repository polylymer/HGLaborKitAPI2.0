package de.hglabor.plugins.kitapi.kit.kits.endermage;

import com.google.common.collect.ImmutableMap;
import de.hglabor.Localization.Localization;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EndermageSearch extends BukkitRunnable {
    protected final int searchDuration;
    protected final Block endermagePortal;
    protected final BlockData oldBlockData;
    protected final Player player;
    protected final KitPlayer kitPlayer;
    protected final World world;
    protected final JavaPlugin plugin;
    protected final double radius;
    protected boolean isSearchingForPlayers;
    protected boolean hasMaged;

    protected EndermageSearch(Player mage, Block endermagePortal, BlockData oldBlockData) {
        this.player = mage;
        this.world = mage.getWorld();
        this.plugin = KitManager.getInstance().getPlugin();
        this.isSearchingForPlayers = true;
        this.kitPlayer = KitManager.getInstance().getPlayer(mage);
        this.searchDuration = EndermageKit.INSTANCE.getSetting(KitSettings.NUMBER);
        this.radius = ((Integer) EndermageKit.INSTANCE.getSetting(KitSettings.RADIUS)).doubleValue();
        this.endermagePortal = endermagePortal;
        this.oldBlockData = oldBlockData;
        init();
    }

    public void init() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            isSearchingForPlayers = false;
            endermagePortal.setBlockData(oldBlockData);
        }, searchDuration * 20L);
    }

    public void end() {
        endermagePortal.setBlockData(oldBlockData);
        cancel();
    }

    private void removeEndermageMetaDataLater(Player player, int delay) {
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            EndermageProperties endermageProperties = kitPlayer.getKitProperty(KitMetaData.HAS_BEEN_MAGED);
            if (endermageProperties == null) {
                player.removeMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), plugin);
            } else if (endermageProperties.getMagedTimeStamp() + (delay * 1000L) <= System.currentTimeMillis()) {
                player.removeMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), plugin);
            }
        }, delay * 20L);
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        if (isSearchingForPlayers) {
            int delay = EndermageKit.INSTANCE.getSetting(KitSettings.NUMBER);
            int magedPeople = 0;
            for (Player nearbyPlayer : world.getNearbyPlayers(endermagePortal.getLocation(), radius, world.getMaxHeight())) {
                KitPlayer nearbyKitPlayer = KitManager.getInstance().getPlayer(nearbyPlayer);
                if (nearbyPlayer == player) {
                    continue;
                }
                if (nearbyKitPlayer.hasKit(EndermageKit.INSTANCE) || nearbyPlayer.hasMetadata(KitMetaData.INGLADIATOR.getKey()) || !nearbyKitPlayer.isValid()) {
                    continue;
                }

                if (!(endermagePortal.getLocation().getY() > nearbyPlayer.getLocation().getY() - 3 && endermagePortal.getLocation().getY() < nearbyPlayer.getLocation().getY() + 3)) {
                    nearbyPlayer.teleport(endermagePortal.getLocation().clone().add(0, 1, 0));
                    //TODO wenn jemand maged und dann nochmal maged verschwindet resistance
                    nearbyPlayer.setMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), new FixedMetadataValue(plugin, ""));
                    nearbyKitPlayer.putKitPropety(KitMetaData.HAS_BEEN_MAGED, new EndermageProperties(System.currentTimeMillis()));
                    removeEndermageMetaDataLater(nearbyPlayer, delay);
                    nearbyPlayer.sendMessage(Localization.INSTANCE.getMessage("endermage.gotTeleported", Utils.getPlayerLocale(nearbyPlayer)));
                    hasMaged = true;
                    magedPeople++;
                }
            }

            if (hasMaged) {
                cancel();
                endermagePortal.setBlockData(oldBlockData);
                player.teleport(endermagePortal.getLocation().clone().add(0, 1, 0));
                player.setMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), new FixedMetadataValue(plugin, ""));
                player.sendMessage(Localization.INSTANCE.getMessage("endermage.successfulTeleport", ImmutableMap.of("amount", String.valueOf(magedPeople)), Utils.getPlayerLocale(player)));
                removeEndermageMetaDataLater(player, delay);
            }
        } else {
            end();
        }
    }
}


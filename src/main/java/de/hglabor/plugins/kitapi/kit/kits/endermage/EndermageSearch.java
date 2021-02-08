package de.hglabor.plugins.kitapi.kit.kits.endermage;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import de.hglabor.utils.localization.Localization;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
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
    protected int counter;
    protected int magedPeople;
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
    }

    private void removeEndermageMetaDataLater(Player player, int delay) {
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            EndermageProperties endermageProperties = kitPlayer.getKitProperty(KitMetaData.HAS_BEEN_MAGED);
            if (endermageProperties == null) {
                player.removeMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), plugin);
                player.sendMessage(Localization.INSTANCE.getMessage("endermage.invincibilityExpired", Utils.getPlayerLocale(player)));
            } else if (endermageProperties.getMagedTimeStamp() + (delay * 1000L) <= System.currentTimeMillis()) {
                player.removeMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), plugin);
                player.sendMessage(Localization.INSTANCE.getMessage("endermage.invincibilityExpired", Utils.getPlayerLocale(player)));
            }
        }, delay * 21L);
    }

    @Override
    public void run() {
        counter++;

        if (isSearchingForPlayers && counter <= searchDuration) {
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
                    mageTeleportPlayer(nearbyPlayer, false);
                    hasMaged = true;
                    magedPeople++;
                }
            }

            this.magedPeople = magedPeople;

            if (hasMaged) {
                endSearching();
                mageTeleportPlayer(player, true);
            }
        } else {
            endSearching();
        }
    }

    protected void mageTeleportPlayer(Player player, boolean isMage) {
        int delay = EndermageKit.INSTANCE.getSetting(KitSettings.NUMBER);
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        player.teleport(endermagePortal.getLocation().clone().add(0, 1, 0));
        player.setMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), new FixedMetadataValue(plugin, ""));
        kitPlayer.putKitPropety(KitMetaData.HAS_BEEN_MAGED, new EndermageProperties(System.currentTimeMillis()));
        if (isMage) {
            player.sendMessage(Localization.INSTANCE.getMessage("endermage.successfulTeleport",
                    ImmutableMap.of("amount", String.valueOf(magedPeople),
                            "timeInSeconds", String.valueOf(delay)),
                    Utils.getPlayerLocale(player)));
        } else {
            player.sendMessage(Localization.INSTANCE.getMessage("endermage.gotTeleported",
                    ImmutableMap.of("timeInSeconds", String.valueOf(delay)),
                    Utils.getPlayerLocale(player)));
        }
        removeEndermageMetaDataLater(player, delay);
    }

    protected void endSearching() {
        cancel();
        isSearchingForPlayers = false;
        endermagePortal.setBlockData(oldBlockData);
        KitManager.getInstance().checkUsesForCooldown(player, EndermageKit.INSTANCE);
        if (!Utils.isUnbreakableLaborBlock(endermagePortal) && endermagePortal.getType() != Material.BEDROCK && !(endermagePortal.getState() instanceof InventoryHolder)) {
            endermagePortal.setType(Material.END_STONE);
        }
    }
}


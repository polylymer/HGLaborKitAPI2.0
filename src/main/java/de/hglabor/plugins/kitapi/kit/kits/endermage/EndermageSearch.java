package de.hglabor.plugins.kitapi.kit.kits.endermage;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

import static de.hglabor.utils.localization.Localization.t;

public class EndermageSearch extends BukkitRunnable {
    protected final int searchDuration;
    protected final Block endermagePortal;
    protected final Material oldBlockType;
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

    protected EndermageSearch(Player mage, Block endermagePortal, BlockData oldBlockData, Material oldBlockType) {
        this.player = mage;
        this.world = mage.getWorld();
        this.plugin = KitApi.getInstance().getPlugin();
        this.isSearchingForPlayers = true;
        this.kitPlayer = KitApi.getInstance().getPlayer(mage);
        this.searchDuration = EndermageKit.INSTANCE.getSearchTime();
        this.radius = EndermageKit.INSTANCE.getSearchRadius();
        this.endermagePortal = endermagePortal;
        this.oldBlockData = oldBlockData;
        this.oldBlockType = oldBlockType;
    }

    private void removeEndermageMetaDataLater(Player player, int delay) {
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            EndermageProperties endermageProperties = kitPlayer.getKitAttribute(EndermageKit.INSTANCE.getHasBeenMagedKey());
            if (endermageProperties == null) {
                player.removeMetadata(EndermageKit.INSTANCE.getHasBeenMagedKey(), plugin);
                player.sendMessage(Localization.INSTANCE.getMessage("endermage.invincibilityExpired", ChatUtils.locale(player)));
            } else if (endermageProperties.getMagedTimeStamp() + (delay * 1000L) <= System.currentTimeMillis()) {
                player.removeMetadata(EndermageKit.INSTANCE.getHasBeenMagedKey(), plugin);
                player.sendMessage(Localization.INSTANCE.getMessage("endermage.invincibilityExpired", ChatUtils.locale(player)));
            }
        }, delay * 21L);
    }

    @Override
    public void run() {
        counter++;

        if (isSearchingForPlayers && counter <= searchDuration) {
            int magedPeople = 0;
            for (Player nearbyPlayer : world.getNearbyPlayers(endermagePortal.getLocation(), radius, world.getMaxHeight())) {
                KitPlayer nearbyKitPlayer = KitApi.getInstance().getPlayer(nearbyPlayer);
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
        int delay = EndermageKit.INSTANCE.getInvulnerabilityAfterMage();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        Location teleportLocation = endermagePortal.getLocation().getBlock().getLocation().add(0.5, 1, 0.5);
        teleportLocation.setPitch(0F);
        teleportLocation.setYaw(0F);
        player.teleport(teleportLocation);
        player.leaveVehicle();
        player.setMetadata(EndermageKit.INSTANCE.getHasBeenMagedKey(), new FixedMetadataValue(plugin, ""));
        kitPlayer.putKitAttribute(EndermageKit.INSTANCE.getHasBeenMagedKey(), new EndermageProperties(System.currentTimeMillis()));
        if (isMage) {
            player.sendMessage(t("endermage.successfulTeleport",
                    Map.of("amount", String.valueOf(magedPeople),
                            "timeInSeconds", String.valueOf(delay)),
                    ChatUtils.locale(player)));
        } else {
            player.sendMessage(t("endermage.gotTeleported",
                    Map.of("timeInSeconds", String.valueOf(delay)),
                    ChatUtils.locale(player)));
        }
        removeEndermageMetaDataLater(player, delay);
    }

    protected void endSearching() {
        cancel();
        isSearchingForPlayers = false;
        KitApi.getInstance().checkUsesForCooldown(player, EndermageKit.INSTANCE, EndermageKit.INSTANCE.getMaxUses());
        if (!Utils.isUnbreakableLaborBlock(endermagePortal) && endermagePortal.getType() != Material.BEDROCK && !(endermagePortal.getState() instanceof InventoryHolder)) {
            endermagePortal.setType(oldBlockType);
        }
        endermagePortal.setBlockData(oldBlockData);
    }
}


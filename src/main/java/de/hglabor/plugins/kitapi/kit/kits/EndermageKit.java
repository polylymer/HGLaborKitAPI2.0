package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableMap;
import de.hglabor.Localization.Localization;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;


public class EndermageKit extends AbstractKit implements Listener {
    public final static EndermageKit INSTANCE = new EndermageKit();

    private EndermageKit() {
        super("Endermage", Material.END_PORTAL_FRAME);
        setMainKitItem(getDisplayMaterial());
        addSetting(KitSettings.RADIUS, 4);
        addSetting(KitSettings.NUMBER, 5);
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        EndermageRunnable endermageRunnable = kitPlayer.getKitAttribute(this);
        if (endermageRunnable != null && endermageRunnable.isSearchingForPlayers) {
            endermageRunnable.end();
        }
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Block endermagePortal = event.getClickedBlock();
        if (endermagePortal != null) {
            Player player = event.getPlayer();
            KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);

            if (player.hasMetadata(KitMetaData.INGLADIATOR.getKey())) {
                return;
            }

            EndermageRunnable endermageRunnable = kitPlayer.getKitAttribute(this);
            if (endermageRunnable != null && endermageRunnable.isSearchingForPlayers) {
                return;
            }
            BlockData oldBlockData = endermagePortal.getBlockData();
            endermagePortal.setType(Material.END_PORTAL_FRAME);
            kitPlayer.putKitAttribute(this,
                    new EndermageRunnable(player, endermagePortal, oldBlockData).runTaskTimer(KitManager.getInstance().getPlugin(), 0, 20));
        }
    }

    @EventHandler
    public void onMagedPlayerGetsDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (player.hasMetadata(KitMetaData.HAS_BEEN_MAGED.getKey())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMagedPlayerHitsEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (player.hasMetadata(KitMetaData.HAS_BEEN_MAGED.getKey())) {
            event.setCancelled(true);
        }
    }

    public static class EndermageRunnable extends BukkitRunnable {
        private final int searchDuration;
        private final Block endermagePortal;
        private final BlockData oldBlockData;
        private final Player player;
        private final World world;
        private final double radius;
        private boolean isSearchingForPlayers;
        private boolean hasMaged;

        public EndermageRunnable(Player mage, Block endermagePortal, BlockData oldBlockData) {
            this.player = mage;
            this.world = mage.getWorld();
            this.isSearchingForPlayers = true;
            this.searchDuration = EndermageKit.INSTANCE.getSetting(KitSettings.NUMBER);
            this.radius = ((Integer) EndermageKit.INSTANCE.getSetting(KitSettings.RADIUS)).doubleValue();
            this.endermagePortal = endermagePortal;
            this.oldBlockData = oldBlockData;
            init();
        }

        public void init() {
            Bukkit.getScheduler().runTaskLater(KitManager.getInstance().getPlugin(), () -> {
                isSearchingForPlayers = false;
                endermagePortal.setBlockData(oldBlockData);
            }, searchDuration * 20L);
        }

        public void end() {
            endermagePortal.setBlockData(oldBlockData);
            cancel();
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
                        nearbyPlayer.setMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), new FixedMetadataValue(KitManager.getInstance().getPlugin(), ""));
                        Bukkit.getScheduler().runTaskLater(KitManager.getInstance().getPlugin(), () -> nearbyPlayer.removeMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), KitManager.getInstance().getPlugin()), delay * 20L);
                        nearbyPlayer.sendMessage(Localization.INSTANCE.getMessage("endermage.gotTeleported", Utils.getPlayerLocale(nearbyPlayer)));
                        hasMaged = true;
                        magedPeople++;
                    }
                }

                if (hasMaged) {
                    cancel();
                    endermagePortal.setBlockData(oldBlockData);
                    player.teleport(endermagePortal.getLocation().clone().add(0, 1, 0));
                    player.sendMessage(Localization.INSTANCE.getMessage("endermage.successfulTeleport", ImmutableMap.of("amount", String.valueOf(magedPeople)), Utils.getPlayerLocale(player)));
                    Bukkit.getScheduler().runTaskLater(KitManager.getInstance().getPlugin(), () -> player.removeMetadata(KitMetaData.HAS_BEEN_MAGED.getKey(), KitManager.getInstance().getPlugin()), delay * 20L);
                }
            } else {
                end();
            }
        }
    }
}






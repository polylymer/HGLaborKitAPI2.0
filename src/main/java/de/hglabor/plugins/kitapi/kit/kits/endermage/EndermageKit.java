package de.hglabor.plugins.kitapi.kit.kits.endermage;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EndermageKit extends AbstractKit implements Listener {
    public final static EndermageKit INSTANCE = new EndermageKit();

    private EndermageKit() {
        super("Endermage", Material.END_PORTAL_FRAME);
        setMainKitItem(getDisplayMaterial());
        addEvents(ImmutableList.of(PlayerInteractEvent.class));
        addSetting(KitSettings.RADIUS, 4);
        addSetting(KitSettings.NUMBER, 5);
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        EndermageSearch endermageRunnable = kitPlayer.getKitAttribute(this);
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

            EndermageSearch endermageRunnable = kitPlayer.getKitAttribute(this);
            if (endermageRunnable != null && endermageRunnable.isSearchingForPlayers) {
                return;
            }
            BlockData oldBlockData = endermagePortal.getBlockData();
            endermagePortal.setType(Material.END_PORTAL_FRAME);
            EndermageSearch newEndermageRunnable = new EndermageSearch(player, endermagePortal, oldBlockData);
            kitPlayer.putKitAttribute(this, newEndermageRunnable);
            newEndermageRunnable.runTaskTimer(KitManager.getInstance().getPlugin(), 0, 20);
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
}






package de.hglabor.plugins.kitapi.kit.kits.endermage;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
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
        super("Endermage", Material.END_PORTAL_FRAME,15);
        setMainKitItem(getDisplayMaterial());
        addEvents(ImmutableList.of(PlayerInteractEvent.class));
        addSetting(KitSettings.RADIUS, 4);
        addSetting(KitSettings.NUMBER, 5);
        addSetting(KitSettings.USES, 5);
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        EndermageSearch endermageRunnable = kitPlayer.getKitAttribute(this,EndermageSearch.class);
        if (endermageRunnable != null && endermageRunnable.isSearchingForPlayers) {
            endermageRunnable.endSearching();
        }
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Block endermagePortal = event.getClickedBlock();
        if (endermagePortal != null) {
            Player player = event.getPlayer();
            KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);

            if (player.hasMetadata(KitMetaData.INGLADIATOR.getKey())) {
                return;
            }

            EndermageSearch endermageRunnable = kitPlayer.getKitAttribute(this,EndermageSearch.class);
            if (endermageRunnable != null && endermageRunnable.isSearchingForPlayers) {
                player.sendMessage(Localization.INSTANCE.getMessage("endermage.alreadySearching", ChatUtils.getPlayerLocale(player)));
                return;
            }

            BlockData oldBlockData = endermagePortal.getBlockData();
            endermagePortal.setType(Material.END_PORTAL_FRAME);
            EndermageSearch newEndermageRunnable = new EndermageSearch(player, endermagePortal, oldBlockData);
            kitPlayer.putKitAttribute(this, newEndermageRunnable,EndermageSearch.class);
            newEndermageRunnable.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
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
        Player damager = (Player) event.getDamager();
        if (damager.hasMetadata(KitMetaData.HAS_BEEN_MAGED.getKey())) {
            event.setCancelled(true);
        }
    }
}






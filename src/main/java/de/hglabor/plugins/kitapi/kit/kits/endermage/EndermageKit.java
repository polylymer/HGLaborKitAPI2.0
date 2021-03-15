package de.hglabor.plugins.kitapi.kit.kits.endermage;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
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
    @IntArg
    private final int maxUses, invulnerabilityAfterMage, searchTime;
    @DoubleArg
    private final double searchRadius;
    private final String attributeKey, hasBeenMagedKey;
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private EndermageKit() {
        super("Endermage", Material.END_PORTAL_FRAME);
        cooldown = 15F;
        maxUses = 5;
        searchRadius = 4D;
        invulnerabilityAfterMage = 5;
        searchTime = 5;
        hasBeenMagedKey = this.getName() + "hasBeenMaged";
        attributeKey = this.getName() + "Runnable";
        setMainKitItem(getDisplayMaterial());
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        EndermageSearch endermageRunnable = kitPlayer.getKitAttribute(attributeKey);
        if (endermageRunnable != null && endermageRunnable.isSearchingForPlayers) {
            endermageRunnable.endSearching();
        }
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Block endermagePortal = event.getClickedBlock();
        if (endermagePortal != null && endermagePortal.getType() != Material.BEDROCK) {
            Player player = event.getPlayer();
            KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);

            if (player.hasMetadata(KitMetaData.INGLADIATOR.getKey())) {
                return;
            }

            EndermageSearch endermageRunnable = kitPlayer.getKitAttribute(attributeKey);
            if (endermageRunnable != null && endermageRunnable.isSearchingForPlayers) {
                player.sendMessage(Localization.INSTANCE.getMessage("endermage.alreadySearching", ChatUtils.getPlayerLocale(player)));
                return;
            }

            BlockData oldBlockData = endermagePortal.getBlockData();
            endermagePortal.setType(Material.END_PORTAL_FRAME);
            EndermageSearch newEndermageRunnable = new EndermageSearch(player, endermagePortal, oldBlockData);
            kitPlayer.putKitAttribute(attributeKey, newEndermageRunnable);
            newEndermageRunnable.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
        }
    }

    @EventHandler
    public void onMagedPlayerGetsDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (player.hasMetadata(hasBeenMagedKey)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMagedPlayerHitsEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        if (damager.hasMetadata(hasBeenMagedKey)) {
            event.setCancelled(true);
        }
    }

    public int getMaxUses() {
        return maxUses;
    }

    public double getSearchRadius() {
        return searchRadius;
    }

    public int getInvulnerabilityAfterMage() {
        return invulnerabilityAfterMage;
    }

    public int getSearchTime() {
        return searchTime;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public String getHasBeenMagedKey() {
        return hasBeenMagedKey;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}






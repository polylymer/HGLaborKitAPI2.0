package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.pvp.LastHitInformation;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.UUID;

public class KangarooKit extends AbstractKit implements Listener {
    public final static KangarooKit INSTANCE = new KangarooKit();

    // boost power is not linear beacuse of spigot vectors, use with caution
    @IntArg
    private final int boostUpPower;
    @IntArg
    private final int boostForwardPower;

    private HashSet<UUID> jumpingPlayers = new HashSet<>();


    protected KangarooKit() {
        super("Kangaroo", Material.FIREWORK_ROCKET);
        setMainKitItem(getDisplayMaterial());
        boostUpPower = 1;
        boostForwardPower = 1;
    }

    @KitEvent
    @EventHandler
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        handleKangarooEvent(event);
    }

    @KitEvent
    @EventHandler
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event) {
        handleKangarooEvent(event);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isOnGround()) {
            jumpingPlayers.remove(player.getUniqueId());
        }
    }

    private void handleKangarooEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Vector direction = player.getLocation().getDirection();

        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        LastHitInformation lastHitInformation = kitPlayer.getLastHitInformation();
        Player otherPlayer = lastHitInformation.getLastPlayer();
        boolean isInCombat = kitPlayer.isInCombat();

        // ich glaub hier muss irgendwas mit @KitEvent oder so ich hab die api immer noch nicht verstanden bitte überprüfen @norisk
        if (player.getInventory().getItemInMainHand().getType() == Material.FIREWORK_ROCKET) {
            event.setCancelled(true);
            if (jumpingPlayers.contains(player.getUniqueId())) return;

            boolean jumpingAway = !isLookingAt(player, otherPlayer.getLocation().add(0, otherPlayer.getEyeHeight(), 0));
            if (isInCombat)
                if (jumpingAway) {
                    Localization.INSTANCE.getMessage("kangaroo.jumpingAway", ChatUtils.getPlayerLocale(player));
                    return;
                }

            // delayed to prevent double jumps
            Bukkit.getScheduler().scheduleSyncDelayedTask(KitApi.getInstance().getPlugin(), () -> jumpingPlayers.add(player.getUniqueId()), 2);

            if (!player.isSneaking()) {
                Vector boost = direction.multiply(1.5 * boostForwardPower).setY(0.6);
                player.setVelocity(boost);
            } else {
                if (isInCombat) return;
                player.setVelocity(player.getVelocity().setY(0.9 * boostUpPower));
            }
        }
    }

    // mathe, nicht hinterfragen (rechnet aus ob man sich ungefähr in die richtung von dem gegner/target boosten will)
    private boolean isLookingAt(Player p, Location target) {
        Location head = p.getLocation().add(0, p.getEyeHeight(), 0);
        Vector look = p.getLocation().getDirection().normalize();
        Vector direction = head.subtract(target).toVector().normalize();
        Vector boost = p.getLocation().getDirection().multiply(1.5).setY(0);
        Location newLocation = p.getLocation().add(boost);

        if (target.distance(newLocation) > target.distance(p.getLocation())) return false;

        Vector cp = direction.crossProduct(look);
        double length = cp.length();

        return (length < 0.5);
    }
}
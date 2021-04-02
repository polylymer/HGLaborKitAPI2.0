package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.pvp.LastHitInformation;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import static de.hglabor.utils.localization.Localization.t;

public class KangarooKit extends AbstractKit {
    public final static KangarooKit INSTANCE = new KangarooKit();

    // boost power is not linear because of spigot vectors, use with caution
    @IntArg
    private final int boostUpPower;
    @IntArg
    private final int boostForwardPower;
    private final String isJumpingKey;

    private KangarooKit() {
        super("Kangaroo", Material.FIREWORK_ROCKET);
        setMainKitItem(getDisplayMaterial());
        boostUpPower = 1;
        boostForwardPower = 1;
        isJumpingKey = this.getName() + "isJumping";
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        kitPlayer.putKitAttribute(isJumpingKey, false);
    }

    @KitEvent
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        handleKangarooEvent(event);
    }

    @KitEvent
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event, KitPlayer kitPlayer) {
        handleKangarooEvent(event);
    }

    @KitEvent
    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {
        if (event.getPlayer().isOnGround() && kitPlayer.getKitAttributeOrDefault(isJumpingKey, false)) {
            kitPlayer.putKitAttribute(isJumpingKey, false);
        }
    }

    private void handleKangarooEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Vector direction = player.getLocation().getDirection();

        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        LastHitInformation lastHitInformation = kitPlayer.getLastHitInformation();
        Player otherPlayer = lastHitInformation.getLastPlayer();
        boolean isInCombat = kitPlayer.isInCombat();

        if (kitPlayer.getKitAttributeOrDefault(isJumpingKey, false)) {
            return;
        }
        // if (jumpingPlayers.contains(player.getUniqueId())) return;

        boolean jumpingAway = otherPlayer != null && !isLookingAt(player, otherPlayer.getLocation().add(0, otherPlayer.getEyeHeight(), 0));
        if (isInCombat && jumpingAway) {
            player.sendMessage(t("kangaroo.jumpingAway", ChatUtils.getPlayerLocale(player)));
            return;
        }

        // delayed to prevent double jumps
        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> kitPlayer.putKitAttribute(isJumpingKey, true), 2);

        if (!player.isSneaking()) {
            Vector boost = direction.multiply(1.5 * boostForwardPower).setY(0.6);
            player.setVelocity(boost);
        } else {
            if (isInCombat) {
                return;
            }
            player.setVelocity(player.getVelocity().setY(0.9 * boostUpPower));
        }
    }

    // mathe, nicht hinterfragen (rechnet aus ob man sich ungefähr in die richtung von dem gegner/target boosten will)
    private boolean isLookingAt(Player p, Location target) {
        Location head = p.getLocation().add(0, p.getEyeHeight(), 0);
        Vector look = p.getLocation().getDirection().normalize();
        Vector direction = head.subtract(target).toVector().normalize();
        Vector boost = p.getLocation().getDirection().multiply(1.5).setY(0);
        Location newLocation = p.getLocation().add(boost);

        if (target.distance(newLocation) > target.distance(p.getLocation())) {
            return false;
        }

        Vector cp = direction.crossProduct(look);
        double length = cp.length();

        return (length < 0.5);
    }
}

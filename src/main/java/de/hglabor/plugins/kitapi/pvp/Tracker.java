package de.hglabor.plugins.kitapi.pvp;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.kit.kits.analyst.AnalystKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.supplier.IPlayerList;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static de.hglabor.utils.localization.Localization.t;

public class Tracker implements Listener {
    private final double distance;
    private final IPlayerList playerList;

    public Tracker(double distance, IPlayerList playerList) {
        this.distance = distance;
        this.playerList = playerList;
    }

    @EventHandler
    public void onUseTracker(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Entity target = searchForCompassTarget(player);
        KitPlayer kitPlayer = playerList.getKitPlayer(player);
        if (event.getMaterial() == Material.COMPASS) {
            if (target == null) {
                player.sendMessage(t("hglabor.tracker.noTarget", ChatUtils.locale(player)));
            } else {
                player.setCompassTarget(target.getLocation());
                sendTrackingMessage(player, target, kitPlayer);
            }
        }
    }

    private void sendTrackingMessage(Player player, Entity target, KitPlayer kitPlayer) {
        if (kitPlayer.hasKit(AnalystKit.INSTANCE)) {
            KitPlayer targetKitPlayer = playerList.getKitPlayer((Player) target);
            player.sendMessage(t("hglabor.tracker.targetDetailed",
                    ImmutableMap.of(
                            "targetName", target.getName(),
                            "kits", targetKitPlayer.printKits(),
                            "distance", String.valueOf((int) player.getLocation().distance(target.getLocation()))),
                    ChatUtils.locale(player)));
        } else {
            player.sendMessage(t("hglabor.tracker.target", ImmutableMap.of("targetName", target.getName()), ChatUtils.locale(player)));
        }
    }

    private Entity searchForCompassTarget(Player tracker) {
        List<Pair<Entity, Double>> pairs = new ArrayList<>();
        for (Entity possibleTarget : playerList.getTrackingTargets()) {
            if (possibleTarget == null)
                continue;
            if (!tracker.getWorld().equals(possibleTarget.getWorld()))
                continue;
            if (tracker == possibleTarget)
                continue;
            double distanceBetween = getDistanceBetween(tracker, possibleTarget);
            if (distanceBetween > distance) {
                pairs.add(Pair.of(possibleTarget, distanceBetween));
            }
        }
        Optional<Pair<Entity, Double>> target = pairs.stream().min(Comparator.comparingDouble(Pair::getRight));
        return target.isEmpty() ? null : target.get().getLeft();
    }

    private double getDistanceBetween(Entity player, Entity player2) {
        Location location = player.getLocation().clone();
        Location location2 = player2.getLocation().clone();
        location.setY(0);
        location2.setY(0);
        return location.distanceSquared(location2);
    }
}



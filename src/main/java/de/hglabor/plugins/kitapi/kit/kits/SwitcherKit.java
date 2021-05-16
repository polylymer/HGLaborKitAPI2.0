package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class SwitcherKit extends AbstractKit implements Listener {
    public static final SwitcherKit INSTANCE = new SwitcherKit();
    private final List<Material> disabledBlocks = Arrays.asList(Material.AIR, Material.BARRIER, Material.BEDROCK, Material.END_PORTAL_FRAME);
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private SwitcherKit() {
        super("Switcher", Material.SNOWBALL);
        cooldown = 5;
        setKitItemPlaceable(true);
        setMainKitItem(getDisplayMaterial(), 16);
    }

    @KitEvent
    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Snowball)) return;
        e.getEntity().setMetadata(KitMetaData.SWITCHER_BALL.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent e) {
        if (!e.getEntity().hasMetadata(KitMetaData.SWITCHER_BALL.getKey())) {
            return;
        }
        if (e.getEntity().getShooter() == null) {
            return;
        }
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) e.getEntity().getShooter();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(shooter);
        if (!kitPlayer.isValid()) {
            return;
        }
        Location playerLoc = shooter.getLocation();

        if (e.getHitEntity() != null) {
            Entity hit = e.getHitEntity();
            if (hit instanceof Player) {
                KitPlayer kitEntity = KitApi.getInstance().getPlayer((Player) hit);
                if (!kitEntity.isValid()) {
                    return;
                }
            }
            Location hitLocation = hit.getLocation();
            shooter.teleport(hitLocation);
            hit.teleport(playerLoc);
            if (hit instanceof LivingEntity) {
                ((LivingEntity) hit).damage(1, shooter);
            }
            hit.sendMessage(ChatColor.LIGHT_PURPLE + "SWITCHEROOOOO");
            shooter.sendMessage(ChatColor.LIGHT_PURPLE + "SWITCHEROOOOO");
            KitApi.getInstance().getPlayer(shooter).activateKitCooldown(this);
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

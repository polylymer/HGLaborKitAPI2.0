package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

    private SwitcherKit() {
        super("Switcher", Material.SNOWBALL);
        setMainKitItem(getDisplayMaterial(), 16);
        addEvents(ImmutableList.of(ProjectileHitEvent.class, ProjectileLaunchEvent.class));
        setCooldown(5);
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        e.getEntity().setMetadata(KitMetaData.SWITCHER_BALL.getKey(), new FixedMetadataValue(KitManager.getInstance().getPlugin(), ""));
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
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(shooter);
        if (!kitPlayer.isValid()) {
            return;
        }
        Location playerLoc = shooter.getLocation();

        if (e.getHitEntity() != null) {
            Entity hit = e.getHitEntity();
            if (hit instanceof Player) {
                KitPlayer kitEntity = KitManager.getInstance().getPlayer((Player) hit);
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
            KitManager.getInstance().getPlayer(shooter).activateKitCooldown(this, this.getCooldown());
        }
    }
}

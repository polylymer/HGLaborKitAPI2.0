package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.mozilla.javascript.Kit;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SwitcherKit extends AbstractKit implements Listener {

    private final List<Material> disabledBlocks = Arrays.asList(Material.AIR, Material.BARRIER, Material.BEDROCK, Material.END_PORTAL_FRAME);

    public static final SwitcherKit INSTANCE = new SwitcherKit();

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
        e.getEntity().setCustomName("switcherBall");
    }

    @Override
    public void onProjectileHitEvent(ProjectileHitEvent e) {
        if (!(Objects.equals(e.getEntity().getCustomName(), "switcherBall"))) {
            return;
        }
        if (e.getEntity().getShooter() == null) {
            return;
        }
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) e.getEntity().getShooter();
        Location playerLoc = shooter.getLocation();

        if (e.getHitEntity() != null) {
            Entity hit = e.getHitEntity();
            Location hitLocation = hit.getLocation();

            shooter.teleport(hitLocation);
            hit.teleport(playerLoc);

            hit.sendMessage(ChatColor.LIGHT_PURPLE + "SWITCHEROOOOO");
            shooter.sendMessage(ChatColor.LIGHT_PURPLE + "SWITCHEROOOOO");

            KitManager.getInstance().getPlayer(shooter).activateKitCooldown(this, this.getCooldown());
        }
    }
}

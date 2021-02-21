package de.hglabor.plugins.kitapi.kit.kits.grappler;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Optional;

public class GrapplerKit extends AbstractKit implements Listener {
    public final static GrapplerKit INSTANCE = new GrapplerKit();
    private final ItemStack grapplerArrow;

    private GrapplerKit() {
        super("Grappler", Material.CROSSBOW, 60);
        this.grapplerArrow = new ItemBuilder(Material.ARROW).setName("Grappler Arrow").build();
        setMainKitItem(new ItemBuilder(Material.CROSSBOW).setUnbreakable(true).build());
        addSetting(KitSettings.USES, 3);
        addEvents(List.of(ProjectileLaunchEvent.class, PlayerInteractEvent.class));
    }

    @EventHandler
    public void onGrapplerArrowHitEvent(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.hasMetadata(KitMetaData.GRAPPLER_ARROW.getKey())) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (shooter != null) {
                removeGrapplerHook(projectile);
                Vector vector = getVectorForPoints(shooter.getLocation(), event.getEntity().getLocation());
                Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
                    shooter.setGravity(true);
                    shooter.setVelocity(vector);
                }, 0);
            }
            event.setCancelled(true);
            projectile.remove();
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Player player = (Player) event.getEntity().getShooter();
        if (player == null) {
            return;
        }
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (kitPlayer.getKitAttribute(this, Boolean.class)) {
            kitPlayer.putKitAttribute(this, false, Boolean.class);
            Arrow projectile = (Arrow) event.getEntity();
            projectile.setCritical(false);
            projectile.setMetadata(KitMetaData.GRAPPLER_ARROW.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
            GrapplerHookEntity grapplerHookEntity = new GrapplerHookEntity(((CraftPlayer) player).getHandle(), ((CraftWorld) player.getWorld()).getHandle(), 1, 1);
            ((CraftWorld) player.getWorld()).getHandle().addEntity(grapplerHookEntity);
            projectile.addPassenger(grapplerHookEntity.getBukkitEntity());
            KitApi.getInstance().checkUsesForCooldown(player, this);
        }
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        getCrossBow(player).ifPresent(itemStack -> {
            CrossbowMeta crossbowMeta = (CrossbowMeta) itemStack.getItemMeta();
            if (!crossbowMeta.hasChargedProjectiles()) {
                crossbowMeta.addChargedProjectile(grapplerArrow);
                kitPlayer.putKitAttribute(this, true, Boolean.class);
                itemStack.setItemMeta(crossbowMeta);
            }
        });
    }

    private Vector getVectorForPoints(Location l1, Location l2) {
        double t = l2.distance(l1);
        double vX = (1.0 + 0.07 * t) * (l2.getX() - l1.getX()) / t;
        double vY = (1.0 + 0.03 * t) * (l2.getY() - l1.getY()) / t - 0.5 * -0.08 * t;
        double vZ = (1.0 + 0.07 * t) * (l2.getZ() - l1.getZ()) / t;
        return new Vector(vX, vY, vZ);
    }

    private void removeGrapplerHook(Projectile projectile) {
        for (Entity passenger : projectile.getPassengers()) {
            if (((CraftEntity) passenger).getHandle() instanceof GrapplerHookEntity) {
                ((GrapplerHookEntity) ((CraftEntity) passenger).getHandle()).remove();
            }
        }
    }

    private Optional<ItemStack> getCrossBow(Player player) {
        if (player.getInventory().getItemInMainHand().isSimilar(getMainKitItem())) {
            return Optional.of(player.getInventory().getItemInMainHand());
        } else if (player.getInventory().getItemInOffHand().isSimilar(getMainKitItem())) {
            return Optional.of(player.getInventory().getItemInOffHand());
        }
        return Optional.empty();
    }
}

package de.hglabor.plugins.kitapi.kit.kits.grappler;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GrapplerKit extends AbstractKit implements Listener {
    public final static GrapplerKit INSTANCE = new GrapplerKit();
    private final ItemStack grapplerArrow;
    private final Map<UUID, Long> onCooldown;
    @IntArg
    private final int spamCooldown;
    @IntArg
    private final int maxUses;
    private final String hasShotKey;
    private final String projectileKey;
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private GrapplerKit() {
        super("Grappler", Material.CROSSBOW);
        cooldown = 45;
        spamCooldown = 2;
        maxUses = 2;
        hasShotKey = this.getName() + "hasShoot";
        this.projectileKey = this.getName() + "projectile";
        this.grapplerArrow = new ItemBuilder(Material.ARROW).setName("Grappler Arrow").build();
        this.onCooldown = new HashMap<>();
        setMainKitItem(new ItemBuilder(Material.CROSSBOW).setUnbreakable(true).build());
    }

    @EventHandler
    public void onGrapplerArrowHitEvent(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.hasMetadata(KitMetaData.GRAPPLER_ARROW.getKey())) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (shooter != null) {
                removeGrapplerHook(projectile);
                boolean inCombat = projectile.hasMetadata(KitMetaData.KITPLAYER_IS_IN_COMBAT.getKey());
                Vector vector = getVectorForPoints(shooter.getLocation(), event.getEntity().getLocation(), inCombat);
                Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
                    KitPlayer player = KitApi.getInstance().getPlayer(shooter);
                    if (!player.isValid() && !player.hasKit(this))
                        return;
                    shooter.setGravity(true);
                    shooter.setVelocity(vector);
                    if (inCombat)
                        shooter.sendMessage(Localization.INSTANCE.getMessage("grappler.inCombat", ChatUtils.getPlayerLocale(shooter)));
                }, 0);
            }
            event.setCancelled(true);
            projectile.remove();
        }
    }

    @KitEvent
    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Player player = (Player) event.getEntity().getShooter();
        if (player == null) {
            return;
        }
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (kitPlayer.getKitAttribute(hasShotKey)) {
            kitPlayer.putKitAttribute(hasShotKey, false);
            Arrow projectile = (Arrow) event.getEntity();
            kitPlayer.putKitAttribute(projectileKey, projectile);
            projectile.setCritical(false);
            projectile.setMetadata(KitMetaData.GRAPPLER_ARROW.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
            GrapplerHookEntity grapplerHookEntity = new GrapplerHookEntity(((CraftPlayer) player).getHandle(), ((CraftWorld) player.getWorld()).getHandle(), 1, 1);
            ((CraftWorld) player.getWorld()).getHandle().addEntity(grapplerHookEntity);
            projectile.addPassenger(grapplerHookEntity.getBukkitEntity());
            if (kitPlayer.isInCombat()) {
                projectile.setMetadata(KitMetaData.KITPLAYER_IS_IN_COMBAT.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
                KitApi.getInstance().checkUsesForCooldown(player, this, maxUses);
            }
        }
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (hasInternalCooldown(player)) {
            player.sendMessage(Localization.INSTANCE.getMessage("kit.spamPrevention", ChatUtils.getPlayerLocale(player)));
            return;
        }
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        getCrossBow(player).ifPresent(itemStack -> {
            CrossbowMeta crossbowMeta = (CrossbowMeta) itemStack.getItemMeta();
            if (!crossbowMeta.hasChargedProjectiles()) {
                crossbowMeta.addChargedProjectile(grapplerArrow);
                kitPlayer.putKitAttribute(hasShotKey, true);
                itemStack.setItemMeta(crossbowMeta);
                onCooldown.put(player.getUniqueId(), System.currentTimeMillis() + spamCooldown * 1000L);
            }
        });
    }

    @KitEvent
    @Override
    public void onKitPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if(kitPlayer == null || !kitPlayer.isValid()) {
            return;
        }
        if(kitPlayer.getKitAttribute(projectileKey) != null) {
            Projectile projectile = kitPlayer.getKitAttribute(projectileKey);
            removeGrapplerHook(projectile);
            projectile.remove();
        }
    }


    private boolean hasInternalCooldown(Player player) {
        if (onCooldown.containsKey(player.getUniqueId())) {
            Long coolDown = onCooldown.get(player.getUniqueId());
            if (System.currentTimeMillis() > coolDown) {
                onCooldown.remove(player.getUniqueId());
                return false;
            }
            return true;
        }
        return false;
    }

    private Vector getVectorForPoints(Location l1, Location l2, boolean inCombat) {
        final double boost = inCombat ? 0.1 : 1.0;
        final double height = inCombat ? -0.008 : -0.08;
        double t = l2.distance(l1);
        double vX = (boost + 0.07 * t) * (l2.getX() - l1.getX()) / t;
        double vY = (boost + 0.03 * t) * (l2.getY() - l1.getY()) / t - 0.5 * height * t;
        double vZ = (boost + 0.07 * t) * (l2.getZ() - l1.getZ()) / t;
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

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

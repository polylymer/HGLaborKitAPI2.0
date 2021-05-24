package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.kit.settings.LongArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import java.util.Random;

public class SwordmanKit extends AbstractKit {

    public static final SwordmanKit INSTANCE = new SwordmanKit();

    private final String runnableAttributeKey;
    private final String armorStandAttributeKey;
    @IntArg
    private int radius;
    @IntArg
    private int radiusY;
    @IntArg
    private int effectMultiplier;
    @DoubleArg
    private double damage;
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @LongArg
    private final long animationSpeed;
    @IntArg
    private int duration;

    protected SwordmanKit() {
        super("Swordman", Material.GOLDEN_SWORD);
        setMainKitItem(new ItemBuilder(getDisplayMaterial()).setUnbreakable(true).build());
        runnableAttributeKey = "swordmanRunnable";
        armorStandAttributeKey = "swordmanArmorStand";
        this.damage = 6.0;
        this.radius = 15;
        this.radiusY = 15;
        this.cooldown = 13f;
        this.animationSpeed = 3;
        this.effectMultiplier = 3;
        this.duration = 6;
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if(kitPlayer.getKitAttribute(armorStandAttributeKey) != null ) {
            ArmorStand armorStand = kitPlayer.getKitAttribute(armorStandAttributeKey);
            armorStand.remove();
            BukkitTask bukkitTask = kitPlayer.getKitAttribute(runnableAttributeKey);
            if(bukkitTask != null) {
                bukkitTask.cancel();
            }
        }
        Entity entity = null;
        for (Entity entities : player.getNearbyEntities(radius, radiusY, radius)) {
            if(entities instanceof LivingEntity) {
                if(entities instanceof ArmorStand) continue;
                if(entities instanceof Player) {
                    KitPlayer targetPlayer = KitApi.getInstance().getPlayer((Player) entities);
                    if(!targetPlayer.isValid()) {
                        continue;
                    }
                }
                entity = entities;
                break;
            }
        }
        if(entity == null) {
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, effectMultiplier, false, false));
        long activationTime = System.currentTimeMillis();
        BukkitTask task;
        LivingEntity finalEntity = (LivingEntity) entity;
        ArmorStand armorStand = spawnSword(player.getLocation());
        kitPlayer.putKitAttribute(armorStandAttributeKey, armorStand);
        task = Bukkit.getScheduler().runTaskTimer(KitApi.getInstance().getPlugin(), () -> {
            BukkitTask bukkitTask = kitPlayer.getKitAttribute(runnableAttributeKey);
            if(System.currentTimeMillis() >= activationTime + duration * 1000D) {
                armorStand.remove();
                if(bukkitTask != null) {
                    bukkitTask.cancel();
                }
            }
            if(finalEntity.isDead()) {
                armorStand.remove();
                if(bukkitTask != null) {
                    bukkitTask.cancel();
                }
            } else {
                if(armorStand.getLocation().distance(finalEntity.getLocation()) < 2.5) {
                    drawParticleCircle(finalEntity.getLocation(), 1.5);
                    finalEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, finalEntity.getLocation().clone().add(0,1.4,0), 1);
                    finalEntity.getWorld().playSound(finalEntity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                    finalEntity.damage(damage, player);
                } else {
                    int x = finalEntity.getLocation().getBlockX();
                    int y = finalEntity.getLocation().getBlockY();
                    int z = finalEntity.getLocation().getBlockZ();
                    Location armorStandLocation = armorStand.getLocation().clone();
                    if(armorStandLocation.getBlockX() > x) {
                        armorStandLocation.subtract(0.7, 0,0);
                    } else {
                        armorStandLocation.add(0.7, 0,0);
                    }
                    if(armorStandLocation.getBlockY() > y) {
                        armorStandLocation.subtract(0, 0.7,0);
                    } else {
                        armorStandLocation.add(0, 0.7,0);
                    }
                    if(armorStandLocation.getBlockZ() > z) {
                        armorStandLocation.subtract(0, 0,0.7);
                    } else {
                        armorStandLocation.add(0, 0,0.7);
                    }
                    armorStand.teleport(armorStandLocation);
                }
                armorStand.setRightArmPose(new EulerAngle(new Random().nextInt(270), new Random().nextInt(270), new Random().nextInt(270)));
            }
        }, 3, animationSpeed);
        kitPlayer.putKitAttribute(runnableAttributeKey, task);
        kitPlayer.activateKitCooldown(this);
    }

    private void drawParticleCircle(Location location, double yOffset) {
        Location loc = location.clone();
        loc.add(0, yOffset, 0);
        for (int i = 0; i < animationSpeed * 4; i++) {
            location.getWorld().spawnParticle(Particle.BLOCK_CRACK, new Location(loc.getWorld(), loc.getX(),loc.getY(),loc.getZ()), 2, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
        }
    }

    private ArmorStand spawnSword(Location location) {
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(false);
        armorStand.getEquipment().setItemInMainHand(new ItemBuilder(getMainKitItem()).setEnchantment(Enchantment.IMPALING, 1).build());
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
            armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
        }
        armorStand.setRightArmPose(new EulerAngle(90f, 90f, 90f));
        return armorStand;
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        BukkitTask task = kitPlayer.getKitAttribute(runnableAttributeKey);
        if (task != null) {
            task.cancel();
        }
        ArmorStand armorStand = kitPlayer.getKitAttribute(armorStandAttributeKey);
        if(armorStand != null) {
            armorStand.remove();
        }
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        BukkitTask task = kitPlayer.getKitAttribute(runnableAttributeKey);
        if (task != null) {
            task.cancel();
        }
        ArmorStand armorStand = kitPlayer.getKitAttribute(armorStandAttributeKey);
        if(armorStand != null) {
            armorStand.remove();
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.kit.settings.MaterialArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.WorldEditUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class GladiatorKit extends AbstractKit implements Listener {
    public final static GladiatorKit INSTANCE = new GladiatorKit();
    @IntArg(min = 3)
    private final int radius, height;
    @MaterialArg
    private final Material material;
    @DoubleArg
    private final double intruderDamage;
    @IntArg
    private final int witherEffectAfterXSeconds;
    private final String attributeKey;

    private GladiatorKit() {
        super("Gladiator", Material.IRON_BARS);
        setMainKitItem(getDisplayMaterial());
        radius = 15;
        height = 10;
        witherEffectAfterXSeconds = 120;
        intruderDamage = 5D;
        material = Material.GLASS;
        attributeKey = this.getName() + "Fight";
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        GladiatorFight value = kitPlayer.getKitAttribute(attributeKey);
        if (value != null) {
            value.endFight();
        }
    }

    @KitEvent
    @Override
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Player rightClicked) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (player.hasMetadata(KitMetaData.INGLADIATOR.getKey()) || rightClicked.hasMetadata(KitMetaData.INGLADIATOR.getKey())) {
            return;
        }

        //Prevent Hulk crash?
        rightClicked.getPassengers().forEach(Entity::leaveVehicle);

        rightClicked.setMetadata(KitMetaData.INGLADIATOR.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
        player.setMetadata(KitMetaData.INGLADIATOR.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));

        //Adding +2 so player cant build up and escape because 1 is somehow not enough
        Region gladiatorRegion = getGladiatorLocation(player.getLocation().clone().set(player.getLocation().getX(), 90, player.getLocation().getZ()), radius, height + 2);
        Location center = BukkitAdapter.adapt(world, gladiatorRegion.getCenter());

        WorldEditUtils.createCylinder(player.getWorld(), center, radius - 1, true, 1, material);
        WorldEditUtils.createCylinder(player.getWorld(), center, radius - 1, false, height, material);
        WorldEditUtils.createCylinder(player.getWorld(), center.clone().add(0, height - 1, 0), radius - 1, true, 1, material);

        //Execute later because async and blocks arent there yet
        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
            for (BlockVector3 blockVector3 : gladiatorRegion) {
                Block block = world.getBlockAt(BukkitAdapter.adapt(world, blockVector3));
                if (block.getType().isAir()) {
                    continue;
                }
                block.setMetadata(KitMetaData.GLADIATOR_BLOCK.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
            }
        }, 5);

        GladiatorFight gladiatorFight = new GladiatorFight(gladiatorRegion, kitPlayer, KitApi.getInstance().getPlayer(rightClicked), radius, height);
        kitPlayer.putKitAttribute(attributeKey, gladiatorFight);
        gladiatorFight.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
    }


    //one day stackoverflow haha
    private Region getGladiatorLocation(Location location, int radius, int height) {
        Random random = new Random();
        AbstractRegion region = new CylinderRegion(BukkitAdapter.adapt(location.getWorld()), BukkitAdapter.asBlockVector(location), Vector2.at(radius, radius), location.getBlockY(), location.getBlockY() + height);
        if (hasEnoughSpace(region)) {
            return region;
        } else {
            return getGladiatorLocation(location.add(random.nextBoolean() ? -10 : 10, 5, random.nextBoolean() ? -10 : 10), radius, height);
        }
    }

    private boolean hasEnoughSpace(Region region) {
        World world;
        if (region.getWorld() != null) {
            world = BukkitAdapter.adapt(region.getWorld());
        } else {
            return true;
        }
        for (BlockVector3 blockVector3 : region) {
            Location adapt = BukkitAdapter.adapt(world, blockVector3);
            if (!world.getWorldBorder().isInside(adapt)) {
                return false;
            }
            if (!world.getBlockAt(adapt).getType().isAir()) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata(KitMetaData.GLADIATOR_BLOCK.getKey())) {
            changeGladiatorBlock(event, block);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().stream().filter(block -> block.hasMetadata(KitMetaData.GLADIATOR_BLOCK.getKey())).forEach(block -> changeGladiatorBlock(event, block));
    }

    private void changeGladiatorBlock(Cancellable event, Block block) {
        event.setCancelled(true);
        if (block.getType().equals(material)) {
            block.setType(Material.GREEN_STAINED_GLASS);
        } else if (block.getType().equals(Material.GREEN_STAINED_GLASS)) {
            block.setType(Material.YELLOW_STAINED_GLASS);
        } else if (block.getType().equals(Material.YELLOW_STAINED_GLASS)) {
            block.setType(Material.RED_STAINED_GLASS);
        } else if (block.getType().equals(Material.RED_STAINED_GLASS)) {
            event.setCancelled(false);
        }
    }

    public Material getMaterial() {
        return material;
    }

    private class GladiatorFight extends BukkitRunnable {
        private final Region region;
        private final Player gladiator;
        private final Player enemy;
        private final KitPlayer gladiatorKitOwner;
        private final KitPlayer enemyKitOwner;
        private final World world;
        private final Location oldLocationGladiator;
        private final Location oldLocationEnemy;
        private final int radius;
        private final int height;
        private final Location center;
        private int timer;

        public GladiatorFight(Region region, KitPlayer gladiator, KitPlayer enemy, int radius, int height) {
            this.region = region;
            this.gladiatorKitOwner = gladiator;
            this.enemyKitOwner = enemy;
            this.gladiator = Bukkit.getPlayer(gladiator.getUUID());
            this.enemy = Bukkit.getPlayer(enemy.getUUID());
            this.world = BukkitAdapter.adapt(region.getWorld());
            this.center = BukkitAdapter.adapt(world, region.getCenter());
            this.oldLocationGladiator = this.gladiator.getLocation();
            this.oldLocationEnemy = this.enemy.getLocation();
            this.radius = radius;
            this.height = height;
            init();
        }

        public void init() {
            gladiatorKitOwner.getLastHitInformation().setLastPlayer(enemy);
            gladiatorKitOwner.getLastHitInformation().setPlayerTimeStamp(System.currentTimeMillis());
            enemyKitOwner.getLastHitInformation().setLastDamager(gladiator);
            enemyKitOwner.getLastHitInformation().setLastDamagerTimestamp(System.currentTimeMillis());
            gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 20));
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 20));
            gladiator.teleport(new Location(world, center.getX() + radius / 2D, center.getY() + 1, center.getZ(), 90, 0));
            enemy.teleport(new Location(world, center.getX() - radius / 2D, center.getY() + 1, center.getZ(), -90, 0));
        }

        @Override
        public void run() {
            if (!gladiator.isOnline() || !enemy.isOnline()) {
                endFight();
                return;
            }
            if (!gladiatorKitOwner.isValid() || !enemyKitOwner.isValid()) {
                endFight();
                return;
            }
            if (gladiator.getLocation().getY() < center.getY() || enemy.getLocation().getY() < center.getY()) {
                endFight();
                return;
            }
            if (region.contains(BukkitAdapter.asBlockVector(gladiator.getLocation())) &&
                    region.contains(BukkitAdapter.asBlockVector(enemy.getLocation()))) {
                timer++;
                damageIntruders();
                if (timer > witherEffectAfterXSeconds) {
                    gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 2));
                }
            } else {
                endFight();
            }
        }

        private void endFight() {
            gladiatorKitOwner.putKitAttribute(attributeKey, null);

            gladiator.removeMetadata(KitMetaData.INGLADIATOR.getKey(), KitApi.getInstance().getPlugin());
            enemy.removeMetadata(KitMetaData.INGLADIATOR.getKey(), KitApi.getInstance().getPlugin());

            gladiator.removePotionEffect(PotionEffectType.WITHER);
            enemy.removePotionEffect(PotionEffectType.WITHER);

            gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 20));
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 20));

            if (gladiatorKitOwner.isValid()) {
                gladiator.teleport(oldLocationGladiator);
            }

            if (enemyKitOwner.isValid()) {
                enemy.teleport(oldLocationEnemy);
            }

            for (BlockVector3 blockVector3 : region) {
                Block block = world.getBlockAt(BukkitAdapter.adapt(world, blockVector3));
                block.removeMetadata(KitMetaData.GLADIATOR_BLOCK.getKey(), KitApi.getInstance().getPlugin());
            }

            gladiatorKitOwner.activateKitCooldown(GladiatorKit.this);

            WorldEditUtils.createCylinder(world, center, radius, true, height, Material.AIR);
            cancel();
        }

        private void damageIntruders() {
            for (Player unknownPlayer : Bukkit.getOnlinePlayers()) {
                if (!unknownPlayer.getGameMode().equals(GameMode.SURVIVAL)) continue;
                if (unknownPlayer == gladiator || unknownPlayer == enemy) continue;
                if (region.contains(BukkitAdapter.asBlockVector(unknownPlayer.getLocation())))
                    unknownPlayer.damage(intruderDamage);
            }
        }
    }
}

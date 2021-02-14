package de.hglabor.plugins.kitapi.kit.kits;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.WorldEditUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
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

import java.util.Collections;
import java.util.Random;

import static de.hglabor.plugins.kitapi.kit.config.KitSettings.MATERIAL;

public class GladiatorKit extends AbstractKit implements Listener {
    public final static GladiatorKit INSTANCE = new GladiatorKit();

    private GladiatorKit() {
        super("Gladiator", Material.IRON_BARS);
        setMainKitItem(getDisplayMaterial());
        addSetting(MATERIAL, Material.GLASS);
        addSetting(KitSettings.RADIUS, 11);
        addSetting(KitSettings.HEIGHT, 10);
        addEvents(Collections.singletonList(PlayerInteractAtEntityEvent.class));
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        GladiatorFight value = kitPlayer.getKitAttribute(this);
        if (value != null) {
            value.endFight();
        }
    }

    @Override
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Player enemy = (Player) event.getRightClicked();
        World world = player.getWorld();
        int radius = getSetting(KitSettings.RADIUS);
        int height = getSetting(KitSettings.HEIGHT);
        Material material = getSetting(KitSettings.MATERIAL);

        if (player.hasMetadata(KitMetaData.INGLADIATOR.getKey()) || enemy.hasMetadata(KitMetaData.INGLADIATOR.getKey())) {
            return;
        }

        enemy.setMetadata(KitMetaData.INGLADIATOR.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
        player.setMetadata(KitMetaData.INGLADIATOR.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));

        Region gladiatorRegion = getGladiatorLocation(player.getLocation().clone().set(player.getLocation().getX(), 90, player.getLocation().getZ()), radius, height);
        Location center = BukkitAdapter.adapt(world, gladiatorRegion.getCenter());

        WorldEditUtils.createCylinder(player.getWorld(), center, radius - 1, true, 1, material);
        WorldEditUtils.createCylinder(player.getWorld(), center, radius - 1, false, height, material);
        WorldEditUtils.createCylinder(player.getWorld(), center.clone().add(0, height - 1, 0), radius - 1, true, 1, material);


        for (BlockVector3 blockVector3 : gladiatorRegion) {
            Block block = world.getBlockAt(BukkitAdapter.adapt(world, blockVector3));
            block.setMetadata(KitMetaData.GLADIATOR_BLOCK.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
        }

        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        GladiatorFight gladiatorFight = new GladiatorFight(gladiatorRegion, kitPlayer, KitApi.getInstance().getPlayer(enemy), radius, height);
        kitPlayer.putKitAttribute(this, gladiatorFight);
        gladiatorFight.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
    }


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
            if (!world.getBlockAt(BukkitAdapter.adapt(world, blockVector3)).getType().isAir()) {
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
        if (block.getType().equals(getSetting(MATERIAL))) {
            block.setType(Material.GREEN_STAINED_GLASS);
        } else if (block.getType().equals(Material.GREEN_STAINED_GLASS)) {
            block.setType(Material.YELLOW_STAINED_GLASS);
        } else if (block.getType().equals(Material.YELLOW_STAINED_GLASS)) {
            block.setType(Material.RED_STAINED_GLASS);
        } else if (block.getType().equals(Material.RED_STAINED_GLASS)) {
            event.setCancelled(false);
        }
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
                if (timer > 120) {
                    gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 2));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 2));
                }
            } else {
                endFight();
            }
        }

        private void endFight() {
            gladiatorKitOwner.putKitAttribute(GladiatorKit.this, null);

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

            gladiatorKitOwner.activateKitCooldown(GladiatorKit.this, GladiatorKit.this.getCooldown());

            WorldEditUtils.createCylinder(world, center, radius, true, height, Material.AIR);
            cancel();
        }

        private void damageIntruders() {
            for (Player unknownPlayer : Bukkit.getOnlinePlayers()) {

                if (!unknownPlayer.getGameMode().equals(GameMode.SURVIVAL)) {
                    continue;
                }

                if (unknownPlayer == gladiator || unknownPlayer == enemy) {
                    continue;
                }

                if (region.contains(BukkitAdapter.asBlockVector(unknownPlayer.getLocation()))) {
                    unknownPlayer.damage(5);
                }
            }
        }
    }
}

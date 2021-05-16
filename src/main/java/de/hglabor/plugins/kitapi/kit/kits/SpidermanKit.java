package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.kit.settings.MaterialArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.CircleUtils;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;


public class SpidermanKit extends AbstractKit implements Listener {
    public static final SpidermanKit INSTANCE = new SpidermanKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @IntArg
    private final int spidernetRadius, spidernetHeight;
    @MaterialArg
    private final Material spidernetBlock;
    @DoubleArg
    private final double climbVelocity, shootingVelocity;
    private final String spidermanSnowballKey;

    private SpidermanKit() {
        super("Spiderman", Material.COBWEB);
        cooldown = 45;
        spidernetRadius = 5;
        spidernetHeight = 5;
        spidernetBlock = Material.COBWEB;
        climbVelocity = 0.3D;
        shootingVelocity = 1.5D;
        spidermanSnowballKey = this.getName() + "spidermanSnowball";
        setMainKitItem(getDisplayMaterial());
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);

        kitPlayer.activateKitCooldown(this);

        final Vector direction = player.getEyeLocation().getDirection();

        Snowball snowball = player.getWorld().spawn(player.getEyeLocation().add(direction.multiply(shootingVelocity)), Snowball.class);
        snowball.setVelocity(direction);
        snowball.setShooter(player);
        snowball.setMetadata(spidermanSnowballKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(snowball.getEntityId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }

        FallingBlock cobWeb = player.getWorld().spawnFallingBlock(player.getEyeLocation().add(direction), spidernetBlock.createBlockData());
        cobWeb.setHurtEntities(true);
        cobWeb.setVelocity(direction);
        cobWeb.setDropItem(false);
        snowball.addPassenger(cobWeb);
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }
        if (!event.getEntity().hasMetadata(spidermanSnowballKey)) {
            return;
        }

        Snowball snowball = (Snowball) event.getEntity();
        Set<Block> spiderNet = new HashSet<>();

        if (event.getHitEntity() != null) {
            Entity hittedEntity = event.getHitEntity();
            spiderNet = createSpiderNet(hittedEntity.getLocation(), spidernetRadius, spidernetHeight);
        }

        if (event.getHitBlock() != null) {
            Block hittedBlock = event.getHitBlock();
            spiderNet = createSpiderNet(hittedBlock.getLocation(), spidernetRadius, spidernetHeight);
        }

        Set<Block> finalSpiderNet = spiderNet;
        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
            for (Block block : finalSpiderNet) {
                if (block.getType().equals(spidernetBlock)) {
                    block.setType(Material.AIR);
                }
            }
        }, 20 * 10);

        snowball.remove();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (!kitPlayer.hasKit(this) || !KitApi.getInstance().hasKitItemInAnyHand(player, this) || kitPlayer.areKitsDisabled()) {
            return;
        }
        if (nearWall(0.5, event.getPlayer())) {
            if (kitPlayer.getKitCooldown(this).hasCooldown()) {
                player.sendActionBar(Localization.INSTANCE.getMessage("spiderman.noClimbWithCooldown", ChatUtils.locale(player)));
                return;
            }
            player.setVelocity(new Vector(0, climbVelocity, 0));
        }
    }

    public boolean nearWall(double distance, Player player) {
        Vector locale = player.getLocation().toVector();
        int y = locale.getBlockY() + 1;
        double x = locale.getX(), z = locale.getZ();
        World world = player.getWorld();
        Block b1 = world.getBlockAt(new Location(world, x + distance, y, z));
        Block b2 = world.getBlockAt(new Location(world, x - distance, y, z));
        Block b3 = world.getBlockAt(new Location(world, x, y, z + distance));
        Block b4 = world.getBlockAt(new Location(world, x, y, z - distance));
        return (b1.getType().isSolid()) || (b2.getType().isSolid()) || (b3.getType().isSolid()) || (b4.getType().isSolid());
    }

    private Set<Block> createSpiderNet(Location startLocation, int radius, int height) {
        Set<Block> result = new HashSet<>();
        for (Location location : CircleUtils.makeCircle(startLocation, radius, height, true, true, 0)) {
            if (!location.getBlock().getType().equals(Material.AIR)) {
                continue;
            }
            result.add(location.getBlock());
            location.getBlock().setType(spidernetBlock);
        }
        return result;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

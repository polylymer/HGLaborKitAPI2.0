package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.CircleUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GardenerKit extends AbstractKit implements Listener {
    public final static GardenerKit INSTANCE = new GardenerKit();
    private final List<Material> destructibleBlocks;

    @IntArg
    private final int radius;
    @IntArg
    private final int heightRadius;
    private final String blockListKey;
    private final String gardenerBushKey;
    private final List<Material> ackerMaterials;

    private GardenerKit() {
        super("Gardener", Material.SWEET_BERRIES);
        destructibleBlocks = Arrays.asList(
                Material.AIR,
                Material.GRASS,
                Material.TALL_GRASS,
                Material.SNOW,
                Material.DEAD_BUSH
        );
        ackerMaterials = Arrays.asList(Material.GRASS_BLOCK, Material.DIRT, Material.FARMLAND);
        radius = 5;
        heightRadius = 3;
        blockListKey = this.getName() + "blockListKey";
        gardenerBushKey = this.getName() + "gardenerBushKey";
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        Set<Block> blocks = kitPlayer.getKitAttributeOrDefault(blockListKey, new HashSet<>());
        for (Block block : blocks) {
            if (block.getType().equals(Material.SWEET_BERRY_BUSH)) {
                block.removeMetadata(gardenerBushKey, KitApi.getInstance().getPlugin());
                block.setType(Material.AIR);
            }
        }
        blocks.clear();
    }

    @KitEvent
    public void onPlayerIsSneakingEvent(PlayerToggleSneakEvent event, KitPlayer kitPlayer) {
        Player player = event.getPlayer();
        Set<Block> blocks = new HashSet<>();
        for (int i = -heightRadius; i < heightRadius; i++) {
            Set<Location> locations = CircleUtils.makeCircle(player.getLocation(), radius, 1, false, false, i);
            for (Location location : locations) {
                Block block = location.getBlock();
                if (destructibleBlocks.contains(block.getType()) && ackerMaterials.contains(block.getRelative(BlockFace.DOWN).getType())) {
                    block.setType(Material.SWEET_BERRY_BUSH);
                    //Make Bush grow haha
                    if (block.getBlockData() instanceof Ageable) {
                        Ageable ageable = (Ageable) block.getBlockData();
                        ageable.setAge(2);
                        block.setBlockData(ageable);
                    }
                    block.setMetadata(gardenerBushKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
                    blocks.add(block);
                }
            }
        }
        kitPlayer.putKitAttribute(blockListKey, blocks);
    }

    @KitEvent
    public void onPlayerIsNotSneakingAnymoreEvent(PlayerToggleSneakEvent event, KitPlayer kitPlayer) {
        onDeactivation(kitPlayer);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata(gardenerBushKey)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(EntityDamageByBlockEvent event) {
        Block block = event.getDamager();
        if (block == null) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            KitPlayer player = KitApi.getInstance().getPlayer((Player) event.getEntity());
            if (!player.hasKit(this)) {
                return;
            }
            if (block.hasMetadata(gardenerBushKey)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock.hasMetadata(gardenerBushKey)) {
            event.setCancelled(true);
        }
    }

    /*  @KitEvent
    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {
        Block fromBlock = event.getFrom().getBlock();
        if (fromBlock.equals(event.getTo().getBlock())) {
            return;
        }

        Block[] lastBlocks = kitPlayer.getKitAttribute(lastBlocksKey);
        if (lastBlocks[2] == null || !fromBlock.equals(lastBlocks[2])) {
            Block old2 = lastBlocks[2];
            Block old1 = lastBlocks[1];
            Block old0 = lastBlocks[0];
            lastBlocks[2] = fromBlock;
            lastBlocks[1] = old2;
            lastBlocks[0] = old1;
            if (old0 != null && old0.getType().equals(Material.SWEET_BERRY_BUSH)) {
                old0.setType(Material.AIR);
            }
        }

        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
            for (int i = 0; i < lastBlocks.length; i++) {
                Block block = lastBlocks[i];
                if (block != null) {
                    if (!block.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS_BLOCK)) {
                        continue;
                    }
                    if (!destructibleBlocks.contains(block.getType())) {
                        continue;
                    }
                    block.setType(Material.SWEET_BERRY_BUSH);
                    Ageable blockData = (Ageable) block.getBlockData();
                    blockData.setAge(i);
                    block.setBlockData(blockData);
                }
            }
        }, delayInTicks);
    } */


    public String getGardenerBushKey() {
        return gardenerBushKey;
    }
}

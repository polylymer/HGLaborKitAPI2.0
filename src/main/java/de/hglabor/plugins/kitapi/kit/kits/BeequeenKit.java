package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.items.KitItemBuilder;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftBee;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeequeenKit extends AbstractKit implements Listener {
    public final static BeequeenKit INSTANCE = new BeequeenKit();

    @IntArg
    private final int honeyDurationInSeconds, movementTick, beeAmount;
    @FloatArg(min = 0.0F)
    private final float cooldown;
    private final String honeyTrailKey;
    private final String isHoneyBlockKey;

    private BeequeenKit() {
        super("Beequeen", Material.HONEY_BLOCK);
        mainKitItem = new KitItemBuilder(Material.HONEYCOMB).build();
        honeyDurationInSeconds = 7;
        cooldown = 35F;
        movementTick = 1;
        beeAmount = 3;
        honeyTrailKey = this.getName() + "honeyTrailKey";
        isHoneyBlockKey = this.getName() + "honeyBlockKey";
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        HoneyTrail honeyTrail = kitPlayer.getKitAttribute(honeyTrailKey);
        if (honeyTrail != null) {
            honeyTrail.cancel();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().hasMetadata(isHoneyBlockKey)) {
            event.setCancelled(true);
        }
    }

    @KitEvent
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Player rightClicked) {
        kitPlayer.activateKitCooldown(this);
        HoneyTrail honeyTrail = new HoneyTrail(rightClicked);
        honeyTrail.runTaskTimer(KitApi.getInstance().getPlugin(), 0, movementTick);
        kitPlayer.putKitAttribute(honeyTrailKey, honeyTrail);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    private final class HoneyTrail extends BukkitRunnable {
        private final Player player;
        private final KitPlayer kitPlayer;
        private final BlockFace[] directions;
        private final long endTime;
        private final Map<BlockFace, Block> currentHoneyBlocks;
        private final Map<BlockFace, BlockData> oldFaceBlockData;
        private final Map<Block, BlockData> oldBlockData;
        private final List<Bee> bees;

        private HoneyTrail(Player player) {
            this.player = player;
            this.oldBlockData = new HashMap<>();
            this.kitPlayer = KitApi.getInstance().getPlayer(player);
            this.endTime = System.currentTimeMillis() + honeyDurationInSeconds * 1000L;
            this.directions = new BlockFace[]{
                    BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.SELF,
                    BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST, BlockFace.SOUTH_EAST
            };
            this.bees = new ArrayList<>();
            this.currentHoneyBlocks = new HashMap<>();
            this.oldFaceBlockData = new HashMap<>();
            for (int i = 0; i < beeAmount; i++) {
                Bee bee = (Bee) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.BEE);
                bees.add(bee);
            }
            //TODO custom pathfinder wies aussieht
            makeBeesAngry();
        }

        @Override
        public void run() {
            if (player.isDead() || !kitPlayer.isValid()) {
                stop();
                return;
            }

            if (System.currentTimeMillis() > endTime) {
                stop();
                return;
            }

            makeBeesAngry();

            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (player.getLocation().getBlock().getType().equals(Material.HONEY_BLOCK)) {
                return;
            }

            //At this point I dont care anymore
            for (BlockFace direction : directions) {
                Block relative = block.getRelative(direction);
                if (relative.getType().isSolid() && !relative.getType().equals(Material.HONEY_BLOCK) && !Utils.isUnbreakableLaborBlock(relative)) {

                    if (currentHoneyBlocks.containsKey(direction)) {
                        Block toReplace = currentHoneyBlocks.get(direction);
                        BlockData blockData = oldFaceBlockData.getOrDefault(direction,Material.DIAMOND_BLOCK.createBlockData());
                        if (blockData != null) {
                            toReplace.setBlockData(blockData);
                            toReplace.removeMetadata(isHoneyBlockKey, KitApi.getInstance().getPlugin());
                            oldBlockData.remove(toReplace);
                        }
                    }

                    BlockData clone = relative.getBlockData().clone();
                    oldBlockData.put(relative, clone);
                    relative.setMetadata(isHoneyBlockKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
                    relative.setType(Material.HONEY_BLOCK);

                    currentHoneyBlocks.put(direction, relative);
                    oldFaceBlockData.put(direction, clone);
                }
            }
        }

        private void makeBeesAngry() {
            for (Bee bee : bees) {
                bee.setAnger(5);
                bee.setTarget(player);
                //TODO still trash custom pathfinder would be better
                ((CraftBee) bee).getHandle().anger();
                ((CraftBee) bee).getHandle().setAngerTarget(player.getUniqueId());
            }
        }

        private void stop() {
            cancel();
            oldBlockData.keySet().forEach(block -> {
                block.setBlockData(oldBlockData.get(block));
                block.removeMetadata(isHoneyBlockKey, KitApi.getInstance().getPlugin());
            });
            bees.forEach(Entity::remove);
        }
    }
}

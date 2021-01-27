package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collections;

public class ThorKit extends AbstractKit implements Listener {
    public static final ThorKit INSTANCE = new ThorKit();

    protected ThorKit() {
        super("Thor", Material.WOODEN_AXE, 10);
        setMainKitItem(getDisplayMaterial(), true);
        addEvents(Collections.singletonList(PlayerInteractEvent.class));
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Location location = event.getClickedBlock().getLocation();
        World world = location.getWorld();
        Location highestBlockLocation = world.getHighestBlockAt(location).getLocation();
        Block highestBlock = highestBlockLocation.getBlock();
        Boolean ifExploded = false;
        if (!world.isClearWeather()) {
            world.strikeLightning(highestBlockLocation.add(1, 0, 0));
            world.strikeLightning(highestBlockLocation.add(0, 0, 1));
            world.strikeLightning(highestBlockLocation.add(1, 0, 1));
        } else {
            world.strikeLightning(highestBlockLocation);
        }
        if (highestBlockLocation.getBlockY() > 80) {
            if (highestBlock.getType() == Material.NETHERRACK && highestBlock.hasMetadata(KitMetaData.THOR_BLOCK.getKey())) {
                highestBlock.setType(Material.AIR);
                ifExploded = true;
            } else {
                Block block = highestBlockLocation.add(0, 1, 0).getBlock();
                block.setType(Material.NETHERRACK);
                block.setMetadata(KitMetaData.THOR_BLOCK.getKey(), new FixedMetadataValue(KitManager.getInstance().getPlugin(), ""));
            }
        }
        if (!ifExploded)
            world.getHighestBlockAt(location).getLocation().add(0, 1, 0).getBlock().setType(Material.FIRE);
        else
            highestBlock.getLocation().getWorld().createExplosion(highestBlockLocation, 4, true, true, event.getPlayer());
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(event.getPlayer());
        kitPlayer.activateKitCooldown(this, this.getCooldown());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        event.blockList().stream().filter(block -> block.getType() == Material.NETHERRACK).forEach(block -> {
            if (block.getY() > 80) {
                if (block.hasMetadata(KitMetaData.THOR_BLOCK.getKey())) {
                    block.setType(Material.AIR);
                    block.getLocation().getWorld().createExplosion(block.getLocation(), 4, true, true, player);
                }
            }
        });
    }
}

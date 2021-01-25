package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;

import de.hglabor.plugins.kitapi.kit.KitManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Collections;
import java.util.Objects;

/**
 * Hommage an Waffel :) (wtf?)
 */
public class JackhammerKit extends AbstractKit {
    public final static JackhammerKit INSTANCE = new JackhammerKit();

    private JackhammerKit() {
        super("Jackhammer", Material.STONE_AXE);
        setMainKitItem(getDisplayMaterial(),true);
        addEvents(Collections.singletonList(BlockBreakEvent.class));
    }

    @Override
    public void onBlockBreakWithKitItem(BlockBreakEvent e) {
        Block block = e.getBlock();
        Material above = block.getRelative(BlockFace.UP).getType();
        Material below = block.getRelative(BlockFace.DOWN).getType();

        if (above != Material.AIR && below == Material.AIR) {
            //HOCH
            dig(block.getLocation(), 1, 1);
        } else if (above == Material.AIR) {
            //RUNTER
            dig(block.getLocation(), -1, 1);
        } else {
            //BEIDE RICHTUNGEN
            dig(block.getLocation(), 1, 2);
            dig(block.getLocation(), -1, 2);
        }
    }

    /**
     * @param loc       Location to start
     * @param direction -1 = down; 1 = up; 0 = both
     */
    private void dig(Location loc, int direction, int delay) {
        final Location currentLocation = loc.clone();

        Bukkit.getScheduler().runTaskTimer(KitManager.getInstance().getPlugin(), r -> {
            currentLocation.getBlock().setType(Material.AIR);
            loc.getWorld().spawnParticle(Particle.ASH, currentLocation.clone().add(.5, 0, .5), 100);
            currentLocation.add(0, direction, 0);
            if (direction == 1 && currentLocation.getBlockY() >= currentLocation.getWorld().getMaxHeight()) {
                r.cancel();
            }
            if (currentLocation.getBlock().getType() == Material.BEDROCK) {
                r.cancel();
            }
        }, 0, delay);
    }
}

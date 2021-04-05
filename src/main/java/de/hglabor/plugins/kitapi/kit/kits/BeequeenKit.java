package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;

public class BeequeenKit extends AbstractKit {

    public final static BeequeenKit INSTANCE = new BeequeenKit();


    private BeequeenKit() {
        super("Beequeen", Material.HONEY_BLOCK);
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (!(entity instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player hittedPlayer = (Player) entity;
        Block block = hittedPlayer.getLocation().add(damager.getLocation().getDirection().normalize().multiply(1.5)).getBlock();
        List<Block> blocks = Arrays.asList(
                block,
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.WEST),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH)
        );

        List<Material> oldBlocks = Arrays.asList(
                block.getType(),
                block.getRelative(BlockFace.EAST).getType(),
                block.getRelative(BlockFace.WEST).getType(),
                block.getRelative(BlockFace.NORTH).getType(),
                block.getRelative(BlockFace.SOUTH).getType()
        );

        if (block.getType() == Material.AIR || block.getType() == Material.GRASS) return;

        blocks.forEach(
                b -> b.setType(Material.HONEY_BLOCK)
        );

        Bukkit.getScheduler().scheduleSyncDelayedTask(KitApi.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (var i = 0; i < blocks.size(); i++) {
                    Material oldBlock = oldBlocks.get(i);
                    if (oldBlock == Material.HONEY_BLOCK) oldBlock = Material.HONEYCOMB_BLOCK;
                    blocks.get(i).setType(oldBlock);
                }
            }
        }, 20);
    }
}

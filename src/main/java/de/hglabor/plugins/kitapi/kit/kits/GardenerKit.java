package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GardenerKit extends AbstractKit {
    public final static GardenerKit INSTANCE = new GardenerKit();

    private GardenerKit() {
        super("Gardener", Material.SWEET_BERRIES);
    }

    List<Material> destructibleBlocks = Arrays.asList(
            Material.AIR,
            Material.GRASS,
            Material.TALL_GRASS,
            Material.SNOW,
            Material.DEAD_BUSH
    );


    @KitEvent
    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {

        Bukkit.broadcastMessage(event.getFrom().toString());

        if (true) return;

        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (!player.isOnGround()) return;
        if (event.getFrom().getZ() == event.getTo().getZ() && event.getFrom().getX() == event.getTo().getX()) return;

        Block blockBehind = playerLocation.subtract(playerLocation.getDirection().normalize().multiply(3).setY(0)).getBlock();
        if (destructibleBlocks.contains(blockBehind.getType())) {
            if (blockBehind.getLocation() == player.getLocation().getBlock().getLocation()) return; // keine ahnung denke das kann raus XDD

            if (blockBehind.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) return;
            blockBehind.setType(Material.SWEET_BERRY_BUSH);

            for (int i = 0; i < new Random().nextInt(3); i++) {
                blockBehind.applyBoneMeal(BlockFace.UP); // warum auch immer kann man diese scheiss beeren büsche nicht als Ageable casten ohne packets zu benutzen
                // hab nochmal getestet geht nich ich fick spigot
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(KitApi.getInstance().getPlugin(), () -> blockBehind.setType(Material.AIR), 3 * 20);
        }
    }

    @KitEvent
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT)
            event.setCancelled(true);
    }

}

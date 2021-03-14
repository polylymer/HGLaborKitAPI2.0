package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
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

import java.util.stream.IntStream;

public class ThorKit extends AbstractKit implements Listener {
    public static final ThorKit INSTANCE = new ThorKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @IntArg
    private final int netherrackHeight, amountOfLightnings;
    @FloatArg
    private final float netherrackPower;
    private final String thorBlockKey;

    private ThorKit() {
        super("Thor", Material.WOODEN_AXE);
        cooldown = 10;
        netherrackHeight = 80;
        netherrackPower = 4F;
        amountOfLightnings = 1;
        thorBlockKey = this.getName() + "thorBlock";
        setMainKitItem(getDisplayMaterial(), true);
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Location location = event.getClickedBlock().getLocation();
        World world = location.getWorld();
        Location highestBlockLocation = world.getHighestBlockAt(location).getLocation();
        Block highestBlock = highestBlockLocation.getBlock();
        boolean ifExploded = false;
        if (!world.isClearWeather()) {
            world.strikeLightning(highestBlockLocation.add(1, 0, 0));
            world.strikeLightning(highestBlockLocation.add(0, 0, 1));
            world.strikeLightning(highestBlockLocation.add(1, 0, 1));
        } else {
            IntStream.range(0, amountOfLightnings).mapToObj(i -> highestBlockLocation).forEach(world::strikeLightning);
        }
        if (highestBlockLocation.getBlockY() > netherrackHeight) {
            if (highestBlock.getType() == Material.NETHERRACK && highestBlock.hasMetadata(thorBlockKey)) {
                highestBlock.setType(Material.AIR);
                ifExploded = true;
            } else {
                Block block = highestBlockLocation.add(0, 1, 0).getBlock();
                block.setType(Material.NETHERRACK);
                block.setMetadata(thorBlockKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
            }
        }
        if (!ifExploded)
            world.getHighestBlockAt(location).getLocation().add(0, 1, 0).getBlock().setType(Material.FIRE);
        else
            highestBlock.getLocation().getWorld().createExplosion(highestBlockLocation, netherrackPower, true, true, event.getPlayer());
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(event.getPlayer());
        kitPlayer.activateKitCooldown(this);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        event.blockList().stream().filter(block -> block.getType() == Material.NETHERRACK).forEach(block -> {
            if (block.getY() > netherrackHeight) {
                if (block.hasMetadata(thorBlockKey)) {
                    block.setType(Material.AIR);
                    block.getLocation().getWorld().createExplosion(block.getLocation(), netherrackPower, true, true, player);
                }
            }
        });
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

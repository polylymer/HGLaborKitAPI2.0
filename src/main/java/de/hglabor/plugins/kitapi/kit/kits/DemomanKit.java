package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.events.KitEventHandler;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class DemomanKit extends AbstractKit implements Listener {
    public final static DemomanKit INSTANCE = new DemomanKit();

    @FloatArg(min = 0.1F, max = 100F)
    private final float sandExplosionSize;
    @FloatArg(min = 0.1F, max = 100F)
    private final float gravelExplosionSize;
    @FloatArg(min = 0.1F, max = 100F)
    private final float concreteExplosionSize;
    private final String demomanPlateKey;


    private DemomanKit() {
        super("Demoman", Material.GRAVEL);
        addAdditionalKitItems(new ItemStack(Material.GRAVEL, 8));
        addAdditionalKitItems(new ItemStack(Material.STONE_PRESSURE_PLATE, 8));
        this.setKitItemPlaceable(true);
        sandExplosionSize = 1;
        gravelExplosionSize = 4;
        concreteExplosionSize = 9;
        demomanPlateKey = "demomanPlate";
    }

    @KitEvent
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block eventBlock = event.getBlock();
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);

        if (!Tag.WOODEN_PRESSURE_PLATES.isTagged(eventBlock.getType()) && !Tag.STONE_PRESSURE_PLATES.isTagged(eventBlock.getType()) && !Tag.CARPETS.isTagged(eventBlock.getType()))
            return;
        if (!KitEventHandler.canUseKit(event, kitPlayer, this)) {
            event.setCancelled(true);
            return;
        }

        event.getBlockPlaced().setMetadata(demomanPlateKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), player.getName()));
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.isOnGround())
            return;
        if (event.getTo().distanceSquared(event.getFrom()) == 0)
            return;

        Block eventBlock = event.getTo().getBlock();

        if (!Tag.WOODEN_PRESSURE_PLATES.isTagged(eventBlock.getType()) && !Tag.STONE_PRESSURE_PLATES.isTagged(eventBlock.getType()) && !Tag.CARPETS.isTagged(eventBlock.getType()))
            return;

        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);

        if (!kitPlayer.isValid())
            return;

        // check if it is own plate
        if (!eventBlock.hasMetadata(demomanPlateKey)) return;
        String plateOwnerName = eventBlock.getMetadata(demomanPlateKey).get(0).asString();
        if (plateOwnerName.equals(event.getPlayer().getName())) return;

        // wood plate on sand
        if (Tag.WOODEN_PRESSURE_PLATES.isTagged(eventBlock.getType())) {
            if (eventBlock.getRelative(BlockFace.DOWN).getType() != Material.SAND) return;
            eventBlock.setType(Material.AIR);
            eventBlock.getWorld().createExplosion(eventBlock.getLocation(), sandExplosionSize);
        }
        // stone plate on gravel
        if (Tag.STONE_PRESSURE_PLATES.isTagged(eventBlock.getType())) {
            if (eventBlock.getRelative(BlockFace.DOWN).getType() != Material.GRAVEL) return;
            eventBlock.setType(Material.AIR);
            eventBlock.getWorld().createExplosion(eventBlock.getLocation(), gravelExplosionSize);
        }
        // same color carpet on concrete powder
        if (Tag.CARPETS.isTagged(eventBlock.getType())) {
            String carpetColor = eventBlock.getType().name().split("_")[0];
            Block concretePowder = eventBlock.getRelative(BlockFace.DOWN);
            if (!concretePowder.getType().name().endsWith("CONCRETE_POWDER"))
                return; // keine ahnung ob es besser geht, gibt kein concrete powder interface
            String concreteColor = concretePowder.getType().name().split("_CONCRETE_POWDER")[0];

            if (concreteColor.equals(carpetColor)) {
                eventBlock.setType(Material.AIR);  // blöcke removen damit der schaden richtig ist (warum auch immer)
                concretePowder.setType(Material.AIR);
                eventBlock.getWorld().createExplosion(eventBlock.getLocation(), concreteExplosionSize);
            }
        }
    }

}

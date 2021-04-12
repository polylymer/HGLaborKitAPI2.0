package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.MultipleKitItemsKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.items.KitItemAction;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.util.Logger;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PiratKit extends MultipleKitItemsKit implements Listener {
    public static final PiratKit INSTANCE = new PiratKit();

    @IntArg
    private final int explosionBarrelsLimit;
    @FloatArg(min = 0.1F, max = 100f)
    private final float explosionPower;

    private final Map<UUID, ArrayList<Block>> explosionBarrels = new HashMap<>();

    // KitItems

    private final ItemStack canon = new ItemBuilder(Material.FIRE_CHARGE).setName("Kanone").setDescription("Abschuss!!").build();

    // rechtsklick = all, linksklick = one by one
    private final ItemStack remoteDetonator = new ItemBuilder(Material.TRIPWIRE_HOOK).setName("Fernzünder").setDescription("Explosion!!").build();

    // auch mit buttons/druckplatten/hebeln auslösbar
    private final ItemStack explosionBarrel = new ItemBuilder(Material.BARREL).setName("Pulverfass").setDescription("Explosion??").build();

    private final NamespacedKey canonKey = markerKeyKitPirat("canon");
    private final NamespacedKey remoteDetonatorKey = markerKeyKitPirat("remote_detonator");
    private final NamespacedKey explosionBarrelKey = markerKeyKitPirat("explosion_barrel");

    protected PiratKit() {
        super("Pirat", Material.FIRE_CHARGE);
        explosionBarrelsLimit = 3;
        Map<KitItemAction, Float> kitActions = Map.of(
                new KitItemAction(canon, "pirate.canon"), 5F,
                new KitItemAction(remoteDetonator, "pirate.remoteDetonator"), 5F);
        setItemsAndCooldown(kitActions);
        explosionPower = 5f;
        maxAdditionalExplosionPower = 5f;
        additionalExplosionPowerStep = 0.5f;
    }

    // sollte der fernzünder droppen können? -> müsste dann das item als key nehmen


    @KitEvent
    public void onPlayerRightClicksOneOfMultipleKitItems(PlayerInteractEvent event, ItemStack item) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();

        ArrayList<Block> explosionBarrelsForPlayer = explosionBarrels.get(uuid);

        // Detonator
        if (item.isSimilar(remoteDetonator)) {
            // Alle Fässer explodieren lassen
            for (Block b : explosionBarrelsForPlayer) {
                if (isExplosionBarrel(b)) // Überprüfen, ob es ein Valides Pulverfass ist
                    world.createExplosion(b.getLocation(), explosionPower);
            }
            // Liste der Fässer säubern - alle Fässer sind schließlich explodiert
            explosionBarrels.get(uuid).clear();
        }

            /* Well its always rightclick look at method name
            else if (Utils.isLeftclick(event.getAction())) {
                // Fass an erster Position explodieren lassen
                if (isExplosionBarrel(explosionBarrelsForPlayer.get(0)))// Überprüfen, ob es ein Valides Pulverfass ist
                    world.createExplosion(explosionBarrelsForPlayer.get(0).getLocation(), explosionPower);
                // Das explodierte Fass entfernen
                explosionBarrelsForPlayer.remove(0);
            } */

    private void detonateExplosionBarrel(World world, UUID playerUUID) {
        Block explosionBarrel = world.getBlockAt(explosionBarrelsHolder.get(playerUUID).get(0));
        if (isExplosionBarrel(explosionBarrel)) {
            explosionBarrel.getWorld().createExplosion(explosionBarrel.getLocation(), Math.min(Math.min(explosionPower + getAdditionalExplosionPower(explosionBarrel), maxAdditionalExplosionPower), 100f));
            explosionBarrel.setType(Material.AIR);
            explosionBarrelsHolder.get(playerUUID).remove(0);
        }
    }

    private float getAdditionalExplosionPower(Block block) {
        AtomicInteger amount = new AtomicInteger();
        Arrays.stream(((Barrel) block).getInventory().getStorageContents()).filter(itemStack -> itemStack.getType() == Material.GUNPOWDER).collect(Collectors.toList()).forEach(itemStack -> amount.addAndGet(itemStack.getAmount()));
        return amount.get() * additionalExplosionPowerStep;
    }

    // Überprüfen, ob es ein Pulverfass ist und dieses ggf. aus der Liste entfernen
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (isExplosionBarrel(block)) {
            UUID explosionBarrelOwner = (UUID) block.getMetadata(KitMetaData.EXPLOSION_BARREL.getKey()).get(0).value();

            if (explosionBarrelOwner != null) {

                Player playerFromUUID = Bukkit.getPlayer(explosionBarrelOwner);

                if (playerFromUUID != null) {
                    playerFromUUID.sendMessage(Localization.INSTANCE.getMessage("pirat.destroyed",
                            ImmutableMap.of("location", String.valueOf(explosionBarrelsHolder.get(explosionBarrelOwner).get(0).toString())),
                            ChatUtils.getPlayerLocale(playerFromUUID)));
                }

                explosionBarrelsHolder.get(explosionBarrelOwner).remove(block.getLocation());
                //block.removeMetadata(KitMetaData.EXPLOSION_BARREL.getKey(), KitApi.getInstance().getPlugin());
            }
        }
    }

    // Die Pulverfässer explodieren, wenn man versucht sie mit einem Piston zu verschieben
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        detonateOnPistonMove(event.getBlocks());
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        detonateOnPistonMove(event.getBlocks());
    }

    private void detonateOnPistonMove(List<Block> blocks) {
        for (Block block : blocks) {
            UUID explosionBarrelOwner = (UUID) block.getMetadata(KitMetaData.EXPLOSION_BARREL.getKey()).get(0).value();
            if (explosionBarrelOwner != null) {
                Player playerFromUUID = Bukkit.getPlayer(explosionBarrelOwner);
                if (playerFromUUID != null) {
                    playerFromUUID.sendMessage(Localization.INSTANCE.getMessage("pirat.destroyedbypiston",
                            ImmutableMap.of("location", String.valueOf(explosionBarrelsHolder.get(explosionBarrelOwner).get(0).toString())),
                            ChatUtils.getPlayerLocale(playerFromUUID)));
                }
                detonateExplosionBarrel(block.getWorld(), explosionBarrelOwner);
            } else
                Logger.debug("[Kit-Pirat] Oh no, explosionBarrelOwner bei detonateOnPistonMove war null! Bitte gebe einem Coder bescheid! XD");
        }
    }

    private boolean isCanon(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().has(canonKey, PersistentDataType.BYTE);
    }

    private boolean isExplosionBarrel(Block block) {
        return block.hasMetadata(KitMetaData.EXPLOSION_BARREL.getKey());
    }
}

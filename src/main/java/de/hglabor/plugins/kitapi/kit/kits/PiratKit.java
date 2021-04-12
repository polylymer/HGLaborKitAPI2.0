package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.MultipleKitItemsKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
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

public class PiratKit extends MultipleKitItemsKit {
    public static final PiratKit INSTANCE = new PiratKit();

    @IntArg
    private final int explosionBarrelsLimit;
    @FloatArg(min = 0.1F, max = 100f)
    private final float explosionPower;

    // the max explosionPower (100f!) can't be outrun by this setting
    @FloatArg(min = 0.1F, max = 100f)
    private final float maxAdditionalExplosionPower, additionalExplosionPowerStep;

    private final HashMap<UUID, ArrayList<Location>> explosionBarrelsHolder = new HashMap<>();

    // KitItems

    private final ItemStack canon = new ItemBuilder(Material.FIRE_CHARGE).setName("Kanone").setDescription("Abschuss!!").build();
    // rechtsklick = all, linksklick = one by one
    private final ItemStack remoteDetonator = new ItemBuilder(Material.TRIPWIRE_HOOK).setName("Fernzünder").setDescription("Explosion!!").build();

    // auch mit druckplatten auslösbar machen
    private final ItemStack explosionBarrel = new ItemBuilder(Material.BARREL).setName("Pulverfass").setDescription("Explosion??").build();

    protected PiratKit() {
        super("Pirat", Material.FIRE_CHARGE);
        Map<KitItemAction, Float> kitActions = Map.of(
                new KitItemAction(canon, "pirate.canon"), 5F,
                new KitItemAction(remoteDetonator, "pirate.remoteDetonator"), 5F,
                new KitItemAction(explosionBarrel, "pirate.explosionBarrel"), 5F // nur testweise xd
        );
        setItemsAndCooldown(kitActions);
        explosionBarrelsLimit = 3;
        explosionPower = 5f;
        maxAdditionalExplosionPower = 5f;
        additionalExplosionPowerStep = 0.5f;
    }

    // sollte der fernzünder droppen können? -> müsste dann das item als key nehmen


    // Detonator leftclick
    @Override
    public void onPlayerLeftClicksOneOfMultipleKitItems(PlayerInteractEvent event, ItemStack item) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();

        player.sendMessage(Localization.INSTANCE.getMessage("pirate.detonatedone",
                ImmutableMap.of("location", String.valueOf(explosionBarrelsHolder.get(uuid).get(0).toString())),
                ChatUtils.getPlayerLocale(player)));
        // Fass an erster Position explodieren lassen
        detonateExplosionBarrel(world, uuid);
    }

    // Detonator rightclick, explosionBarrel placement &' canon handling
    @Override
    public void onPlayerRightClicksOneOfMultipleKitItems(PlayerInteractEvent event, ItemStack item) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();

        if (event.hasItem() && event.getItem() != null && item != null) {
            // Detonator
            if (item.isSimilar(remoteDetonator)) { // woran erkenne ich meinen detonator!? Ist das so richtig?
                event.setCancelled(true);
                player.sendMessage(Localization.INSTANCE.getMessage("pirate.detonatedall",
                        ImmutableMap.of("amount", String.valueOf(explosionBarrelsHolder.get(uuid).size())),
                        ChatUtils.getPlayerLocale(player)));
                // Alle Fässer explodieren lassen
                explosionBarrelsHolder.get(uuid).forEach(it -> detonateExplosionBarrel(world, uuid));
            } else if (item.isSimilar(explosionBarrel) && event.getClickedBlock() != null) {
                Block clickedBlock = event.getClickedBlock();
                if (explosionBarrelsHolder.get(uuid).size() < explosionBarrelsLimit) {
                    // Beim platzieren eines Pulverfasses das Pulverfass als solches markieren und in die Liste eintragen
                    clickedBlock.setMetadata(KitMetaData.EXPLOSION_BARREL.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), uuid));
                    explosionBarrelsHolder.putIfAbsent(uuid, new ArrayList<>());
                    explosionBarrelsHolder.get(uuid).add(clickedBlock.getLocation());
                } else {
                    player.sendMessage(Localization.INSTANCE.getMessage("pirate.explosionbarrelslimitreached", ChatUtils.getPlayerLocale(player)));
                    event.setCancelled(true);
                }
            }
        }
    }

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
                    playerFromUUID.sendMessage(Localization.INSTANCE.getMessage("pirate.explosionbarreldestroyed",
                            ImmutableMap.of("location", String.valueOf(explosionBarrelsHolder.get(explosionBarrelOwner).get(0).toString())),
                            ChatUtils.getPlayerLocale(playerFromUUID)));
                }
                explosionBarrelsHolder.get(explosionBarrelOwner).remove(block.getLocation());
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
                    playerFromUUID.sendMessage(Localization.INSTANCE.getMessage("pirate.explosionbarreldestroyedbypiston",
                            ImmutableMap.of("location", String.valueOf(explosionBarrelsHolder.get(explosionBarrelOwner).get(0).toString())),
                            ChatUtils.getPlayerLocale(playerFromUUID)));
                }
                detonateExplosionBarrel(block.getWorld(), explosionBarrelOwner);
            } else
                Logger.debug("[Kit-Pirat] Oh no, explosionBarrelOwner bei detonateOnPistonMove war null! Bitte gebe einem Coder bescheid! XD");
        }
    }

    private boolean isExplosionBarrel(Block block) {
        return block.hasMetadata(KitMetaData.EXPLOSION_BARREL.getKey());
    }
}

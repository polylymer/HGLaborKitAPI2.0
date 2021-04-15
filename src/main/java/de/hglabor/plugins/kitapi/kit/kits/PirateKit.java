package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.MultipleKitItemsKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.items.KitItemAction;
import de.hglabor.plugins.kitapi.kit.items.KitItemBuilder;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.hglabor.utils.localization.Localization.t;

public class PirateKit extends MultipleKitItemsKit implements Listener {
    public static final PirateKit INSTANCE = new PirateKit();

    @IntArg
    private final int explosionBarrelsLimit, fireballSpeed;
    @FloatArg(min = 0.1F, max = 100f)
    private final float explosionPower, additionalExplosionPowerStep, maxAdditionalExplosionPower;

    private final String explosionBarrelMetaKey;
    private final String explosionBarrelsKey;
    private final String UUID_KEY = "uuid";

    private final ItemStack canon = new KitItemBuilder(Material.FIRE_CHARGE).setName("Kanone").setDescription("Abschuss!!").build();
    private final ItemStack remoteDetonator = new KitItemBuilder(Material.TRIPWIRE_HOOK).setName("Fernzünder").setDescription("Explosion!!").build();

    protected PirateKit() {
        super("Pirate", Material.FIRE_CHARGE);
        Map<KitItemAction, Float> kitActions = Map.of(
                new KitItemAction(canon, "pirate.canon"), 5F,
                new KitItemAction(remoteDetonator, "pirate.remoteDetonator"), 5F
        );
        setItemsAndCooldown(kitActions);
        explosionBarrelsLimit = 3;
        explosionPower = 5F;
        fireballSpeed = 2;
        explosionBarrelMetaKey = this.getName() + "explosionBarrel";
        explosionBarrelsKey = this.getName() + "explosionBarrelsList";
        maxAdditionalExplosionPower = 5F;
        additionalExplosionPowerStep = 0.5F;
    }

    @KitEvent
    public void onPlayerLeftClicksOneOfMultipleKitItems(PlayerInteractEvent event, KitPlayer kitPlayer, ItemStack item) {
        //TODO nacheinander
    }

    @KitEvent
    public void onPlayerRightClicksOneOfMultipleKitItems(PlayerInteractEvent event, KitPlayer kitPlayer, ItemStack item) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (item.isSimilar(remoteDetonator)) {
            List<Block> barrels = kitPlayer.getKitAttributeOrDefault(explosionBarrelsKey, Collections.emptyList());
            for (Block barrel : barrels) {
                if (barrel.hasMetadata(explosionBarrelMetaKey)) {
                    barrel.removeMetadata(explosionBarrelMetaKey, KitApi.getInstance().getPlugin());
                    barrel.removeMetadata(UUID_KEY, KitApi.getInstance().getPlugin());
                    world.createExplosion(barrel.getLocation(), explosionPower);
                }
            }
            barrels.clear();
        } else if (item.isSimilar(canon)) {
            player.launchProjectile(Fireball.class, player.getEyeLocation().getDirection().multiply(fireballSpeed));
            this.activateCooldown(kitPlayer, item);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block barrel = event.getBlockPlaced();
        if (!barrel.getType().equals(Material.BARREL)) {
            return;
        }
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (kitPlayer.hasKit(this)) {
            List<Block> barrels = kitPlayer.getKitAttributeOrDefault(explosionBarrelsKey, new ArrayList<>());
            barrel.setMetadata(explosionBarrelMetaKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
            barrel.setMetadata(UUID_KEY, new FixedMetadataValue(KitApi.getInstance().getPlugin(), player.getUniqueId()));
            barrels.add(barrel);
            kitPlayer.putKitAttribute(explosionBarrelsKey, barrels);
            //TODO place message?
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata(explosionBarrelMetaKey) && block.hasMetadata(UUID_KEY)) {
            UUID owner = (UUID) block.getMetadata(UUID_KEY).get(0).value();
            if (owner == null) {
                return;
            }
            Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(owner));
            optionalPlayer.ifPresent(player -> {
                String key = "pirat.destroyed";
                Locale locale = ChatUtils.locale(owner);
                ImmutableMap<String, String> arguments = ImmutableMap.of("location", block.getLocation().toString());

                player.sendMessage(t(key, arguments, locale));

                KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
                List<Block> barrels = kitPlayer.getKitAttributeOrDefault(explosionBarrelsKey, Collections.emptyList());
                barrels.removeIf(barrel -> {
                    if (barrel.equals(block)) {
                        barrel.removeMetadata(explosionBarrelMetaKey, KitApi.getInstance().getPlugin());
                        barrel.removeMetadata(UUID_KEY, KitApi.getInstance().getPlugin());
                        return true;
                    }
                    return false;
                });
            });
        }
    }

   /* private void detonateExplosionBarrel(World world, UUID playerUUID) {
        Block explosionBarrel = world.getBlockAt(explosionBarrelsHolder.get(playerUUID).get(0));
        if (explosionBarrel.hasMetadata(explosionBarrelMetaKey)) {
            //whats the 100F?
            float min = Math.min(Math.min(explosionPower + getAdditionalExplosionPower(explosionBarrel), maxAdditionalExplosionPower), 100f);
            explosionBarrel.getWorld().createExplosion(explosionBarrel.getLocation(), min);
            explosionBarrel.setType(Material.AIR);
            explosionBarrelsHolder.get(playerUUID).remove(0);
        }
    } */

    private float getAdditionalExplosionPower(Block block) {
        AtomicInteger amount = new AtomicInteger();
        Arrays.stream(((Barrel) block).getInventory().getStorageContents()).filter(itemStack -> itemStack.getType() == Material.GUNPOWDER).collect(Collectors.toList()).forEach(itemStack -> amount.addAndGet(itemStack.getAmount()));
        return amount.get() * additionalExplosionPowerStep;
    }
}


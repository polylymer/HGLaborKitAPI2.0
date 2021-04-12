package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.MultipleKitItemsKit;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.items.KitItemAction;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PiratKit extends MultipleKitItemsKit {
    public static final PiratKit INSTANCE = new PiratKit();

    @IntArg
    private final int maxExplosionBarrels;
    @FloatArg(min = 0.1F, max = 100f)
    private final float explosionPower;

    private final HashMap<UUID, ArrayList<Block>> explosionBarrels = new HashMap<>();

    // KitItems

    private final ItemStack canon = new ItemBuilder(Material.FIRE_CHARGE).setName("Kanone").setDescription("Abschuss!!").build();

    // auch mit buttons/druckplatten/hebeln auslösbar
    private final ItemStack explosionBarrel = new ItemBuilder(Material.BARREL).setName("Pulverfass").setDescription("Explosion??").build();

    // rechtsklick = all, linksklick = one by one
    private final ItemStack remoteDetonator = new ItemBuilder(Material.TRIPWIRE_HOOK).setName("Fernzünder").setDescription("Explosion!!").build();

    protected PiratKit() {
        super("Pirat", Material.FIRE_CHARGE);
        Map<KitItemAction, Float> kitActions = Map.of(
                new KitItemAction(canon, "pirate.canon"), 5F,
                new KitItemAction(remoteDetonator, "pirate.remoteDetonator"), 5F);
        setItemsAndCooldown(kitActions);
        maxExplosionBarrels = 3;
        explosionPower = 5f;
    }

    // fässer mit gunpowder füllen = explosion größer?
    // sollte der fernzünder droppen können? -> müsste dann das item als key nehmen
    // fass mit piston verschieben = boom

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {

        // soll einmalig beim bekommen der items ausgeführt werden, muss noch machen
        canon.getItemMeta().getPersistentDataContainer().set(markerKeyKitPirat("fire_charge"), PersistentDataType.BYTE, (byte) 1);
        explosionBarrel.getItemMeta().getPersistentDataContainer().set(markerKeyKitPirat("explosion_barrel"), PersistentDataType.BYTE, (byte) 1);
        remoteDetonator.getItemMeta().getPersistentDataContainer().set(markerKeyKitPirat("remote_detonator"), PersistentDataType.BYTE, (byte) 1);

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();

        ArrayList<Block> explosionBarrelsForPlayer = explosionBarrels.get(uuid);

        if (!event.hasItem()) return;

        // Detonator
        if (event.getItem().isSimilar(remoteDetonator)) { // Wird nicht gleich sein, da es noch KitItem tags bekommt - muss mir was ausdenken, außerdem löst das hier sowieso nur aus, wenn ein KitItem angeklickt wird - gilt das auch für "additionalKitItems"?
            // Rechtsklick
            if (isRightclick(event.getAction())) {
                // Alle Fässer explodieren lassen
                for (Block b : explosionBarrelsForPlayer) {
                    if (isExplosionBarrel(b)) // Überprüfen, ob es ein Valides Pulverfass ist
                        world.createExplosion(b.getLocation(), explosionPower);
                }
                // Liste der Fässer säubern - alle Fässer sind schließlich explodiert
                explosionBarrels.get(uuid).clear();
            } else if (isLeftclick(event.getAction())) {
                // Fass an erster Position explodieren lassen
                if (isExplosionBarrel(explosionBarrelsForPlayer.get(0)))// Überprüfen, ob es ein Valides Pulverfass ist
                    world.createExplosion(explosionBarrelsForPlayer.get(0).getLocation(), explosionPower);
                // Das explodierte Fass entfernen
                explosionBarrelsForPlayer.remove(0);
            }
        }

        // TODO = Beim platzieren eines Pulverfasses das Pulverfass als solches markieren und in die Liste eintragen
        Block block = event.getClickedBlock();
        block.setMetadata(KitMetaData.EXPLOSION_BARREL.getKey(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));

    }


    // Überprüfen, ob es ein Pulverfass ist und dieses ggf. aus der Liste entfernen
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Block block = event.getBlock();
        if (isExplosionBarrel(block)) {
            Block explosionBarrel = (Block) explosionBarrels.get(uuid).stream().filter(b -> b.getLocation() == block.getLocation());
            explosionBarrels.get(uuid).remove(explosionBarrel);
        }
    }

    private boolean isExplosionBarrel(Block block) {
        return block.hasMetadata(KitMetaData.EXPLOSION_BARREL.getKey());
    }

    private NamespacedKey markerKeyKitPirat(String key) {
        return new NamespacedKey(KitApi.getInstance().getPlugin(), "hglaborkitapi2.0_marker_" + "kit_pirat_" + key);
    }

    // Add to Utils?
    private boolean isRightclick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    private boolean isLeftclick(Action action) {
        return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
    }
}

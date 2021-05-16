package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.events.KitEventHandler;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.metadata.FixedMetadataValue;

public class KayaKit extends AbstractKit implements Listener {
    public final static KayaKit INSTANCE = new KayaKit();
    private final NamespacedKey namespacedKey;
    private final String kayaBlockKey;
    private final ItemStack kayaBlock;

    private KayaKit() {
        super("Kaya", Material.GRASS_BLOCK);
        this.kayaBlockKey = "kayaBlock";
        this.namespacedKey = new NamespacedKey(KitApi.getInstance().getPlugin(), kayaBlockKey);
        this.kayaBlock = new ItemBuilder(Material.GRASS_BLOCK).setName(ChatColor.GREEN + "Kaya Block").build();
        this.addAdditionalKitItems(new ItemBuilder(kayaBlock.clone()).setAmount(16).build());
        this.setKitItemPlaceable(true);
        this.registerRecipe();
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> player.discoverRecipe(namespacedKey));
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> player.undiscoverRecipe(namespacedKey));
    }

    @KitEvent
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (!event.getItemInHand().isSimilar(kayaBlock))
            return;
        if (!KitEventHandler.canUseKit(event, kitPlayer, this)) {
            event.setCancelled(true);
            return;
        }
        event.getBlockPlaced().setMetadata(kayaBlockKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnGround())
            return;
        if (event.getTo().distanceSquared(event.getFrom()) == 0)
            return;
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (kitPlayer.hasKit(this))
            return;
        if (!kitPlayer.isValid())
            return;
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (block.getType().equals(kayaBlock.getType())) {
            if (block.hasMetadata(kayaBlockKey)) {
                Localization.INSTANCE.getMessage("kaya.triggerBlock", ChatUtils.locale(player));
                block.setType(Material.AIR);
            }
        }
    }

    private void registerRecipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, kayaBlock);
        recipe.addIngredient(Material.WHEAT_SEEDS);
        recipe.addIngredient(Material.DIRT);
        Bukkit.addRecipe(recipe);
    }

    public ItemStack getKayaBlock() {
        return kayaBlock;
    }
}

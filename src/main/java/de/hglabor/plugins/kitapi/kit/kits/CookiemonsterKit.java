package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.events.KitEventHandler;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CookiemonsterKit extends AbstractKit implements Listener {

    public static final CookiemonsterKit INSTANCE = new CookiemonsterKit();

    protected CookiemonsterKit() {
        super("Cookiemonster", Material.COOKIE);
        this.effectMultiplier = 0;
        this.effectDuration = 3;
        this.itemAmount = 3;
        this.likeihood = 10;
        setMainKitItem(new ItemBuilder(getDisplayMaterial()).setAmount(this.itemAmount).setName(ChatColor.GOLD + "Cookie").build());
    }

    @IntArg
    private final int effectMultiplier, effectDuration, itemAmount, likeihood;

    @KitEvent
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if(!player.getInventory().getItemInMainHand().getType().equals(Material.COOKIE)) {
            return;
        }
        if (!KitEventHandler.canUseKit(event, kitPlayer, this)) {
            return;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        itemStack.setAmount(itemStack.getAmount()-1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DONKEY_EAT, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, effectDuration*20, effectMultiplier, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effectDuration*20, effectMultiplier, false, false));
    }

    @KitEvent
    @EventHandler
    public void onBlockBreakA(BlockBreakEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (event.getBlock().getType() != Material.GRASS) {
            return;
        }
        if (!KitEventHandler.canUseKit(event, kitPlayer, this)) {
            return;
        }
        if(ChanceUtils.roll(likeihood)) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemBuilder(getDisplayMaterial()).setName(ChatColor.GOLD + "Cookie").build());
        }
    }
}

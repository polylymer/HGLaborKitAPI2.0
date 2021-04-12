package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;


public class SpitKit extends AbstractKit implements Listener {
    public static final SpitKit INSTANCE = new SpitKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    private final String spitProjectileKey;
    @DoubleArg
    private final double spitDamage;
    @IntArg
    private final int spitSoupHealing;

    private SpitKit() {
        super("Spit", Material.GHAST_TEAR);
        cooldown = 10;
        spitSoupHealing = 3;
        spitDamage = 4D;
        spitProjectileKey = this.getName() + " Soup";
        setMainKitItem(getDisplayMaterial());
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        Entity entity = player.launchProjectile(LlamaSpit.class);
        entity.setMetadata(spitProjectileKey, new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, 100, 100);
        kitPlayer.activateKitCooldown(this);
    }

    @EventHandler
    public void onSpitHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof LlamaSpit)) {
            return;
        }
        if (!(event.getDamager().hasMetadata(spitProjectileKey))) {
            return;
        }
        Player player = (Player) event.getEntity();
        event.setDamage(spitDamage);
        if (player.getInventory().contains(Material.MUSHROOM_STEW)) {
            spitInSoup(player);
        }
    }

    private void spitInSoup(Player player) {
        int random = new Random().nextInt(35) + 1;
        ItemStack randomItem = player.getInventory().getItem(random);
        if (randomItem != null) {
            if (randomItem.getType().equals(Material.MUSHROOM_STEW)) {
                ItemStack itemStack = new ItemBuilder(Material.SUSPICIOUS_STEW).setName(spitProjectileKey).build();
                player.getInventory().setItem(random, itemStack);
            } else {
                spitInSoup(player);
            }
        }
    }

    public String getSpitProjectileKey() {
        return spitProjectileKey;
    }

    public int getSpitSoupHealing() {
        return spitSoupHealing;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}



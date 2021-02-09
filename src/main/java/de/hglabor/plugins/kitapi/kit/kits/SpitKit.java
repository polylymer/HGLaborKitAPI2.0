package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
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

import java.util.Collections;
import java.util.Random;


public class SpitKit extends AbstractKit implements Listener {
    public static final SpitKit INSTANCE = new SpitKit();

    private SpitKit() {
        super("Spit",Material.GHAST_TEAR,10);
        setMainKitItem(getDisplayMaterial());
        addEvents(Collections.singletonList(PlayerInteractEvent.class));
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        Entity entity = player.launchProjectile(LlamaSpit.class);
        entity.setMetadata(KitMetaData.SPIT_PROJECTILE.getKey(), new FixedMetadataValue(KitManager.getInstance().getPlugin(), ""));
        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, 100, 100);
        kitPlayer.activateKitCooldown(this, this.getCooldown());
    }

    @EventHandler
    public void onSpitHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof LlamaSpit)) {
            return;
        }
        if (!(event.getDamager().hasMetadata(KitMetaData.SPIT_SOUP.getKey()))) {
            return;
        }
        Player player = (Player) event.getEntity();
        event.setDamage(4);
        if (player.getInventory().contains(Material.MUSHROOM_STEW)) {
            spitInSoup(player);
        }
    }

    private void spitInSoup(Player player) {
        int random = new Random().nextInt(35) + 1;
        ItemStack randomItem = player.getInventory().getItem(random);
        if (randomItem != null) {
            if (randomItem.getType().equals(Material.MUSHROOM_STEW)) {
                ItemStack itemStack = new ItemBuilder(Material.SUSPICIOUS_STEW).setName(KitMetaData.SPIT_SOUP.getKey()).build();
                player.getInventory().setItem(random, itemStack);
            } else {
                spitInSoup(player);
            }
        }
    }
}



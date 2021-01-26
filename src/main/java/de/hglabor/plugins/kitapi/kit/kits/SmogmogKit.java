package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Collections;
import java.util.Random;

public class SmogmogKit extends AbstractKit implements Listener {
    public final static SmogmogKit INSTANCE = new SmogmogKit();

    private SmogmogKit() {
        super("Smogmog", Material.POPPED_CHORUS_FRUIT, 20);
        addSetting(KitSettings.EFFECT_DURATION, 3);
        addSetting(KitSettings.RADIUS, 8);
        setMainKitItem(getDisplayMaterial());
        addEvents(Collections.singletonList(PlayerInteractEvent.class));
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent e) {
        AreaEffectCloud cloud = (AreaEffectCloud) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.AREA_EFFECT_CLOUD);
        cloud.setCustomName(e.getPlayer().getUniqueId().toString());
        cloud.setColor(Color.fromBGR(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
        cloud.setDuration((Integer) getSetting(KitSettings.EFFECT_DURATION) * 20);
        cloud.setSource(e.getPlayer());
        cloud.setRadius(((Integer) getSetting(KitSettings.RADIUS)).floatValue());
        cloud.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
        cloud.setRadius(((Integer) getSetting(KitSettings.RADIUS)).floatValue());
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(e.getPlayer());
        kitPlayer.activateKitCooldown(this, this.getCooldown());
        e.getPlayer().getLocation().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 0);
    }

    @EventHandler
    public void onAreaEffectCloudDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof AreaEffectCloud) {
            Player involved = (Player) e.getEntity();
            AreaEffectCloud cloud = (AreaEffectCloud) e.getDamager();
            if (involved.getUniqueId().toString().equals(cloud.getCustomName())) {
                e.setCancelled(true);
            }
        }
    }
}

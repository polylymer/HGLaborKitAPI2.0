package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class SmogmogKit extends AbstractKit {
    public final static SmogmogKit INSTANCE = new SmogmogKit();

    private SmogmogKit() {
        super("Smogmog", Material.SHULKER_SPAWN_EGG, 20);
        setMainKitItem(getDisplayMaterial());
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent e) {
        e.setCancelled(true);
        AreaEffectCloud cloud = (AreaEffectCloud) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.AREA_EFFECT_CLOUD);
        cloud.setCustomName(e.getPlayer().getUniqueId().toString());
        cloud.setColor(Color.fromBGR(201, 110, 235));
        cloud.setDuration(60);
        cloud.setSource(e.getPlayer());
        cloud.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
    }

    @Override
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof AreaEffectCloud) {
            Player involved = (Player) e.getEntity();
            AreaEffectCloud cloud = (AreaEffectCloud) e.getDamager();
            if (involved.getUniqueId().toString().equals(cloud.getCustomName())) {
                e.setCancelled(true);
            }
        }
    }
}

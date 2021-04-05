package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.kit.settings.PotionTypeArg;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Random;

public class SmogmogKit extends AbstractKit implements Listener {
    public final static SmogmogKit INSTANCE = new SmogmogKit();
    @FloatArg(min = 0.0F)
    private final float cooldown, radius;
    @IntArg
    private final int effectDuration;
    @PotionTypeArg
    private final PotionType potionType;

    private SmogmogKit() {
        super("Smogmog", Material.POPPED_CHORUS_FRUIT);
        cooldown = 20;
        radius = 8F;
        effectDuration = 3;
        potionType = PotionType.INSTANT_DAMAGE;
        setMainKitItem(getDisplayMaterial());
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        AreaEffectCloud cloud = (AreaEffectCloud) player.getWorld().spawnEntity(player.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        cloud.setCustomName(player.getUniqueId().toString());
        cloud.setColor(Color.fromBGR(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
        cloud.setDuration(effectDuration * 20);
        cloud.setSource(player);
        cloud.setRadius(radius);
        cloud.setBasePotionData(new PotionData(potionType, false, false));
        cloud.setRadius(radius);
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        kitPlayer.activateKitCooldown(this);
        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 0);
    }

    @EventHandler
    public void onAreaEffectCloudDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof AreaEffectCloud) {
            Player involved = (Player) e.getEntity();
            AreaEffectCloud cloud = (AreaEffectCloud) e.getDamager();
            if (involved.getUniqueId().toString().equals(cloud.getCustomName())) {
                e.setCancelled(true);
            }
            else {
                involved.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0));
            }
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

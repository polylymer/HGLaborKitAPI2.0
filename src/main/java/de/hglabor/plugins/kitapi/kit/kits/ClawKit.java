package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEvokerFangs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class ClawKit extends AbstractKit implements Listener {
    public final static ClawKit INSTANCE = new ClawKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private ClawKit() {
        super("Claw", Material.SHEARS);
        cooldown = 12F;
        setMainKitItem(getDisplayMaterial(), true);
        addSetting(KitSettings.RADIUS, 15);
        addSetting(KitSettings.EFFECT_DURATION, 5);
        addSetting(KitSettings.EFFECT_MULTIPLIER, 10);
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer hgPlayer = KitApi.getInstance().getPlayer(player);

        Location start = player.getLocation();
        Location start2 = player.getLocation().add(0, 2, 0);
        Vector direction = start.getDirection();

        for (int i = 1; i <= (Integer) getSetting(KitSettings.RADIUS); i++) {
            player.getWorld().spawnEntity(start.clone().add(direction.clone().multiply(i)), EntityType.EVOKER_FANGS).setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
            player.getWorld().spawnEntity(start2.clone().add(direction.clone().multiply(i)), EntityType.EVOKER_FANGS).setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(KitApi.getInstance().getPlugin(), ""));
            ;
        }

        hgPlayer.activateKitCooldown(this);
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof CraftEvokerFangs) {
            Player player = (Player) event.getEntity();
            CraftEvokerFangs evokerFangs = (CraftEvokerFangs) event.getDamager();
            if (evokerFangs.hasMetadata(player.getUniqueId().toString())) {
                event.setCancelled(true);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * (Integer) getSetting(KitSettings.EFFECT_DURATION), (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER)));
            }
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

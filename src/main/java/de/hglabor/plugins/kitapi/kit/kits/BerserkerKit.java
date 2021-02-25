package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */

public class BerserkerKit extends AbstractKit {
    public static final BerserkerKit INSTANCE = new BerserkerKit();


    protected BerserkerKit() {
        super("Berserker",Material.BLAZE_POWDER);
        addEvents(Collections.singletonList(EntityDeathEvent.class));
    }

    @Override
    public void onPlayerKillsLivingEntity(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        if(!KitApi.getInstance().getPlayer(killer).hasKit(this)) return;
        if(event.getEntity() instanceof Player){
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,20*12,1,true,false));
            killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*8,2,true,false));
        }else {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,20*4,1,true,false));
            killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*4,1,true,false));
        }
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        Bukkit.getPlayer(kitPlayer.getUUID()).getActivePotionEffects().clear();
    }
}

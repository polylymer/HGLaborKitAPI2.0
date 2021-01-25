package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoseidonKit extends AbstractKit {
    public static final PoseidonKit INSTANCE = new PoseidonKit();

    private PoseidonKit() {
        super("Poseidon", Material.WATER_BUCKET);
        addSetting(KitSettings.EFFECT_MULTIPLIER, 2);
        addSetting(KitSettings.EFFECT_DURATION, 3);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(event.getPlayer().getLocation().subtract(0, 1,0).getBlock().getType() == Material.WATER) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE,(Integer) getSetting(KitSettings.EFFECT_DURATION)*20, (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER), false,false ));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,(Integer) getSetting(KitSettings.EFFECT_DURATION)*20, (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER), false,false ));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,(Integer) getSetting(KitSettings.EFFECT_DURATION)*20, (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER), false,false ));
        }
        if(!player.getWorld().isClearWeather()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,(Integer) getSetting(KitSettings.EFFECT_DURATION)*20, (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER), false,false ));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,(Integer) getSetting(KitSettings.EFFECT_DURATION)*20, (Integer) getSetting(KitSettings.EFFECT_MULTIPLIER), false,false ));
        }
    }
}

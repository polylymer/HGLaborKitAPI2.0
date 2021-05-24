package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SnakeKit extends AbstractKit {

    public static final SnakeKit INSTANCE = new SnakeKit();

    @IntArg
    private final int effectDuration, effectMultiplier;

    protected SnakeKit() {
        super("Snake", Material.WEEPING_VINES);
        this.effectDuration = 2;
        this.effectMultiplier = 0;
        setMainKitItem(getDisplayMaterial());
    }

    @KitEvent
    @Override
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, effectDuration * 20, effectMultiplier, false, false));
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PHANTOM_BITE, 5, 10f);
        attacker.activateKitCooldown(this);
    }

    @KitEvent(ignoreCooldown = true)
    @Override
    public void onPlayerMoveEvent(PlayerMoveEvent event, KitPlayer kitPlayer) {
        Player player = event.getPlayer();
        Block blockBelow = player.getLocation().clone().subtract(0,1,0).getBlock();
        if(blockBelow.getType().name().contains("SAND")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, effectDuration * 10, effectMultiplier, false, false));
        }
    }
}

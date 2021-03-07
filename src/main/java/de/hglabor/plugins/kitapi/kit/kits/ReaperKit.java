package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ReaperKit extends AbstractKit {
    public static final ReaperKit INSTANCE = new ReaperKit();
    @IntArg
    private final int maxUses;
    @IntArg
    private final int likelihood, blueWitherLikelihood;
    @IntArg
    private final int witherDuration, witherAmplifier;
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private ReaperKit() {
        super("Reaper", Material.WITHER_SKELETON_SKULL);
        cooldown = 15F;
        maxUses = 2;
        likelihood = 100;
        blueWitherLikelihood = 5;
        witherAmplifier = 1;
        witherDuration = 3;
        setMainKitItem(new ItemBuilder(Material.STONE_HOE).setEnchantment(Enchantment.VANISHING_CURSE, 1).setUnbreakable(true).build());
    }

    @KitEvent
    @Override
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(likelihood)) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherDuration * 20, witherAmplifier));
        }
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        WitherSkull witherSkull = player.getWorld().spawn(player.getLocation().clone().add(0, 1, 0), WitherSkull.class);
        if (ChanceUtils.roll(blueWitherLikelihood)) {
            witherSkull.setCharged(true);
        }
        witherSkull.setVelocity(player.getLocation().getDirection().multiply(1.5));
        KitApi.getInstance().checkUsesForCooldown(player, this, maxUses);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

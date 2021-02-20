package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class ReaperKit extends AbstractKit {
    public static final ReaperKit INSTANCE = new ReaperKit();

    private ReaperKit() {
        super("Reaper", Material.WITHER_SKELETON_SKULL, 15);
        addSetting(KitSettings.USES, 2);
        setMainKitItem(new ItemBuilder(Material.STONE_HOE).setEnchantment(Enchantment.DAMAGE_ALL, 3).setUnbreakable(true).build());
        addSetting(KitSettings.LIKELIHOOD, 100);
        addSetting(KitSettings.EFFECT_DURATION, 3);
        addSetting(KitSettings.EFFECT_MULTIPLIER, 1);
        addEvents(Arrays.asList(PlayerInteractEvent.class, EntityDamageByEntityEvent.class));
    }

    @Override
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(getSetting(KitSettings.LIKELIHOOD))) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (Integer) getSetting(KitSettings.EFFECT_DURATION) * 20, getSetting(KitSettings.EFFECT_MULTIPLIER)));
        }
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        WitherSkull witherSkull = event.getPlayer().getWorld().spawn(event.getPlayer().getLocation().clone().add(0, 1, 0), WitherSkull.class);
        witherSkull.setVelocity(event.getPlayer().getLocation().getDirection().multiply(1.5));
        KitApi.getInstance().checkUsesForCooldown(event.getPlayer(), this);
    }
}

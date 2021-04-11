package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ArcherKit extends AbstractKit implements Listener {

    public final static ArcherKit INSTANCE = new ArcherKit();

    @FloatArg(min = 0.0F)
    private final float cooldown;
    @IntArg(min = 0)
    private final float effectDuration;

    ArrayList<PotionEffectType> potionEffectTypes = new ArrayList<>();

    private ArcherKit() {
        super("Archer", Material.CROSSBOW);
        ItemStack crossbow = new ItemBuilder(Material.CROSSBOW).setUnbreakable(true).build();
        chargeCrossbow(crossbow);
        setMainKitItem(crossbow); // nicht getestet, hoffe das geht
        cooldown = 12;
        effectDuration = 4;
        initArrowEffects();
    }

    @Override
    public void onProjectileHitEvent(ProjectileHitEvent event, KitPlayer kitPlayer, Entity hitEntity) {

    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player.getInventory().getItemInMainHand().getType() != Material.CROSSBOW) return;

        if (ThreadLocalRandom.current().nextBoolean()) {
            if (event.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getProjectile();
                arrow.addCustomEffect(new PotionEffect(
                        potionEffectTypes.get(ThreadLocalRandom.current().nextInt(potionEffectTypes.size())),
                        (int) (effectDuration * 20),
                        0
                ), false);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
        }
    }

    private void chargeCrossbow(ItemStack crossbow) {
        CrossbowMeta crossbowMeta = (CrossbowMeta) crossbow.getItemMeta();
        if (crossbowMeta == null) return;
        if (crossbowMeta.hasChargedProjectiles()) return;
        List<ItemStack> projectiles = new ArrayList<>();
        projectiles.add(new ItemStack(Material.ARROW));
        crossbowMeta.setChargedProjectiles(projectiles);
        crossbow.setItemMeta(crossbowMeta);
    }

    private boolean crossBowIsCharged(ItemStack crossbow) {
        if (crossbow.getType() != Material.CROSSBOW) return false;
        CrossbowMeta crossbowMeta = (CrossbowMeta) crossbow.getItemMeta();
        if (crossbowMeta == null) return false;
        return crossbowMeta.hasChargedProjectiles();
    }

    public void initArrowEffects() {
        potionEffectTypes.add(PotionEffectType.BLINDNESS);
        potionEffectTypes.add(PotionEffectType.SLOW_FALLING);
        potionEffectTypes.add(PotionEffectType.LEVITATION);
        potionEffectTypes.add(PotionEffectType.REGENERATION);
        potionEffectTypes.add(PotionEffectType.SLOW_DIGGING);
        potionEffectTypes.add(PotionEffectType.HUNGER);
        potionEffectTypes.add(PotionEffectType.CONFUSION);
        potionEffectTypes.add(PotionEffectType.GLOWING);
        potionEffectTypes.add(PotionEffectType.WITHER);
        potionEffectTypes.add(PotionEffectType.POISON);
        potionEffectTypes.add(PotionEffectType.INCREASE_DAMAGE);
    }
}

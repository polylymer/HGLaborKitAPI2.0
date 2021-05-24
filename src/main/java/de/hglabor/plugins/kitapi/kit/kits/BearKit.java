package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.events.KitEventHandler;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class BearKit extends AbstractKit implements Listener {

    public static final BearKit INSTANCE = new BearKit();

    private final String snowballKey;
    @DoubleArg
    private final double damage;
    @FloatArg
    private final float volume;
    @FloatArg
    private final float explosionPower;
    @FloatArg(min = 0.0F)
    private final float cooldown;

    protected BearKit() {
        super("Bear", Material.DEAD_TUBE_CORAL);
        setMainKitItem(getDisplayMaterial());
        snowballKey = "bearSnowball";
        damage = 2.0;
        volume = 4.3F;
        this.explosionPower = 1.2f;
        this.cooldown = 50.0f;
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(player -> DisguiseAPI.disguiseEntity(player, new MobDisguise(DisguiseType.POLAR_BEAR)));
    }

    @KitEvent(ignoreCooldown = true)
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }
        if(!player.getInventory().getItemInMainHand().getType().equals(Material.BAMBOO)) {
            return;
        }
        if (!KitEventHandler.canUseKit(event, kitPlayer, this)) {
            return;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        itemStack.setAmount(itemStack.getAmount()-1);
        Snowball snowball = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(2));
        snowball.addScoreboardTag(snowballKey);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }

    @KitEvent
    @Override
    public void onProjectileHitEvent(ProjectileHitEvent event, KitPlayer kitPlayer, Entity hitEntity) {
        if(kitPlayer.isValid()) {
            if (event.getEntity().getScoreboardTags().contains(snowballKey)) {
                if (hitEntity != null) {
                    hitEntity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, hitEntity.getLocation(), 1);
                    if (hitEntity instanceof LivingEntity) {
                        kitPlayer.getBukkitPlayer().ifPresent(it -> ((LivingEntity) hitEntity).damage(damage, it));
                    }
                    hitEntity.getWorld().playSound(hitEntity.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
                }
                Block block = event.getHitBlock();
                if(block != null) {
                    block.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, block.getLocation(), 1);
                    block.getWorld().playSound(block.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
                }
            }
        }
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        kitPlayer.getBukkitPlayer().ifPresent(DisguiseAPI::undisguiseToAll);
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.getWorld().createExplosion(player.getLocation(), explosionPower, false, true, player);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_POLAR_BEAR_WARNING, volume, 0.9f);
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        kitPlayer.activateKitCooldown(this);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

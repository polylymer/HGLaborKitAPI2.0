package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.BoolArg;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class MagmaKit extends AbstractKit {
    public final static MagmaKit INSTANCE = new MagmaKit();
    @IntArg
    private final int likelihood, effectDuration, extraDamage;

    @DoubleArg(min = 1.0, max = 100.0)
    private final double radius;

    @IntArg(min = 1, max = 16)
    private final int height;

    @DoubleArg(min = 0.1, max = 1.0)
    private final double velocity;

    @BoolArg
    private final boolean enableVelocity;

    @FloatArg
    private final float cooldown;

    private MagmaKit() {
        super("Magma", Material.MAGMA_BLOCK);
        setMainKitItem(getDisplayMaterial());
        likelihood = 33;
        effectDuration = 2;
        radius = 5;
        height = 8;
        velocity = 0.5;
        extraDamage = 2;
        cooldown = 45;
        enableVelocity = true;
    }

    @KitEvent(ignoreCooldown = true)
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(likelihood)) {
            entity.setFireTicks(effectDuration * 20);
        }
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        playFireCircle(player);
        for (LivingEntity nearby : player.getWorld().getNearbyLivingEntities(player.getLocation(), radius)) {
            if (nearby instanceof Player) {
                KitPlayer kitPlayer = KitApi.getInstance().getPlayer((Player) nearby);
                if (!kitPlayer.isValid()) {
                    continue;
                }
            }
            setEntityOnFire(player, nearby);
        }
        KitApi.getInstance().getPlayer(player).activateKitCooldown(this);
    }

    private void playFireCircle(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();
        // "Plus" Symbol on the Floor
        for (int i = 0; i <= radius; i++) {
            world.spawnParticle(Particle.FLAME, loc.clone().add(i, 0, 0), 0, 0, 0, 0, 8);
            world.spawnParticle(Particle.FLAME, loc.clone().add(i * -1, 0, 0), 0, 0, 0, 0, 8);
            world.spawnParticle(Particle.FLAME, loc.clone().add(0, 0, i), 0, 0, 0, 0, 8);
            world.spawnParticle(Particle.FLAME, loc.clone().add(0, 0, i * -1), 0, 0, 0, 0, 8);
        }
        // Circles to build the "cage"
        for (double i = 0; i < height; i++) {
            for (double y = 0.0; y < (Math.PI * 2); y += .1) {
                double x = radius * Math.cos(y);
                double z = radius * Math.sin(y);
                loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(x, i / 4, z), 0, 0, 0, 0, 5);
            }
        }
    }

    private void setEntityOnFire(Entity attacker, LivingEntity target) {
        target.setFireTicks(effectDuration * 20);
        if (enableVelocity) {
            target.setVelocity(new Vector(0, velocity, 0));
        }
        // Extra Damage with another 33% chance of happening
        if (ChanceUtils.roll(likelihood)) {
            target.damage(extraDamage, attacker);
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

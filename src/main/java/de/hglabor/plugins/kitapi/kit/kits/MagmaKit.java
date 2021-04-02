package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.BoolArg;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChanceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class MagmaKit extends AbstractKit {
    public final static MagmaKit INSTANCE = new MagmaKit();
    @IntArg
    private final int likelihood, effectDuration, extraDamage;

    @DoubleArg(min = 1.0, max = 3.0)
    private final double radius;

    @IntArg(min = 1, max = 16)
    private final int height;

    @DoubleArg(min = 0.1, max = 1.0)
    private final double velocity;

    @BoolArg
    private final boolean enableVelocity;

    private MagmaKit() {
        super("Magma", Material.MAGMA_BLOCK);
        likelihood = 33;
        effectDuration = 2;
        radius = 1.5;
        height = 8;
        velocity = 0.5;
        extraDamage = 2;
        enableVelocity = true;
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        if (ChanceUtils.roll(likelihood)) {
            damagePlayer(event.getDamager(), entity);
        }
    }

    private void damagePlayer(Entity attacker, LivingEntity target) {
        Location loc = target.getLocation();
        target.setFireTicks(effectDuration * 20);
        if (enableVelocity) target.setVelocity(new Vector(0, velocity, 0));
        if (target instanceof Player) {
            // "Plus" Symbol on the Floor
            for (int i = 0; i <= radius; i++) {
                target.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(i, 0, 0), 0, 0, 0, 0, 8);
                target.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(i * -1, 0, 0), 0, 0, 0, 0, 8);
                target.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(0, 0, i), 0, 0, 0, 0, 8);
                target.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(0, 0, i * -1), 0, 0, 0, 0, 8);
            }
            // Circles to build the "cage"
            for (double i = 0; i < height; i++) {
                for (double y = 0.0; y < (Math.PI * 2); y += .1) {
                    double x = radius * Math.cos(y);
                    double z = radius * Math.sin(y);
                    loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(x, i / 4, z), 0, 0, 0, 0, 5);
                }
            }
            // Extra Damage with another 33% chance of happening
            if (ChanceUtils.roll(likelihood)) {
                target.damage(extraDamage, attacker);
            }
        }
    }
}

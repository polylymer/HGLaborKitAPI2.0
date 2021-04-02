package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class FireSerpentKit extends AbstractKit {

    public static final FireSerpentKit INSTANCE = new FireSerpentKit();

    @DoubleArg(min = 1.0, max = 3.0)
    private final double radius;

    @IntArg(min = 20, max = 200)
    private final int fireTicks;

    @DoubleArg(min = 0.5, max = 20.0)
    private final double damage;

    @DoubleArg(min = 0.1)
    private final double velocity;

    private final Particle particle = Particle.FLAME;

    public FireSerpentKit() {
        super("Fire Serpent", Material.FLINT_AND_STEEL);
        radius = 1.5;
        fireTicks = 80;
        damage = 4.0;
        velocity = 1.0;
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent e) {
        Location playerLocation = e.getPlayer().getLocation();

        for (double i = 0; i < radius; i += .1) {
            e.getPlayer().getWorld().spawnParticle(Particle.FLAME, e.getPlayer().getLocation().clone().add(i, 0, 0), 0, 0, 0, 0, 2);
            e.getPlayer().getWorld().spawnParticle(Particle.FLAME, e.getPlayer().getLocation().clone().add(i * -1, 0, 0), 0, 0, 0, 0, 2);

            e.getPlayer().getWorld().spawnParticle(Particle.FLAME, e.getPlayer().getLocation().clone().add(0, 0, i), 0, 0, 0, 0, 2);
            e.getPlayer().getWorld().spawnParticle(Particle.FLAME, e.getPlayer().getLocation().clone().add(0, 0, i * -1), 0, 0, 0, 0, 2);
        }

        for (double i = 0.0; i < 8.0; i++) {
            for (double y = 0.0; y < (Math.PI * 2); y += .1) {
                double x = radius * Math.cos(y);
                double z = radius * Math.sin(y);
                e.getPlayer().getLocation().getWorld().spawnParticle(particle, playerLocation.clone().add(x, i / 4, z), 0, 0, 0, 0, 5);
            }
        }

        for (Entity entity : e.getPlayer().getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Damageable) {
                Damageable damageable = (Damageable) entity;
                if (damageable.getUniqueId() != e.getPlayer().getUniqueId()) {
                    damageable.damage(damage);
                    damageable.setFireTicks(new Random().nextInt(fireTicks) + 20);
                    damageable.setVelocity(new Vector(0, velocity, 0));
                }
            }
        }
    }
}

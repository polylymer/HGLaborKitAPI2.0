package de.hglabor.plugins.kitapi.util.pathfinder;


import net.minecraft.server.v1_16_R3.EntityCreeper;
import net.minecraft.server.v1_16_R3.PathfinderGoal;

// Custom Made
public class LaborPathfinderGoalSwell extends PathfinderGoal {

    private final EntityCreeper handler;

    public int blastPower = 4;
    public double maxDistance = 6;
    public int swellTimer;

    public LaborPathfinderGoalSwell(EntityCreeper handler) {
        this.handler = handler;
    }

    public boolean a() {
        return handler.getGoalTarget() != null;
    }

    public void e() {
        if (handler.getBukkitEntity().getLocation().distance(handler.getGoalTarget().getBukkitEntity().getLocation()) > maxDistance) {
            handler.a(-1);
        } else if (!handler.getEntitySenses().a(handler.getGoalTarget())) {
            handler.a(-1);
        } else if (handler.getBukkitEntity().getLocation().distance(handler.getGoalTarget().getBukkitEntity().getLocation()) < maxDistance) {
            handler.a(1);

        } else if (handler.getBukkitEntity().getLocation().distance(handler.getGoalTarget().getBukkitEntity().getLocation()) > maxDistance) {

            if (this.swellTimer > 0) {
                this.swellTimer--;
            }
        } else if (!handler.getEntitySenses().a(handler.getGoalTarget())) {

            if (this.swellTimer > 0) {
                this.swellTimer--;
            }
        } else if (handler.getBukkitEntity().getLocation().distance(handler.getGoalTarget().getBukkitEntity().getLocation()) < maxDistance) {

            if (this.swellTimer < 50) {
                this.swellTimer++;
            } else {
                handler.getBukkitEntity().getLocation().getWorld().createExplosion(handler.getBukkitEntity().getLocation(), blastPower);
            }
        }
    }
}

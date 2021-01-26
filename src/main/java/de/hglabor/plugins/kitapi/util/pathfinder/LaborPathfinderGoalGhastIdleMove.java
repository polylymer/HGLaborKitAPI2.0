package de.hglabor.plugins.kitapi.util.pathfinder;

import net.minecraft.server.v1_16_R3.ControllerMove;
import net.minecraft.server.v1_16_R3.EntityGhast;
import net.minecraft.server.v1_16_R3.PathfinderGoal;

import java.util.EnumSet;
import java.util.Random;

public class LaborPathfinderGoalGhastIdleMove extends PathfinderGoal {
    private final EntityGhast a;

    public LaborPathfinderGoalGhastIdleMove(EntityGhast entityghast) {
        this.a = entityghast;
        this.a(EnumSet.of(Type.MOVE));
    }

    public boolean a() {
        ControllerMove controllermove = this.a.getControllerMove();
        if (!controllermove.b()) {
            return true;
        } else {
            double d0 = controllermove.d() - this.a.locX();
            double d1 = controllermove.e() - this.a.locY();
            double d2 = controllermove.f() - this.a.locZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
        }
    }

    public boolean b() {
        return false;
    }

    public void c() {
        Random random = this.a.getRandom();
        double d0 = this.a.locX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double d1 = this.a.locY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double d2 = this.a.locZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        this.a.getControllerMove().a(d0, d1, d2, 1.0D);
    }
}

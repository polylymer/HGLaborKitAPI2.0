package de.hglabor.plugins.kitapi.util.pathfinder;

import net.minecraft.server.v1_16_R3.*;

import java.util.EnumSet;

public class LaborPathfinderGoalBlazeFireball extends PathfinderGoal {
    private final EntityBlaze blaze;
    private int b;
    private int c;
    private int d;

    public LaborPathfinderGoalBlazeFireball(EntityBlaze var0) {
        this.blaze = var0;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK));
    }

    public boolean a() {
        EntityLiving var0 = this.blaze.getGoalTarget();
        return var0 != null && var0.isAlive() && this.blaze.c(var0);
    }

    public void c() {
        this.b = 0;
    }

    public void d() {
      //  this.blaze.t(false);
        this.d = 0;
    }

    private double g() {
        return this.blaze.b(GenericAttributes.FOLLOW_RANGE);
    }

    public void e() {
        --this.c;
        EntityLiving var0 = this.blaze.getGoalTarget();
        if (var0 != null) {
            boolean var1 = this.blaze.getEntitySenses().a(var0);
            if (var1) {
                this.d = 0;
            } else {
                ++this.d;
            }

            double var2 = this.blaze.h(var0);
            if (var2 < 4.0D) {
                if (!var1) {
                    return;
                }

                if (this.c <= 0) {
                    this.c = 20;
                    this.blaze.attackEntity(var0);
                }

                this.blaze.getControllerMove().a(var0.locX(), var0.locY(), var0.locZ(), 1.0D);
            } else if (var2 < this.g() * this.g() && var1) {
                double var4 = var0.locX() - this.blaze.locX();
                double var6 = var0.e(0.5D) - this.blaze.e(0.5D);
                double var8 = var0.locZ() - this.blaze.locZ();
                if (this.c <= 0) {
                    ++this.b;
                    if (this.b == 1) {
                        this.c = 60;
                      //  this.blaze.t(true);
                    } else if (this.b <= 4) {
                        this.c = 6;
                    } else {
                        this.c = 100;
                        this.b = 0;
                    //    this.blaze.t(false);
                    }

                    if (this.b > 1) {
                        float var10 = MathHelper.c(MathHelper.sqrt(var2)) * 0.5F;
                        if (!this.blaze.isSilent()) {
                            this.blaze.world.a(null, 1018, this.blaze.getChunkCoordinates(), 0);
                        }

                        for (int var11 = 0; var11 < 1; ++var11) {
                            EntitySmallFireball var12 = new EntitySmallFireball(this.blaze.world, this.blaze, var4 + this.blaze.getRandom().nextGaussian() * (double) var10, var6, var8 + this.blaze.getRandom().nextGaussian() * (double) var10);
                            var12.setPosition(var12.locX(), this.blaze.e(0.5D) + 0.5D, var12.locZ());
                            this.blaze.world.addEntity(var12);
                        }
                    }
                }

                this.blaze.getControllerLook().a(var0, 10.0F, 10.0F);
            } else if (this.d < 5) {
                this.blaze.getControllerMove().a(var0.locX(), var0.locY(), var0.locZ(), 1.0D);
            }

            super.e();
        }
    }
}

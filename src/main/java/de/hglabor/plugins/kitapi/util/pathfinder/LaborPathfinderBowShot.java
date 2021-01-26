package de.hglabor.plugins.kitapi.util.pathfinder;

import net.minecraft.server.v1_16_R3.*;

import java.util.EnumSet;

public class LaborPathfinderBowShot extends PathfinderGoal {
    private final EntityMonster a;
    private final double b;
    private int c;
    private final float d;
    private int e = -1;
    private int f;
    private boolean g;
    private boolean h;
    private int i = -1;

    public LaborPathfinderBowShot(EntityMonster var0, double var1, int var3, float var4) {
        this.a = var0;
        this.b = var1;
        this.c = var3;
        this.d = var4 * var4;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK));
    }

    public void a(int var0) {
        this.c = var0;
    }

    public boolean a() {
        return this.a.getGoalTarget() != null && this.g();
    }

    protected boolean g() {
        return this.a.a(Items.BOW);
    }

    public boolean b() {
        return (this.a() || !this.a.getNavigation().m()) && this.g();
    }

    public void c() {
        super.c();
        this.a.setAggressive(true);
    }

    public void d() {
        super.d();
        this.a.setAggressive(false);
        this.f = 0;
        this.e = -1;
        this.a.clearActiveItem();
    }

    public void e() {
        EntityLiving var0 = this.a.getGoalTarget();
        if (var0 != null) {
            double var1 = this.a.h(var0.locX(), var0.locY(), var0.locZ());
            boolean var3 = this.a.getEntitySenses().a(var0);
            boolean var4 = this.f > 0;
            if (var3 != var4) {
                this.f = 0;
            }

            if (var3) {
                ++this.f;
            } else {
                --this.f;
            }

            if (var1 <= (double) this.d && this.f >= 20) {
                this.a.getNavigation().o();
                ++this.i;
            } else {
                this.a.getNavigation().a(var0, this.b);
                this.i = -1;
            }

            if (this.i >= 20) {
                if ((double) this.a.getRandom().nextFloat() < 0.3D) {
                    this.g = !this.g;
                }

                if ((double) this.a.getRandom().nextFloat() < 0.3D) {
                    this.h = !this.h;
                }

                this.i = 0;
            }

            if (this.i > -1) {
                if (var1 > (double) (this.d * 0.75F)) {
                    this.h = false;
                } else if (var1 < (double) (this.d * 0.25F)) {
                    this.h = true;
                }

                this.a.getControllerMove().a(this.h ? -0.5F : 0.5F, this.g ? 0.5F : -0.5F);
                this.a.a(var0, 30.0F, 30.0F);
            } else {
                this.a.getControllerLook().a(var0, 30.0F, 30.0F);
            }

            if (this.a.isHandRaised()) {
                if (!var3 && this.f < -60) {
                    this.a.clearActiveItem();
                } else if (var3) {
                    int var5 = this.a.dZ();
                    if (var5 >= 20) {
                        this.a.clearActiveItem();
                        ((IRangedEntity) this.a).a(var0, ItemBow.a(var5));
                        this.e = this.c;
                    }
                }
            } else if (--this.e <= 0 && this.f >= -60) {
                this.a.c(ProjectileHelper.a(this.a, Items.BOW));
            }

        }
    }
}

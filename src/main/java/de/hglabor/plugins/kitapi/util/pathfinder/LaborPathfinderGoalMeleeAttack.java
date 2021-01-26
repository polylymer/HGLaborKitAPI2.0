package de.hglabor.plugins.kitapi.util.pathfinder;

import net.minecraft.server.v1_16_R3.*;

import java.util.EnumSet;
import java.util.Random;

public class LaborPathfinderGoalMeleeAttack extends PathfinderGoal {
    protected final EntityCreature a;
    protected int b;
    private final double d;
    private final boolean e;
    private PathEntity f;
    private int g;
    private double h;
    private double i;
    private double j;
    protected final int c = 20;
    private long k;
    private double range = 9;
    private int tick = 5;
    private Random random = new Random();

    public LaborPathfinderGoalMeleeAttack(EntityCreature var0, double var1, boolean var3) {
        this.a = var0;
        this.d = var1;
        this.e = var3;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK));
    }

    public LaborPathfinderGoalMeleeAttack(EntityCreature var0, double var1, boolean var3, double range, int tick) {
        this.a = var0;
        this.d = var1;
        this.e = var3;
        this.range = range * range;
        this.tick = tick;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK));
    }

    public boolean a() {
        long var0 = this.a.world.getTime();
        if (var0 - this.k < 20L) {
            return false;
        } else {
            this.k = var0;
            EntityLiving goalTarget = this.a.getGoalTarget();
            if (goalTarget == null) {
                return false;
            } else if (!goalTarget.isAlive()) {
                return false;
            } else {
                this.f = this.a.getNavigation().a(goalTarget, 0);
                if (this.f != null) {
                    return true;
                } else {
                    return this.a(goalTarget) >= this.a.h(goalTarget.locX(), goalTarget.locY(), goalTarget.locZ());
                }
            }
        }
    }

    public boolean b() {
        EntityLiving goalTarget = this.a.getGoalTarget();
        if (goalTarget == null) {
            return false;
        } else if (!goalTarget.isAlive()) {
            return false;
        } else if (!this.e) {
            return !this.a.getNavigation().m();
        } else if (!this.a.a(goalTarget.getChunkCoordinates())) {
            return false;
        } else {
            return !(goalTarget instanceof EntityHuman) || !goalTarget.isSpectator() && !((EntityHuman) goalTarget).isCreative();
        }
    }

    //Target gefunden
    public void c() {
        this.a.getNavigation().a(this.f, this.d);
        this.a.setAggressive(true);
        this.g = 0;
        this.a.getControllerJump().jump();
    }

    public void d() {
        EntityLiving var0 = this.a.getGoalTarget();
        if (!IEntitySelector.e.test(var0)) {
            this.a.setGoalTarget(null);
        }

        this.a.setAggressive(false);
        this.a.getNavigation().o();
    }

    public void e() {
        EntityLiving attacker = this.a.getGoalTarget();
        if (attacker == null) return;
        this.a.getControllerLook().a(attacker, 30.0F, 30.0F);
        double distanceToPlayer = this.a.h(attacker.locX(), attacker.locY(), attacker.locZ());
        --this.g;
        if ((this.e || this.a.getEntitySenses().a(attacker)) && this.g <= 0 && (this.h == 0.0D && this.i == 0.0D && this.j == 0.0D || attacker.h(this.h, this.i, this.j) >= 1.0D || this.a.getRandom().nextFloat() < 0.05F)) {
            this.h = attacker.locX();
            this.i = attacker.locY();
            this.j = attacker.locZ();
            this.g = 4 + this.a.getRandom().nextInt(7);
            if (distanceToPlayer > 1024.0D) {
                this.g += 10;
            } else if (distanceToPlayer > 256.0D) {
                this.g += 5;
            }

            if (!this.a.getNavigation().a(attacker, this.d)) {
                this.g += 15;
            }
        }

        this.b = Math.max(this.b - 1, 0);
        this.a(attacker, distanceToPlayer);

    }

    protected void a(EntityLiving var0, double distanceToPlayer) {
        final double attackRadius = range;
        // System.out.println(distanceToPlayer); //5
        // System.out.println(attackRadius); //2
        // System.out.println(this.b); //0
        if (distanceToPlayer <= attackRadius && this.b <= 0) {
            this.b = tick;
            this.a.swingHand(EnumHand.MAIN_HAND);
            this.a.attackEntity(var0);
        }

    }

    protected double a(EntityLiving var0) {
        return this.a.getWidth() * 2.0F * this.a.getWidth() * 2.0F + var0.getWidth();
    }
}

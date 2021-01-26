package de.hglabor.plugins.kitapi.util.pathfinder;

import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.UUID;

public class LaborPathfinderFindTarget extends PathfinderGoal {
    private final KitPlayer kitPlayer;
    public EntityInsentient mob;
    public UUID safe;
    public boolean attack;

    public LaborPathfinderFindTarget(EntityInsentient mob, UUID safe, boolean attack) {
        this.mob = mob;
        this.safe = safe;
        this.attack = attack;
        this.kitPlayer = KitManager.getInstance().getPlayer(Bukkit.getPlayer(safe));
    }

    @Override
    public boolean a() {
        Entity target = kitPlayer.getLastHitInformation().getLastEntity();

        if (kitPlayer.getLastHitInformation().getEntityTimeStamp() + (long) (10 * 1000) < System.currentTimeMillis()) {
            mob.setGoalTarget(null, EntityTargetEvent.TargetReason.CUSTOM, true);
            return false;
        }


        if (target == null) {
            mob.setGoalTarget(null, EntityTargetEvent.TargetReason.CUSTOM, true);
            return false;
        }


        if (!(((CraftLivingEntity) target).getHandle() instanceof EntityPlayer)) {
            mob.setGoalTarget(null, EntityTargetEvent.TargetReason.CUSTOM, true);
            return false;
        }

        if (target.getUniqueId().equals(safe)) {
            mob.setGoalTarget(null, EntityTargetEvent.TargetReason.CUSTOM, true);
            return false;
        }


        mob.setGoalTarget(((CraftPlayer) target).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        return true;
    }

    @Override
    public void e() {
        if (mob.getGoalTarget() == null) {
            return;
        }

        mob.getNavigation().a(mob.getGoalTarget().getBukkitEntity().getLocation().getX(),
                mob.getGoalTarget().getBukkitEntity().getLocation().getY() + 1.0D,
                mob.getGoalTarget().getBukkitEntity().getLocation().getZ(), 1.5F);

        if (!attack) { // We want the entity to not attack but able to move from the method above.
            return;
        }

        if (mob.getBukkitEntity().getLocation().distance(mob.getGoalTarget().getBukkitEntity().getLocation()) <= 1.5D) {
            if (mob.getEntitySenses().a(mob.getGoalTarget())) { // canSee method
                ((LivingEntity) mob.getGoalTarget().getBukkitEntity()).damage(4.0D, Bukkit.getPlayer(safe));
            }
        }
    }

    @Override
    public boolean b() {
        return false;
    }
}

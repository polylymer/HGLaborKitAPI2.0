package de.hglabor.plugins.kitapi.util.pathfinder;


import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LaborPathfinderMoveToPlayer extends PathfinderGoal {
    private final Player player;
    private final EntityInsentient mob;

    public LaborPathfinderMoveToPlayer(Player player, EntityInsentient mob) {
        this.player = player;
        this.mob = mob;
    }

    @Override
    public boolean a() {
        return player != null && !player.isDead();
    }

    // e() executes whenever a() returns true

    @Override
    public void e() {

        if (player == null || player.isDead()) {
            return;
        }

        if (mob == null) {
            return;
        }


        if (mob.getBukkitEntity().getLocation().distanceSquared(player.getLocation()) >= 1200) {
            mob.getBukkitEntity().teleport(player.getLocation().add(0, 1, 0));
            mob.setGoalTarget(null);
            return;
        }

        if (mob.getGoalTarget() != null) {
            return;
        }

        if (mob.getBukkitEntity().getLocation().distanceSquared(player.getLocation()) >= 24.0) {
            mob.getNavigation().a(player.getLocation().getX(), player.getLocation().getY() + 1.0D, player.getLocation().getZ(), 1.5F);
            mob.getControllerLook().a(((CraftPlayer) player).getHandle(), 10.0F, 0.0F);
        }
    }

    @Override
    public boolean b() {
        return false;
    }
}

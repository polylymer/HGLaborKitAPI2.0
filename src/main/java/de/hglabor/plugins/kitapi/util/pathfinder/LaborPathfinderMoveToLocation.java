package de.hglabor.plugins.kitapi.util.pathfinder;


import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;

public class LaborPathfinderMoveToLocation extends PathfinderGoal {
    private EntityInsentient monster;
    private Location location;

    public LaborPathfinderMoveToLocation(Location location, EntityInsentient monster) {
        this.location = location;
        this.monster = monster;
    }

    public LaborPathfinderMoveToLocation(Location location, EntityInsentient monster, double speed) {
        this.location = location;
        this.monster = monster;
        monster.craftAttributes.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
    }

    @Override
    public boolean a() {
        return monster.getGoalTarget() == null;
    }

    // e() executes whenever a() returns true

    @Override
    public void e() {

        if (monster == null) {
            return;
        }

        monster.getNavigation().a(location.getX(), location.getY() + 1.0D, location.getZ(), 1.5F);

    }

    @Override
    public boolean b() {
        return false;
    }
}

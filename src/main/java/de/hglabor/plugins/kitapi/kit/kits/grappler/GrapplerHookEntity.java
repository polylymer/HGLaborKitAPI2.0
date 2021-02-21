package de.hglabor.plugins.kitapi.kit.kits.grappler;

import de.hglabor.plugins.kitapi.util.Logger;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityFishingHook;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.World;

class GrapplerHookEntity extends EntityFishingHook {
    public GrapplerHookEntity(EntityHuman entityhuman, World world, int i, int j) {
        super(entityhuman, world, i, j);
    }

    @Override
    public void die() {
    }

    protected void remove() {
        super.die();
        EntityHuman entityhuman = this.getOwner();
        if (entityhuman != null) {
            entityhuman.hookedFish = null;
        }
    }
}

package de.hglabor.plugins.kitapi.kit.kits.analyst;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.pvp.recraft.Recraft;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AnalystHologram extends EntityArmorStand {
    private final Player target;
    private final Player owner;
    private final HologramType type;
    private final double boost;

    public AnalystHologram(World world, Player target, Player owner, HologramType type, double boost) {
        super(EntityTypes.ARMOR_STAND, world);
        this.owner = owner;
        this.type = type;
        this.boost = boost;
        this.setPosition(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ());
        this.target = target;
        this.setCustomNameVisible(true);
        this.setInvisible(true);
        this.setMarker(true);
        this.setSilent(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (!target.isOnline() || target.isDead() || !target.isValid()) {
            this.die();
            return;
        }
        if (!owner.isOnline() || owner.isDead() || !owner.isValid()) {
            this.die();
            return;
        }
        this.setCustomName(new ChatComponentText(getInformation()));
        Location location = target.getEyeLocation().clone();
        this.teleportAndSync(location.getX(), location.getY() + boost, location.getZ());
    }

    private String getInformation() {
        switch (type) {
            case SOUPS:
                return "Soups: " + getMaterialAmount(org.bukkit.Material.MUSHROOM_STEW);
            case RECRAFT:
                Recraft recraft = new Recraft();
                recraft.calcRecraft(target.getInventory().getContents());
                return String.format("Recraft: %sx", recraft.getRecraftPoints());
            case CPS:
                KitPlayer player = KitApi.getInstance().getPlayer(target);
                return String.format("CPS: %s", player.getLeftCps());
            default:
                return "";
        }
    }

    public int getMaterialAmount(org.bukkit.Material material) {
        return target.getInventory().all(material).values().stream().mapToInt(ItemStack::getAmount).sum();
    }

    enum HologramType {
        SOUPS, RECRAFT, CPS
    }
}

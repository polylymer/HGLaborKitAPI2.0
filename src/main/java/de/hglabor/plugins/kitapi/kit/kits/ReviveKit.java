package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;

public class ReviveKit extends AbstractKit {
    public static final ReviveKit INSTANCE = new ReviveKit();

    private ReviveKit() {
        super("Revive", Material.TOTEM_OF_UNDYING, 60);
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        player.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if(player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }
}

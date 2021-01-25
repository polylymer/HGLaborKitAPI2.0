package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ReviveKit extends AbstractKit {
    public static final ReviveKit INSTANCE = new ReviveKit();

    private ReviveKit() {
        super("Revive", Material.TOTEM_OF_UNDYING, 60);
        setMainKitItem(getDisplayMaterial());
        setUsesOffHand(true);
        addEvents(Collections.singletonList(EntityResurrectEvent.class));
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player != null) {
            if (player.getInventory().getItemInOffHand().isSimilar(this.getMainKitItem())) {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        }
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        giveTotem(kitPlayer, player);
    }

    @Override
    public void onEntityResurrect(EntityResurrectEvent event) {
        Player player = (Player) event.getEntity();
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        kitPlayer.putKitAttribute(this, kitPlayer.getKitAttribute(this) != null ? (Integer) kitPlayer.getKitAttribute(this) + 1 : 1);
        Bukkit.getScheduler().runTaskLater(KitManager.getInstance().getPlugin(), () -> {
            giveTotem(kitPlayer, player);
        }, (long) getCooldown() * 20 * (Integer) kitPlayer.getKitAttribute(this));
    }

    private void giveTotem(KitPlayer kitPlayer, Player player) {
        if (kitPlayer.isValid() && kitPlayer.hasKit(this)) {
            player.getInventory().setItemInOffHand(this.getMainKitItem());
        }
    }
}

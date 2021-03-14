package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;

public class ReviveKit extends AbstractKit {
    public static final ReviveKit INSTANCE = new ReviveKit();
    private final String deathCounterKey;
    @FloatArg(min = 0.0F)
    private final float cooldown;

    private ReviveKit() {
        super("Revive", Material.TOTEM_OF_UNDYING);
        cooldown = 60;
        deathCounterKey = this.getName() + "counter";
        setMainKitItem(getDisplayMaterial());
        setUsesOffHand(true);
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player != null) {
            if (player.getInventory().getItemInOffHand().isSimilar(this.getMainKitItem())) {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        }
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        giveTotem(kitPlayer, player);
    }

    @KitEvent
    @Override
    public void onEntityResurrect(EntityResurrectEvent event) {
        Player player = (Player) event.getEntity();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        kitPlayer.putKitAttribute(deathCounterKey, kitPlayer.getKitAttributeOrDefault(deathCounterKey, 1) + 1);
        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
            giveTotem(kitPlayer, player);
        }, (long) getCooldown() * 20 * (Integer) kitPlayer.getKitAttribute(deathCounterKey));
    }

    private void giveTotem(KitPlayer kitPlayer, Player player) {
        if (kitPlayer.isValid() && kitPlayer.hasKit(this)) {
            player.getInventory().setItemInOffHand(this.getMainKitItem());
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

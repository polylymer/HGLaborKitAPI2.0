package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;

public class TankKit extends AbstractKit {
    public static final TankKit INSTANCE = new TankKit();

    @FloatArg(min = 0.1F, max = 100F)
    private final float explosionSizePlayer;
    @FloatArg(min = 0.1F, max = 100F)
    private final float explosionSizeEntity;
    @FloatArg(min = 0.1F, max = 100F)
    private final float explosionSizeRecraft;

    private TankKit() {
        super("Tank", Material.TNT);
        explosionSizePlayer = 6F;
        explosionSizeEntity = 3F;
        explosionSizeRecraft = 1F;
    }

    @KitEvent
    @Override
    public void onPlayerKillsLivingEntity(EntityDeathEvent event, Player killer, Entity entity) {
        float explosionSize = entity instanceof Player ? explosionSizePlayer : explosionSizeEntity;
        entity.getWorld().createExplosion(entity.getLocation(), explosionSize, false, true, killer);
    }

    @KitEvent(clazz = EntityDeathEvent.class)
    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        Player deadPlayer = Bukkit.getPlayer(dead.getUUID());
        Player killerPlayer = Bukkit.getPlayer(killer.getUUID());
        if (deadPlayer != null) {
            deadPlayer.getWorld().createExplosion(deadPlayer.getLocation(), explosionSizePlayer, false, true, killerPlayer);
        }
    }

    @KitEvent
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            event.setCancelled(true);
        }
    }

    @KitEvent
    @Override
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getInventory().getViewers().get(0);
        if (!event.getRecipe().getResult().getType().equals(Material.MUSHROOM_STEW)) {
            return;
        }

        if (!(event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            return;
        }

        if (player.getInventory().firstEmpty() != -1) {
            player.getWorld().createExplosion(player.getLocation(), explosionSizeRecraft, false, true, player);
        }
    }
}

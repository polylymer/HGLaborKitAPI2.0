package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableList;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;

public class TankKit extends AbstractKit {
    public static final TankKit INSTANCE = new TankKit();

    protected TankKit() {
        super("Tank", Material.TNT);
        addSetting(KitSettings.EXPLOSION_SIZE_PLAYER, 6);
        addSetting(KitSettings.EXPLOSION_SIZE_ENTITY, 1);
        addSetting(KitSettings.EXPLOSION_SIZE_RECRAFT, 1);
        addEvents(ImmutableList.of(EntityDeathEvent.class, EntityDamageEvent.class, CraftItemEvent.class));
    }


    @Override
    public void onPlayerKillsLivingEntity(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        int explosionSize = entity instanceof Player ? (Integer) getSetting(KitSettings.EXPLOSION_SIZE_PLAYER) : (Integer) getSetting(KitSettings.EXPLOSION_SIZE_ENTITY);
        entity.getWorld().createExplosion(entity.getLocation(), explosionSize, false, true, killer);
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            event.setCancelled(true);
        }
    }

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
            player.getWorld().createExplosion(player.getLocation(), (Integer) getSetting(KitSettings.EXPLOSION_SIZE_RECRAFT), false, true, player);
        }
    }
}

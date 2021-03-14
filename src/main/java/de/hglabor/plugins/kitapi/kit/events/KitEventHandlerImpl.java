package de.hglabor.plugins.kitapi.kit.events;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class KitEventHandlerImpl extends KitEventHandler implements Listener {
    public KitEventHandlerImpl() {
        super(KitApi.getInstance().getPlayerSupplier());
    }

    @EventHandler
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getDamager());
            useKit(event, kitPlayer, kit -> kit.onPlayerAttacksLivingEntity(event, kitPlayer, (LivingEntity) event.getEntity()));
        }
    }

    @EventHandler
    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity && event.getEntity() instanceof Player) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getEntity());
            if (event.getDamager() instanceof Player) {
                if (!playerSupplier.getKitPlayer((Player) event.getDamager()).isValid()) return;
            }
            useKit(event, kitPlayer, kit -> kit.onPlayerGetsAttackedByLivingEntity(event, (Player) event.getEntity(), (LivingEntity) event.getDamager()));
        }
    }

    @EventHandler
    public void onToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
            useKit(event, kitPlayer, kit -> kit.onPlayerToggleSneakEvent(event));
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getWhoClicked());
        useKit(event, kitPlayer, kit -> kit.onCraftItem(event));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getEntity());
            useKit(event, kitPlayer, kit -> kit.onEntityDamage(event));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getEntity());
            useKit(event, kitPlayer, kit -> kit.onEntityDeath(event));
        }
    }

    @EventHandler
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
            useKitItem(event, kitPlayer, kit -> kit.onPlayerRightClickPlayerWithKitItem(event));
        } else if (event.getRightClicked() instanceof LivingEntity) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
            useKitItem(event, kitPlayer, kit -> kit.onPlayerRightClickLivingEntityWithKitItem(event));
        }
    }

    @EventHandler
    public void onHitLivingEntityWithKitItem(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getDamager());
            for (AbstractKit playerKit : kitPlayer.getKits()) {
                if (((Player) event.getDamager()).getInventory().getItemInMainHand().isSimilar(playerKit.getMainKitItem())) {
                    useKitItem(event, kitPlayer, kit -> kit.onHitLivingEntityWithKitItem(event, kitPlayer, (LivingEntity) event.getEntity()));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerKillsLivingEntity(EntityDeathEvent event, Player killer, Entity entity) {
        if (event.getEntity().getKiller() != null) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getEntity().getKiller());
            useKit(event, kitPlayer, kit -> kit.onPlayerKillsLivingEntity(event, event.getEntity().getKiller(), event.getEntity()));
        }
    }

    @EventHandler
    public void onKitPlayerDeath(PlayerDeathEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getEntity());
        useKit(event, kitPlayer, kit -> kit.onKitPlayerDeath(event));
    }

    @EventHandler
    public void onBlockBreakWithKitItem(BlockBreakEvent event) {
        KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
        useKitItem(event, kitPlayer, kit -> kit.onBlockBreakWithKitItem(event));
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getEntity().getShooter());
            useKit(event, kitPlayer, kit -> kit.onProjectileLaunch(event));
        }
    }

    @EventHandler
    public void onPlayerKillsPlayer(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer(killer);
            useKit(event, kitPlayer, kit -> kit.onPlayerKillsPlayer(
                    KitApi.getInstance().getPlayer(killer),
                    KitApi.getInstance().getPlayer(event.getEntity())));
        }
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (event.getEntity() instanceof Player) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer((Player) event.getEntity());
            useKit(event, kitPlayer, kit -> kit.onEntityResurrect(event));
        }
    }

    @EventHandler
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
            useKitItem(event, kitPlayer, kit -> kit.onPlayerRightClickKitItem(event));
        }
    }

    @EventHandler
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            KitPlayer kitPlayer = playerSupplier.getKitPlayer(event.getPlayer());
            useKitItem(event, kitPlayer, kit -> kit.onPlayerLeftClickKitItem(event));
        }
    }

    public void useKit(Event event, KitPlayer kitPlayer, KitExecutor kitExecutor) {
        kitPlayer.getKits().stream().filter(kit -> canUseKit(event, kitPlayer, kit)).forEach(kitExecutor::execute);
    }

    public void useKitItem(Event event, KitPlayer kitPlayer, KitExecutor kitExecutor) {
        kitPlayer.getKits().stream().filter(kit -> canUseKitItem(event, kitPlayer, kit)).forEach(kitExecutor::execute);
    }

    @FunctionalInterface
    interface KitExecutor {
        void execute(AbstractKit kit);
    }
}

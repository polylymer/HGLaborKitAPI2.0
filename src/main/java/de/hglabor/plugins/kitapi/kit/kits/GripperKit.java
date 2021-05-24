package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.BoolArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;

public class GripperKit extends AbstractKit {

    public static final GripperKit INSTANCE = new GripperKit();
    private final Material[] disabledBlocks = {Material.BEDROCK, Material.OBSIDIAN, Material.BARRIER};

    @FloatArg(min = 0.0F)
    private final float cooldown;

    @BoolArg
    private final boolean canGrabBlocks;

    private final String grippedEntityKey = "grippedEntity";
    private final String runnableKey = "gripperRunnable";

    private GripperKit() {
        super("Gripper", Material.MINECART);
        setMainKitItem(getDisplayMaterial());
        this.cooldown = 8f;
        this.canGrabBlocks = true;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public void onEnable(KitPlayer kitPlayer) {
        BukkitTask bukkitTask;
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                Entity grippedEntity = kitPlayer.getKitAttribute(grippedEntityKey);
                if(grippedEntity != null) {
                    Optional<Player> optionalPlayer = kitPlayer.getBukkitPlayer();
                    if(optionalPlayer.isPresent()) {
                        Player player = optionalPlayer.get();
                        if(player.getInventory().getItemInMainHand().isSimilar(getMainKitItem())) {
                            if(grippedEntity instanceof Player) {
                                if(((Player) grippedEntity).isSneaking()) {
                                    kitPlayer.putKitAttribute(grippedEntityKey, null);
                                    kitPlayer.activateKitCooldown(INSTANCE);
                                }
                            }
                            Location location = player.getLocation().clone().add(player.getLocation().clone().getDirection().normalize().multiply(3));
                            location.setYaw(grippedEntity.getLocation().getYaw());
                            location.setPitch(grippedEntity.getLocation().getPitch());
                            grippedEntity.teleport(location);
                        } else {
                            kitPlayer.putKitAttribute(grippedEntityKey, null);
                        }
                    }
                }
            }
        }.runTaskTimer(KitApi.getInstance().getPlugin(), 2, 1);
        kitPlayer.putKitAttribute(runnableKey, bukkitTask);
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        BukkitTask bukkitTask = kitPlayer.getKitAttribute(runnableKey);
        if(bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    @KitEvent
    @Override
    public void onPlayerRightClickLivingEntityWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, LivingEntity entity) {
        Entity grippedEntity = kitPlayer.getKitAttribute(grippedEntityKey);
        if(grippedEntity == null) {
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, 1);
            kitPlayer.putKitAttribute(grippedEntityKey, (Entity)entity);
        } else {
            grippedEntity.getWorld().playSound(grippedEntity.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 10);
            kitPlayer.getBukkitPlayer().ifPresent(it -> it.sendMessage(Localization.INSTANCE.getMessage("gripper.alreadyGripped", ChatUtils.locale(it))));
        }
        event.setCancelled(true);
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitPlayer kitPlayer = KitApi.getInstance().getPlayer(player);
        if(event.hasBlock()) {
            if(canGrabBlocks) {
                Block block = event.getClickedBlock();
                if(block == null || event.isCancelled()) {
                    return;
                }
                if(List.of(disabledBlocks).contains(block.getType())) {
                    return;
                }
                FallingBlock grippedBlock = kitPlayer.getKitAttribute(grippedEntityKey);
                if(grippedBlock != null) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 10);
                    kitPlayer.getBukkitPlayer().ifPresent(it -> it.sendMessage(Localization.INSTANCE.getMessage("gripper.alreadyGripped", ChatUtils.locale(it))));
                } else {
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(player.getLocation(), block.getBlockData());
                    fallingBlock.setGravity(false);
                    kitPlayer.putKitAttribute(grippedEntityKey, fallingBlock);
                    block.setType(Material.AIR);
                }
            }
        }
    }

    @KitEvent(ignoreCooldown = true)
    @Override
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event, KitPlayer kitPlayer) {
        Entity grippedEntity = kitPlayer.getKitAttribute(grippedEntityKey);
        if(grippedEntity != null) {
            kitPlayer.putKitAttribute(grippedEntityKey, null);
            grippedEntity.getWorld().playSound(grippedEntity.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1);
            grippedEntity.setVelocity(event.getPlayer().getLocation().getDirection().multiply(1.3));
            kitPlayer.activateKitCooldown(this);
        }
    }
}

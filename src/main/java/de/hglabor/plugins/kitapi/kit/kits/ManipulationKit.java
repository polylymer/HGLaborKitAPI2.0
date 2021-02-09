package de.hglabor.plugins.kitapi.kit.kits;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.KitManager;
import de.hglabor.plugins.kitapi.kit.config.KitMetaData;
import de.hglabor.plugins.kitapi.kit.config.KitSettings;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import de.hglabor.plugins.kitapi.util.pathfinder.*;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ManipulationKit extends AbstractKit implements Listener {
    public final static ManipulationKit INSTANCE = new ManipulationKit();

    public ManipulationKit() {
        super("Manipulation", Material.IRON_NUGGET);
        setMainKitItem(getDisplayMaterial());
        addEvents(ImmutableList.of(PlayerInteractAtEntityEvent.class));
        addSetting(KitSettings.USES, 4);
    }

    @Override
    public void enable(KitPlayer kitPlayer) {
        kitPlayer.putKitAttribute(this, new HashSet<UUID>());
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        Set<UUID> controlledMobs = kitPlayer.getKitAttribute(this);
        if (controlledMobs != null) {
            for (UUID controlledMob : controlledMobs) {
                Entity entity = Bukkit.getEntity(controlledMob);
                if (entity != null) {
                    entity.remove();
                }
            }
        }
    }

    @Override
    public void onPlayerRightClickLivingEntityWithKitItem(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Mob) {

            Mob mob = (Mob) event.getRightClicked();
            EntityInsentient craftMonster = (EntityInsentient) ((CraftEntity) mob).getHandle();

            if (isManipulatedMob(mob)) {
                Player manipulator = getManipulator(mob);
                if (manipulator != null && manipulator.getUniqueId().equals(player.getUniqueId())) {
                    //NACHRICHT KOMMT 2x WEGEN 2 HÄNDEN
                    player.sendMessage(Localization.INSTANCE.getMessage("manipulator.alreadyYourMob", ChatUtils.getPlayerLocale(player)));
                    return;
                }
                player.sendMessage(Localization.INSTANCE.getMessage("manipulator.alreadyControlled", ChatUtils.getPlayerLocale(player)));
                return;
            }

            if (getManipulatedMobAmount(player) > (Integer) getSetting(KitSettings.USES)) {
                player.sendMessage(Localization.INSTANCE.getMessage("manipulator.maxAmount", ChatUtils.getPlayerLocale(player)));
                return;
            }

            mob.setMetadata(KitMetaData.MANIPULATED_MOB.getKey(), new FixedMetadataValue(KitManager.getInstance().getPlugin(), ""));
            mob.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(KitManager.getInstance().getPlugin(), ""));

            addMob(player, mob);
            boolean attack = mob.getType() != EntityType.CREEPER && mob.getType() != EntityType.SKELETON;

            // Clear pathfinders to apply ours.
            clearPathfinders(craftMonster);
            mob.setTarget(null);
            craftMonster.goalSelector.a(0, new LaborPathfinderMoveToPlayer(player, craftMonster));
            craftMonster.goalSelector.a(1, new PathfinderGoalFloat(craftMonster));
            craftMonster.targetSelector.a(0, new LaborPathfinderFindTarget(craftMonster, player.getUniqueId(), attack));

            //TODO ghast wont shoot and creeper doesnt explode
            switch (mob.getType()) {
                case HUSK:
                case ZOMBIE:
                case ENDERMAN:
                    craftMonster.goalSelector.a(3, new LaborPathfinderGoalMeleeAttack((EntityCreature) ((CraftEntity) mob).getHandle(), 0.3, true, 3, 20));
                    break;
                case SKELETON:
                    craftMonster.goalSelector.a(3, new LaborPathfinderBowShot((EntityMonster) craftMonster, 1.0D, 20, 15.0F));
                    break;
                case CREEPER:
                    ((Creeper) mob).setPowered(false);
                    ((Creeper) mob).setIgnited(false);
                    craftMonster.goalSelector.a(3, new LaborPathfinderGoalSwell(((EntityCreeper) craftMonster)));
                    break;
                case BLAZE:
                    craftMonster.goalSelector.a(3, new LaborPathfinderGoalBlazeFireball((EntityBlaze) craftMonster));
                    break;
                case GHAST:
                    craftMonster.goalSelector.a(3, new LaborPathfinderGhastAttack((EntityGhast) craftMonster));
                    craftMonster.goalSelector.a(4, new LaborPathfinderGoalGhastIdleMove((EntityGhast) craftMonster));
                    break;
                default:
                    break;
            }

            player.getWorld().spawnParticle(Particle.HEART, mob.getEyeLocation(), 1);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 10, 10);
        }
    }


    @EventHandler
    public void onEntityDespawn(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) event.getEntity();
        if (isManipulatedMob(mob)) {
            removeMob(mob);
            if (getManipulator(mob) == null) {
                Player manipulator = getManipulator(mob);
                if (manipulator != null) {
                    manipulator.sendMessage(Localization.INSTANCE.getMessage("manipulator.mobLoose",
                            ImmutableMap.of("amount", String.valueOf(getManipulatedMobAmount(manipulator))),
                            ChatUtils.getPlayerLocale(manipulator)));
                }
            }
        }
    }

    private void addMob(Player player, Entity mob) {
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        Set<UUID> controlledMobs = kitPlayer.getKitAttribute(this);
        controlledMobs.add(mob.getUniqueId());
    }

    private void removeMob(Mob mob) {
        Player player = getManipulator(mob);
        if (player != null) {
            KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
            Set<UUID> controlledMobs = kitPlayer.getKitAttribute(this);
            controlledMobs.remove(mob.getUniqueId());
        }
    }

    private int getManipulatedMobAmount(Player player) {
        KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
        Set<UUID> controlledMobs = kitPlayer.getKitAttribute(this);
        return controlledMobs.size();
    }

    private boolean isManipulatedMob(Mob entity) {
        return entity.hasMetadata(KitMetaData.MANIPULATED_MOB.getKey());
    }

    private Player getManipulator(Mob entity) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            KitPlayer kitPlayer = KitManager.getInstance().getPlayer(player);
            if (kitPlayer.isValid() && entity.hasMetadata(kitPlayer.getUUID().toString())) {
                return player;
            }
        }
        return null;
    }

    public void clearPathfinders(EntityInsentient entity) {
        entity.goalSelector = new PathfinderGoalSelector(entity.getWorld().getMethodProfilerSupplier());
        entity.targetSelector = new PathfinderGoalSelector(entity.getWorld().getMethodProfilerSupplier());
    }
}

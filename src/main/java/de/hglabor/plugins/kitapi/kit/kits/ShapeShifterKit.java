package de.hglabor.plugins.kitapi.kit.kits;

import com.comphenix.protocol.PacketType;
import com.google.common.collect.ImmutableList;
import de.hglabor.Localization.Localization;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.plugins.kitapi.util.Utils;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

public class ShapeShifterKit extends AbstractKit {
    public static final ShapeShifterKit INSTANCE = new ShapeShifterKit();

    private final List<Material> DISABLED_BLOCKS = Arrays.asList(Material.AIR, Material.BARRIER, Material.BEDROCK,
            Material.REDSTONE_WIRE, Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.TORCH, Material.WALL_TORCH
    );

    private ShapeShifterKit() {
        super("Shapeshifter", Material.REDSTONE_BLOCK);
        addEvents(ImmutableList.of(PlayerInteractEvent.class, EntityDamageByEntityEvent.class));
        setMainKitItem(getDisplayMaterial());
    }

    @Override
    public void disable(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player != null) {
            DisguiseAPI.undisguiseToAll(player);
        }
    }

    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (block != null) {
            if (DISABLED_BLOCKS.contains(block.getType())) {
                player.sendMessage(Localization.INSTANCE.getMessage("shapeshifter.denyTransformation", Utils.getPlayerLocale(player)));
                return;
            }
            MiscDisguise miscDisguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, block.getType());
            DisguiseAPI.disguiseEntity(player, miscDisguise);
        }
    }

    @Override
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event) {
        DisguiseAPI.undisguiseToAll(event.getPlayer());
    }

    @Override
    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
        if (attacker instanceof Player){
            DisguiseAPI.undisguiseToAll(player);
        }
    }
}

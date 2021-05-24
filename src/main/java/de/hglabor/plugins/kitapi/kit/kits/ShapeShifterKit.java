package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChatUtils;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShapeShifterKit extends AbstractKit {
    public static final ShapeShifterKit INSTANCE = new ShapeShifterKit();

    @FloatArg(min = 0.0F)
    private final float cooldown;


    private final List<Material> DISABLED_BLOCKS;

    private ShapeShifterKit() {
        super("Shapeshifter", Material.REDSTONE_BLOCK);
        setMainKitItem(getDisplayMaterial());
        cooldown = 10;
        DISABLED_BLOCKS = new ArrayList<>();
        DISABLED_BLOCKS.addAll(Arrays.asList(Material.AIR, Material.BARRIER, Material.BEDROCK, Material.SHULKER_BOX,
                Material.REDSTONE_WIRE, Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.TORCH, Material.WALL_TORCH,
                Material.CHEST, Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD, Material.CONDUIT));
        DISABLED_BLOCKS.addAll(Arrays.stream(Material.values()).filter(material -> material.name().endsWith("SIGN")).collect(Collectors.toList()));
        DISABLED_BLOCKS.addAll(Arrays.stream(Material.values()).filter(material -> material.name().endsWith("BED")).collect(Collectors.toList()));
        DISABLED_BLOCKS.addAll(Arrays.stream(Material.values()).filter(material -> material.name().endsWith("BANNER")).collect(Collectors.toList()));
        DISABLED_BLOCKS.addAll(Arrays.stream(Material.values()).filter(material -> material.name().endsWith("HEAD")).collect(Collectors.toList()));
        DISABLED_BLOCKS.addAll(Arrays.stream(Material.values()).filter(material -> material.name().endsWith("SKULL")).collect(Collectors.toList()));
    }

    @Override
    public void onDeactivation(KitPlayer kitPlayer) {
        Player player = Bukkit.getPlayer(kitPlayer.getUUID());
        if (player != null) {
            DisguiseAPI.undisguiseToAll(player);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (block != null) {
            if (DISABLED_BLOCKS.contains(block.getType())) {
                player.sendMessage(Localization.INSTANCE.getMessage("shapeshifter.denyTransformation", ChatUtils.locale(player)));
                return;
            }
            MiscDisguise miscDisguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, block.getType());
            DisguiseAPI.disguiseEntity(player, miscDisguise);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, false, false));
            KitApi.getInstance().getPlayer(player).activateKitCooldown(this);
        }
    }

    @KitEvent(ignoreCooldown = true)
    @Override
    public void onPlayerLeftClickKitItem(PlayerInteractEvent event, KitPlayer kitPlayer) {
        DisguiseAPI.undisguiseToAll(event.getPlayer());
        event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @KitEvent(ignoreCooldown = true)
    @Override
    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
        if (attacker instanceof Player) {
            DisguiseAPI.undisguiseToAll(player);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

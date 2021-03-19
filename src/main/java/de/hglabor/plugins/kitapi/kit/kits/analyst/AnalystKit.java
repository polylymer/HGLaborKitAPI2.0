package de.hglabor.plugins.kitapi.kit.kits.analyst;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.utils.noriskutils.NMSUtils;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalystKit extends AbstractKit implements Listener {
    public final static AnalystKit INSTANCE = new AnalystKit();
    @FloatArg
    public final float cooldown;
    @IntArg
    private final int hologramKeepAlive;
    private final Set<Integer> hologramIds;

    private AnalystKit() {
        super("Analyst", Material.GLASS_PANE);
        setMainKitItem(getDisplayMaterial());
        hologramIds = new HashSet<>();
        hologramKeepAlive = 10;
        cooldown = 60F;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        hologramIds.forEach(hologramId -> NMSUtils.sendPacket(player, new PacketPlayOutEntityDestroy(hologramId)));
    }

    @KitEvent
    @Override
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, Player rightClicked) {
        Player player = event.getPlayer();
        double boost = 0.25D;
        List<AnalystHologram> analystHolograms = new ArrayList<>();
        for (AnalystHologram.HologramType type : AnalystHologram.HologramType.values()) {
            boost += 0.25D;
            AnalystHologram analystHologram = new AnalystHologram(NMSUtils.getWorld(rightClicked), rightClicked, player, type, boost);
            analystHolograms.add(analystHologram);
            hologramIds.add(analystHologram.getId());
            NMSUtils.getWorld(player).addEntity(analystHologram);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer == player)
                    continue;
                NMSUtils.sendPacket(onlinePlayer, new PacketPlayOutEntityDestroy(analystHologram.getId()));
            }
        }
        Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
            for (AnalystHologram analystHologram : analystHolograms) {
                hologramIds.removeIf(integer -> integer == analystHologram.getId());
                analystHologram.die();
            }
        }, hologramKeepAlive * 20L);
        KitApi.getInstance().getPlayer(player).activateKitCooldown(this);
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import de.hglabor.utils.noriskutils.NMSUtils;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static de.hglabor.utils.localization.Localization.t;

public class PoseidonKit extends AbstractKit implements Listener {
    public static final PoseidonKit INSTANCE = new PoseidonKit();

    @IntArg(min = 0)
    private final int rainTime;
    @IntArg
    private final int speedAmplifier;
    @IntArg
    private final int regenerationAmplifier;
    private final String rainRunnable;

    private PoseidonKit() {
        super("Poseidon", new ItemBuilder(Material.TRIDENT).setEnchantment(Enchantment.RIPTIDE, 3).setName("Poseidon").build());
        rainTime = 25;
        speedAmplifier = 0;
        regenerationAmplifier = 0;
        rainRunnable = this.getName() + "rainRunnable";
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        if (kitPlayer.getKitAttribute(rainRunnable) != null) {
            ((PoseidonRain) kitPlayer.getKitAttribute(rainRunnable)).stop();
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        KitPlayer killer = KitApi.getInstance().getPlayer(player);
        if (killer.getKitAttribute(rainRunnable) != null) {
            ((PoseidonRain) killer.getKitAttribute(rainRunnable)).addTime(rainTime * 1000L);
        } else {
            PoseidonRain poseidonRain = new PoseidonRain(player);
            killer.putKitAttribute(rainRunnable, poseidonRain);
            poseidonRain.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
        }
    }

    @KitEvent
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer victim) {
        killer.getBukkitPlayer().ifPresent(player -> {
            if (killer.getKitAttribute(rainRunnable) != null) {
                ((PoseidonRain) killer.getKitAttribute(rainRunnable)).addTime(rainTime * 1000L);
            } else {
                PoseidonRain poseidonRain = new PoseidonRain(player);
                killer.putKitAttribute(rainRunnable, poseidonRain);
                poseidonRain.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 20);
            }
        });
    }

    private final class PoseidonRain extends BukkitRunnable {
        private final Player player;
        private final BossBar rainBar;
        private long endTime;

        private PoseidonRain(Player player) {
            this.rainBar = Bukkit.createBossBar(t("poseidon.rain", ChatUtils.getPlayerLocale(player)), BarColor.BLUE, BarStyle.SOLID);
            this.player = player;
            this.startRain();
        }

        private void startRain() {
            // PacketPlayOutGameStateChange.c = End rain
            PacketPlayOutGameStateChange rainPacket = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.c, 0);
            NMSUtils.sendPacket(player, rainPacket);
            rainBar.addPlayer(player);
        }

        @Override
        public void run() {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, speedAmplifier));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, regenerationAmplifier));
            rainBar.setProgress((double) System.currentTimeMillis() / endTime);
            if (System.currentTimeMillis() > endTime) {
                stop();
            }
        }

        public void stop() {
            cancel();
            // PacketPlayOutGameStateChange.b = End rain
            PacketPlayOutGameStateChange rainPacket = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.b, 0);
            NMSUtils.sendPacket(player, rainPacket);
            rainBar.removeAll();
        }

        public void addTime(long time) {
            endTime += time;
        }
    }
}

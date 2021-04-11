package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import de.hglabor.utils.noriskutils.NMSUtils;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagInt;
import net.minecraft.server.v1_16_R3.NBTTagString;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
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
        setMainKitItem(createPoseidonKitItem());
        rainTime = 25;
        speedAmplifier = 0;
        regenerationAmplifier = 0;
        rainRunnable = this.getName() + "rainRunnable";
    }

    private ItemStack createPoseidonKitItem() {
        ItemStack poseidon = new ItemBuilder(Material.TRIDENT).setUnbreakable(true).setEnchantment(Enchantment.RIPTIDE, 3).setName("Poseidon").build();
        net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(poseidon);
        NBTTagCompound damage = new NBTTagCompound();
        //TODO doesnt work wanna reduce damage of trident
        damage.set("AttributeName", NBTTagString.a("generic.attackDamage"));
        damage.set("Name", NBTTagString.a("generic.attackDamage"));
        damage.set("Amount", NBTTagInt.a(-8));
        damage.set("Operation", NBTTagInt.a(0));
        damage.set("UUIDLeast", NBTTagInt.a(894654));
        damage.set("UUIDMost", NBTTagInt.a(2872));
        damage.set("Slot", NBTTagString.a("mainhand"));
        itemStack.setTag(damage);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        if (kitPlayer.getKitAttribute(rainRunnable) != null) {
            ((PoseidonRain) kitPlayer.getKitAttribute(rainRunnable)).stop();
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
            this.endTime = System.currentTimeMillis() + rainTime * 1000L;
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2 * 20, speedAmplifier));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20, regenerationAmplifier));
            double progress = (double) (System.currentTimeMillis() / 1000) / (double) (endTime / 1000);
            //TODO always gives 0 or 1 
            System.out.println(progress);
            rainBar.setProgress(Math.min(progress, 1));
            System.out.println(rainBar.getProgress());
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
            KitApi.getInstance().getPlayer(player).putKitAttribute(rainRunnable, null);
        }

        public void addTime(long time) {
            endTime += time;
        }
    }
}

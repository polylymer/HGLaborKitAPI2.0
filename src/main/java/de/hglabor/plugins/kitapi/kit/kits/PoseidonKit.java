package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import de.hglabor.utils.noriskutils.NMSUtils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static de.hglabor.utils.localization.Localization.t;

public class PoseidonKit extends AbstractKit {
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
        net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(new ItemStack(Material.TRIDENT));
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound compound = new NBTTagCompound();
        //Trident Attack Damage should be much lower than default:9 otherwise its too op
        compound.set("AttributeName", NBTTagString.a("generic.attackDamage"));
        compound.set("Name", NBTTagString.a("generic.attackDamage"));
        compound.set("Amount", NBTTagInt.a(2));
        compound.set("Operation", NBTTagInt.a(0));
        compound.set("UUIDLeast", NBTTagInt.a(894654));
        compound.set("UUIDMost", NBTTagInt.a(2872));
        compound.set("Slot", NBTTagString.a("mainhand"));
        modifiers.add(compound);
        compound.set("AttributeModifiers", modifiers);
        itemStack.setTag(compound);
        return new ItemBuilder(CraftItemStack.asBukkitCopy(itemStack).clone()).setUnbreakable(true).setEnchantment(Enchantment.RIPTIDE, 3).setName("Poseidon").build();
    }

    @Override
    public void onDisable(KitPlayer kitPlayer) {
        if (kitPlayer.getKitAttribute(rainRunnable) != null) {
            ((PoseidonRain) kitPlayer.getKitAttribute(rainRunnable)).stop();
        }
    }

    @KitEvent(clazz = PlayerDeathEvent.class)
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer victim) {
        killer.getBukkitPlayer().ifPresent(player -> {
            if (killer.getKitAttribute(rainRunnable) != null) {
                ((PoseidonRain) killer.getKitAttribute(rainRunnable)).addTime(rainTime);
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
        private int timer;

        private PoseidonRain(Player player) {
            this.rainBar = Bukkit.createBossBar(t("poseidon.rain", ChatUtils.locale(player)), BarColor.BLUE, BarStyle.SOLID);
            this.player = player;
            this.endTime = rainTime;
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
            //TODO maybe otherway around
            double progress = (double) timer / (double) endTime;
            rainBar.setProgress(Math.min(progress, 1));
            if (timer > endTime) {
                stop();
            }
            timer++;
        }

        public void stop() {
            cancel();
            // PacketPlayOutGameStateChange.b = End rain
            PacketPlayOutGameStateChange rainPacket = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.b, 0);
            NMSUtils.sendPacket(player, rainPacket);
            rainBar.removeAll();
            KitApi.getInstance().getPlayer(player).putKitAttribute(rainRunnable, null);
        }

        public void addTime(int time) {
            endTime += time;
        }
    }
}

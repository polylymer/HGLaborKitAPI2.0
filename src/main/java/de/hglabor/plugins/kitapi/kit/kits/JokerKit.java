package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.kit.settings.SoundArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class JokerKit extends AbstractKit {
    public final static JokerKit INSTANCE = new JokerKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @SoundArg
    private final Sound shuffleSound;
    @IntArg
    private final int duration;

    private JokerKit() {
        super("Joker", Material.SKELETON_SKULL);
        cooldown = 55F;
        setMainKitItem(getDisplayMaterial());
        shuffleSound = Sound.ENTITY_ARMOR_STAND_FALL;
        duration = 4;
    }

    @KitEvent
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Player rightClicked) {
        if (!rightClicked.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) rightClicked.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, 0));

        JokerInventorySwitch jokerInventorySwitch = new JokerInventorySwitch(rightClicked, duration);
        jokerInventorySwitch.runTaskTimer(KitApi.getInstance().getPlugin(), 0, 2);

        kitPlayer.activateKitCooldown(this);
    }


    private class JokerInventorySwitch extends BukkitRunnable {
        private final long END;
        private final Player player;
        private final Random random;
        private final PlayerInventory playerInventory;
        private final List<Integer> inventorySlots;

        private final List<Material> swords = Arrays.asList(
                Material.WOODEN_SWORD,
                Material.STONE_SWORD,
                Material.IRON_SWORD,
                Material.DIAMOND_SWORD
                // kein netherneite weil zu stark
        );

        private boolean forceEnd;

        public JokerInventorySwitch(Player player, int jokerDuration) {
            this.player = player;
            this.random = new Random();
            playerInventory = player.getInventory();
            this.END = System.currentTimeMillis() + jokerDuration * 1000L;
            this.inventorySlots = new ArrayList<>();
            IntStream.rangeClosed(0, 35).forEach(inventorySlots::add);
        }

        @Override
        public void run() {
            if (forceEnd) {
                return;
            }
            if (System.currentTimeMillis() >= END) {
                cancel();
            } else {
                player.playSound(player.getLocation(), shuffleSound, 0.8F, 0.75F + random.nextFloat() / 2.0F);
                switchItem(playerInventory, inventorySlots.get(random.nextInt(inventorySlots.size() )), inventorySlots.get(random.nextInt(inventorySlots.size())));
            }
        }

        public void end() {
            forceEnd = true;
            cancel();
        }

        private void switchItem(PlayerInventory inventory, int firstSlot, int secondSlot) {
            ItemStack firstItem = getItem(inventory, firstSlot);
            ItemStack secondItem = getItem(inventory, secondSlot);
            if (swords.contains(firstItem.getType()) || swords.contains(secondItem.getType())) return;
            inventory.setItem(firstSlot, secondItem);
            inventory.setItem(secondSlot, firstItem);
        }

        private ItemStack getItem(PlayerInventory inventory, int slot) {
            ItemStack item = inventory.getItem(slot);
            return item == null ? new ItemStack(Material.AIR) : item;
        }

    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

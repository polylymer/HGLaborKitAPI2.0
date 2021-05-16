package de.hglabor.plugins.kitapi.kit.kits;

import com.google.common.collect.ImmutableMap;
import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import de.hglabor.utils.localization.Localization;
import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class MonkKit extends AbstractKit {
    public final static MonkKit INSTANCE = new MonkKit();

    @IntArg
    private final int likelihoodToSwitchArmor;
    @FloatArg(min = 0.0F)
    private final float cooldown;
    private final List<Integer> inventorySlots;
    private final List<EquipmentSlot> armorSlots;
    private final Random random;

    private MonkKit() {
        super("Monk", Material.BLAZE_ROD);
        this.setMainKitItem(getDisplayMaterial());
        this.random = new Random();
        this.likelihoodToSwitchArmor = 25;
        this.cooldown = 13F;
        this.inventorySlots = new ArrayList<>();
        this.armorSlots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.LEGS);
        this.inventorySlots.add(40);
        IntStream.rangeClosed(9, 35).forEach(inventorySlots::add);
    }

    @KitEvent
    @Override
    public void onPlayerRightClickPlayerWithKitItem(PlayerInteractAtEntityEvent event, KitPlayer kitPlayer, Player rightClicked) {
        Player player = event.getPlayer();
        PlayerInventory inventory = rightClicked.getInventory();
        player.sendMessage(Localization.INSTANCE.getMessage("monk.successful", ImmutableMap.of("enemy", rightClicked.getName()), ChatUtils.locale(player)));
        if (ChanceUtils.roll(likelihoodToSwitchArmor)) {
            for (EquipmentSlot armorSlot : armorSlots) {
                if (inventory.getItem(armorSlot) == null) continue;
                rightClicked.sendMessage(Localization.INSTANCE.getMessage("monk.itemWasSwitched", ChatUtils.locale(rightClicked)));
                switchItem(inventory, inventory.getHeldItemSlot(), armorSlot);
                return;
            }
        }
        rightClicked.sendMessage(Localization.INSTANCE.getMessage("monk.itemWasSwitched", ChatUtils.locale(rightClicked)));
        switchItem(inventory, inventory.getHeldItemSlot(), inventorySlots.get(random.nextInt(inventorySlots.size())));
        KitApi.getInstance().getPlayer(player).activateKitCooldown(this);
    }

    private void switchItem(PlayerInventory inventory, int firstSlot, EquipmentSlot secondSlot) {
        ItemStack firstItem = getItem(inventory, firstSlot);
        ItemStack secondItem = getItem(inventory, secondSlot);
        inventory.setItem(firstSlot, secondItem);
        inventory.setItem(secondSlot, firstItem);
    }

    private void switchItem(PlayerInventory inventory, int firstSlot, int secondSlot) {
        ItemStack firstItem = getItem(inventory, firstSlot);
        ItemStack secondItem = getItem(inventory, secondSlot);
        inventory.setItem(firstSlot, secondItem);
        inventory.setItem(secondSlot, firstItem);
    }

    private ItemStack getItem(PlayerInventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);
        return item == null ? new ItemStack(Material.AIR) : item;
    }

    private ItemStack getItem(PlayerInventory inventory, EquipmentSlot slot) {
        ItemStack item = inventory.getItem(slot);
        return item == null ? new ItemStack(Material.AIR) : item;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

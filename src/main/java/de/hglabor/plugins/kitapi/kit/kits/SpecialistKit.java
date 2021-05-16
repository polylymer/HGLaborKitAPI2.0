package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

/**
 * @author Hotkeyyy
 * @since 13.04.2021
 */

public class SpecialistKit extends AbstractKit implements Listener {
    public static final SpecialistKit INSTANCE = new SpecialistKit();
    private static final HashSet<ItemStack> items = new HashSet<>();

    @IntArg(min = 1)
    private final int xpAmountPerKill;


    protected SpecialistKit() {
        super("Specialist", Material.ENCHANTED_BOOK);
        setMainKitItem(new ItemStack(Material.ENCHANTED_BOOK));

        xpAmountPerKill = 3;
    }

    @KitEvent
    @Override
    public void onPlayerRightClickKitItem(PlayerInteractEvent event) {
        openEnchanter(((CraftPlayer) event.getPlayer()).getHandle());
    }

    @KitEvent(clazz = PlayerDeathEvent.class)
    @Override
    public void onPlayerKillsPlayer(KitPlayer killer, KitPlayer dead) {
        killer.getBukkitPlayer().ifPresent(player -> player.giveExp(xpAmountPerKill));
    }


    @KitEvent
    @EventHandler
    public void onEnchant(PrepareItemEnchantEvent e) {
        ItemStack lapis = new ItemStack(Material.LAPIS_LAZULI, 64);
        items.add(lapis);
        e.getInventory().addItem(lapis);
    }


    @KitEvent
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {

        if (e.getInventory() instanceof EnchantingInventory) {
            ItemStack item = e.getInventory().getItem(0);
            e.getInventory().clear();
            if (item == null) return;
            if (e.getPlayer().getInventory().firstEmpty() == -1) {
                e.getPlayer().getLocation().getWorld().dropItem(e.getPlayer().getLocation().add(0, 1, 0), item);
            } else e.getPlayer().getInventory().addItem(item);
        }
    }

    @KitEvent
    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (items.contains(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    private static void openEnchanter(EntityPlayer entPlayer) {
        int nextContainerID = entPlayer.nextContainerCounter();
        Container container = new ContainerEnchantTable(nextContainerID, entPlayer.inventory, ContainerAccess.at(entPlayer.world, new BlockPosition(0, 0, 0)));
        Container cont = CraftEventFactory.callInventoryOpenEvent(entPlayer, container);

        cont.addSlotListener(entPlayer);
        cont.checkReachable = false;

        entPlayer.activeContainer = cont;
        entPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(nextContainerID, Containers.ENCHANTMENT, new ChatComponentText("Enchanting")));

    }
}

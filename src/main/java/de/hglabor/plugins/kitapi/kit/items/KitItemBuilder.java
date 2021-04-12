package de.hglabor.plugins.kitapi.kit.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class KitItemBuilder {
    private final ItemStack item;
    private final ItemMeta itemMeta;

    public KitItemBuilder(Material material) {
        item = new ItemStack(material);
        itemMeta = item.getItemMeta();
    }

    public KitItemBuilder(ItemStack itemStack) {
        item = itemStack;
        itemMeta = item.getItemMeta();
    }

    public KitItemBuilder setName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public KitItemBuilder setPlayerSkull(String name) {
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        return this;
    }

    public KitItemBuilder setTitle(String name) {
        BookMeta bookMeta = (BookMeta) itemMeta;
        bookMeta.setTitle(name);
        return this;
    }

    public KitItemBuilder addPage(String text) {
        BookMeta bookMeta = (BookMeta) itemMeta;
        bookMeta.addPage(text);
        return this;
    }

    public KitItemBuilder setAuthor(String author) {
        BookMeta bookMeta = (BookMeta) itemMeta;
        bookMeta.setAuthor(author);
        return this;
    }

    public KitItemBuilder setPage(int page, String text) {
        BookMeta bookMeta = (BookMeta) itemMeta;
        bookMeta.setPage(page, text);
        return this;
    }

    public KitItemBuilder setDescription(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public KitItemBuilder addLore(String... lore) {
        List<String> text = new ArrayList<>();
        if (itemMeta.getLore() != null) {
            text.addAll(itemMeta.getLore());
        }
        text.addAll(Arrays.asList(lore));
        itemMeta.setLore(text);
        return this;
    }

    public KitItemBuilder setPotionEffect(PotionEffectType potionEffect, Color color) {
        PotionMeta potionMeta = (PotionMeta) itemMeta;
        potionMeta.setColor(color);
        potionMeta.addCustomEffect(new PotionEffect(potionEffect, 0, 0), true);
        return this;
    }

    public KitItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public KitItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public KitItemBuilder setEnchantment(Enchantment enchantment, int lvl) {
        itemMeta.addEnchant(enchantment, lvl, true);
        return this;
    }

    public KitItemBuilder hideItemFlags() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    public KitItemBuilder hideEnchants() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }


    public KitItemBuilder setLocalizedName(String name) {
        itemMeta.setLocalizedName(name);
        return this;
    }

    public ItemStack build() {
        this.addLore(ChatColor.RED + "Kititem");
        item.setItemMeta(itemMeta);
        return item;
    }
}

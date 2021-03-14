package de.hglabor.plugins.kitapi.kit.config;

import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Deprecated
public enum KitSettings {

    COOLDOWN(new ItemBuilder(Material.CLOCK).setName("§eCooldown")
            .setDescription("", "§fDie Dauer wie lange man sein", "§fKit nach der Benutzung nicht verwenden kann").build()),

    RADIUS(new ItemBuilder(Material.LEVER).setName("§eRadius").
            setDescription("", "§fDer Radius von irgendeinem", "§fEffekt des Kits").build()),

    MATERIAL(new ItemBuilder(Material.GRASS_BLOCK).setName("§eMaterial").
            setDescription("", "§fDas Material von irgendeinem", "§fEffekt des Kits").build()),

    HEIGHT(new ItemBuilder(Material.LEVER).setName("§eHeight").
            setDescription("", "§fDer Höhe von irgendeinem", "§fEffekt des Kits").build()),

    NUMBER(new ItemBuilder(Material.BEACON).setName("§eNumber")
            .setDescription("", "§fIrgendein Wert").build()),

    NUMBER2(new ItemBuilder(Material.BEACON).setName("§eNumber2")
            .setDescription("", "§fIrgendein Wert").build()),

    USES(new ItemBuilder(Material.DAMAGED_ANVIL).setName("§eUses")
            .setDescription("", "§fDie Anzahl der möglichen Verwendungen", "§fbis der Cooldown eintritt").build()),

    RANDOM_BOUND_ONE(new ItemBuilder(Material.IRON_BLOCK).setName("§eRandom Bound One")
            .setDescription("", "§fRandom Bound").build()),

    RANDOM_BOUND_TWO(new ItemBuilder(Material.IRON_BLOCK).setName("§eRandom Bound Two")
            .setDescription("", "§fRandom Bound").build()),

    LIKELIHOOD(new ItemBuilder(Material.DAMAGED_ANVIL).setName("§eLikelihood")
            .setDescription("", "§fWahrscheinlichkeit %").build()),

    CLIMBVELOCITY(new ItemBuilder(Material.COBWEB).setName("§eClimb Velocity")
            .setDescription("", "§fWie viel Boost der Spieler", "§fbekommen soll").build()),

    SHOOTINGVELOCITY(new ItemBuilder(Material.COBWEB).setName("§eShoot Velocity")
            .setDescription("", "§fWie viel Boost das Projektil", "§fbekommen soll").build()),

    EFFECT_DURATION(new ItemBuilder(Material.REDSTONE).setName("§eEffektdauer").setDescription("", "§fDie Dauer wie lange ein Effekt anhält")
            .build()),

    EFFECT_MULTIPLIER(new ItemBuilder(Material.GLOWSTONE_DUST).setName("§eEffektmultiplier").setDescription("", "§fDie Stufe des Effekts")
            .build()),

    EXPLOSION_SIZE_PLAYER(new ItemBuilder(Material.TNT).setName("§eExplosionsize Player")
            .setDescription("", "§fDie Größe der Explosion").build()),

    EXPLOSION_SIZE_ENTITY(new ItemBuilder(Material.TNT).setName("§eExplosionsize Entity")
            .setDescription("", "§fDie Größe der Explosion").build()),

    EXPLOSION_SIZE_RECRAFT(new ItemBuilder(Material.TNT).setName("§eExplosionsize Recraft")
            .setDescription("", "§fDie Größe der Explosion").build()),

    NONE(new ItemBuilder(Material.STRUCTURE_VOID).setName("§eNONE").setDescription("", "§fKeine Einstellung").build());

    private final ItemStack itemStack;

    KitSettings(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static KitSettings getSettingByItem(ItemStack itemStack) {
        for (KitSettings settings : values()) {
            if (settings.getItemStack().getType().equals(itemStack.getType()) && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(settings.getItemStack().getItemMeta().getDisplayName())) {
                return settings;
            }
        }
        return NONE;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}

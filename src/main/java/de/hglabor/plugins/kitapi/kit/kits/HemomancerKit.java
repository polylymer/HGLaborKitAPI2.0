package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.DoubleArg;
import de.hglabor.plugins.kitapi.kit.settings.FloatArg;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class HemomancerKit extends AbstractKit {
    public static final HemomancerKit INSTANCE = new HemomancerKit();
    @FloatArg(min = 0.0F)
    private final float cooldown;
    @DoubleArg
    private final double radius;

    private HemomancerKit() {
        super("Rogue", Material.TURTLE_HELMET);
        cooldown = 40F;
        radius = 5D;
        setMainKitItem(getDisplayMaterial());
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }
}

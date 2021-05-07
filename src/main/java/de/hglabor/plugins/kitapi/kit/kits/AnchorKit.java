package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import de.hglabor.plugins.kitapi.kit.events.KitEvent;
import de.hglabor.plugins.kitapi.kit.settings.IntArg;
import de.hglabor.plugins.kitapi.kit.settings.SoundArg;
import de.hglabor.plugins.kitapi.player.KitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class AnchorKit extends AbstractKit {
    public static final AnchorKit INSTANCE = new AnchorKit();
    @SoundArg
    private final Sound hitSound;
    //0 = false, 1 = true (weil boolarg nicht funktioniert)
    @IntArg(min = 0, max = 1)
    private final int setVelocity;

    private AnchorKit() {
        super("Anchor", Material.ANVIL);
        hitSound = Sound.BLOCK_ANVIL_HIT;
        setVelocity = 1;
    }

    @KitEvent
    @Override
    public void onPlayerAttacksLivingEntity(EntityDamageByEntityEvent event, KitPlayer attacker, LivingEntity entity) {
        handleAnchor(event);
    }

    @KitEvent
    @Override
    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
        handleAnchor(event);
    }

    private void handleAnchor(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (setVelocity == 0) {
            event.setCancelled(true);
            entity.damage(event.getFinalDamage());
        } else {
            Bukkit.getScheduler().runTaskLater(KitApi.getInstance().getPlugin(), () -> {
                entity.setVelocity(new Vector(0, 0, 0));
            }, 1L);
        }

    }

}


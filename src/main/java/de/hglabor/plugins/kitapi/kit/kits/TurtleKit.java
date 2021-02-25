package de.hglabor.plugins.kitapi.kit.kits;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;

/**
 * @author Hotkeyyy
 * @since 2021/02/25
 */

public class TurtleKit extends AbstractKit {
    public static final TurtleKit INSTANCE = new TurtleKit();
    protected TurtleKit() {
        super("Turtle", Material.TURTLE_HELMET);
        addEvents(Collections.singletonList(EntityDamageByEntityEvent.class));
    }

    @Override
    public void onPlayerGetsAttackedByLivingEntity(EntityDamageByEntityEvent event, Player player, LivingEntity attacker) {
        if(!KitApi.getInstance().getPlayer(player).hasKit(this)) return;
        if(event.getFinalDamage() > 0.5 && player.isSneaking()){
            event.setDamage(0.5);
        }
    }
}

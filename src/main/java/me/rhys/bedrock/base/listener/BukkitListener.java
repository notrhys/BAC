package me.rhys.bedrock.base.listener;

import me.rhys.bedrock.Bedrock;
import me.rhys.bedrock.base.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Bed;

public class BukkitListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        this.processEvent(event);
    }

    void processEvent(Event event) {
        if (event instanceof EntityDamageByEntityEvent) {
            this.process(event);
        } else {
            Bedrock.getInstance().getExecutorService().execute(() -> this.process(event));
        }
    }

    void process(Event event) {

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
            if (entityDamageByEntityEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    && entityDamageByEntityEvent.getDamager() instanceof Player) {
                User user = Bedrock.getInstance().getUserManager().getUser((Player)
                        entityDamageByEntityEvent.getDamager());

                if (user.cancelAttackTicks > 0) {
                    entityDamageByEntityEvent.setCancelled(true);
                }
            }
        }

        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
            User user = Bedrock.getInstance().getUserManager().getUser(playerInteractEvent.getPlayer());

            if (user != null) {
                if (playerInteractEvent.getItem().getType() == Material.FIREWORK) {
                    user.getElytraProcessor().setFireworkBoost(2.3);
                }
            }
        }
    }
}

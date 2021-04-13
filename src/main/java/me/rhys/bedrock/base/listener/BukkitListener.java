package me.rhys.bedrock.base.listener;

import me.rhys.bedrock.Bedrock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bedrock.getInstance().getUserManager().addUser(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bedrock.getInstance().getUserManager().removeUser(event.getPlayer());
    }
}

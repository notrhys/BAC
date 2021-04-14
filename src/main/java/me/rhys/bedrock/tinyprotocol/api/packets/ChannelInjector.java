/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package me.rhys.bedrock.tinyprotocol.api.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rhys.bedrock.Bedrock;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.channelhandler.ChannelHandler1_7;
import me.rhys.bedrock.tinyprotocol.api.packets.channelhandler.ChannelHandler1_8;
import me.rhys.bedrock.tinyprotocol.api.packets.channelhandler.ChannelHandlerAbstract;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.geysermc.floodgate.FloodgateAPI;

@Getter
public class ChannelInjector implements Listener {
    private final ChannelHandlerAbstract channel;

    public ChannelInjector() {
        this.channel = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8) ? new ChannelHandler1_8() : new ChannelHandler1_7();
    }

    public void addChannel(Player player) {
        if (FloodgateAPI.isBedrockPlayer(player)) {
            Bedrock.getInstance().getUserManager().addUser(player);
            this.channel.addChannel(player);
        }
    }

    public void removeChannel(Player player) {
        Bedrock.getInstance().getUserManager().removeUser(player);
        this.channel.removeChannel(player);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Bedrock.getInstance().getExecutorService().execute(() -> addChannel(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bedrock.getInstance().getExecutorService().execute(() ->
                removeChannel(event.getPlayer()));
    }

    @Getter
    @AllArgsConstructor
    public static class Data {
        private final Player player;
        private final long time;
        private final int max;
    }
}

package me.rhys.bedrock.base.listener;

import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import me.rhys.bedrock.Bedrock;
import me.rhys.bedrock.base.packet.PacketHandler;
import me.rhys.bedrock.base.user.User;

public class PacketListener implements AtlasListener {

    @Listen
    public void onServerPacket(PacketSendEvent event) {
        User user = Bedrock.getInstance().getUserManager().getUser(event.getPlayer());
        if (user != null) {
            user.getPacketHandler().handlePacket(PacketHandler.Type.SERVER, event.getPacket(), event.getType());
        }
    }

    @Listen
    public void onClientPacket(PacketReceiveEvent event) {
        User user = Bedrock.getInstance().getUserManager().getUser(event.getPlayer());
        if (user != null) {
            user.getPacketHandler().handlePacket(PacketHandler.Type.CLIENT, event.getPacket(), event.getType());
        }
    }
}

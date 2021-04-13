package me.rhys.bedrock.base.processor;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import lombok.Getter;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.util.EvictingMap;

import java.util.Map;

@Getter
public class ConnectionProcessor {
    private final User user;

    public ConnectionProcessor(User user) {
        this.user = user;
    }

    private final Map<Long, Long> sentKeepAlives = new EvictingMap<>(100);
    private int ping;
    private int clientTick;

    public void handle(String type, Object packet) {
        if (type.equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
            WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(packet,
                    this.user.getPlayer());

            if (this.user.getConnectionMap().containsKey(wrappedInKeepAlivePacket.getTime())) {
                this.ping = (int) (System.currentTimeMillis() - this.user.getConnectionMap()
                        .get(wrappedInKeepAlivePacket.getTime()));
                this.sentKeepAlives.put(wrappedInKeepAlivePacket.getTime(), System.currentTimeMillis());
                this.clientTick = (int) Math.ceil(this.ping / 50.0);
            }
        }
    }
}

package me.rhys.bedrock.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.processor.api.Processor;
import me.rhys.bedrock.base.processor.api.ProcessorInformation;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.rhys.bedrock.util.EvictingMap;

import java.util.Map;

@ProcessorInformation(name = "Connection")
@Getter @Setter
public class ConnectionProcessor extends Processor {

    private final Map<Long, Long> sentKeepAlives = new EvictingMap<>(100);
    private int ping;
    private int clientTick;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
            WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(event.getPacket(),
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

package me.rhys.bedrock.base.packet;

import lombok.AllArgsConstructor;
import me.rhys.bedrock.base.user.User;

@AllArgsConstructor
public class PacketHandler {
    private final User user;

    public void handlePacket(Type type, Object packet, String packetType) {
        this.user.getExecutorService().execute(() -> {

            switch (type) {
                case SERVER:
                case CLIENT: {
                    this.user.getConnectionProcessor().handle(packetType, packet);
                    this.user.getMovementProcessor().handle(packetType, packet);
                    this.user.getEventManager().processChecks(packetType, packet);
                    break;
                }
            }
        });
    }

    public enum Type {
        CLIENT,
        SERVER
    }
}

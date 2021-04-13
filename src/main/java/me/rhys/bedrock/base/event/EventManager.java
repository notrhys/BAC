package me.rhys.bedrock.base.event;

import lombok.AllArgsConstructor;
import me.rhys.bedrock.base.user.User;

@AllArgsConstructor
public class EventManager {
    private final User user;

    public void processChecks(String packetType, Object object) {
        PacketEvent packetEvent = new PacketEvent(this.user, object, packetType, System.currentTimeMillis());
        this.user.getCheckManager().getCheckList().forEach(check ->
                check.onPacket(packetEvent));
    }
}

package me.rhys.bedrock.checks.badpackets;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;

@CheckInformation(checkName = "BadPackets", punishmentVL = 1)
public class BadPacketsA extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();
                double pitch = Math.abs(user.getCurrentLocation().getPitch());

                if (pitch > 90) {
                    this.flag(user,
                            "pitch: " + pitch
                    );
                }
            }
        }
    }
}

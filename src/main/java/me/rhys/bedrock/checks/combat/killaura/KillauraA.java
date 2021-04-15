package me.rhys.bedrock.checks.combat.killaura;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;

@CheckInformation(checkName = "KillAura", description = "Checks if the player attacks on post")
public class KillauraA extends Check {

    private long lastPosition;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();
        switch (event.getType()) {
            case Packet.Client.POSITION_LOOK: {
                this.lastPosition = System.currentTimeMillis();
                this.threshold -= (this.threshold > 0 ? .09 : 0);
                break;
            }

            case Packet.Client.USE_ENTITY: {
                long delta = (System.currentTimeMillis() - this.lastPosition);

                if (delta < 15L) {
                    if ((this.threshold += 0.95) > 3.5) {
                        this.flag(user,
                                "threshold: " + this.threshold,
                                "time: " + delta
                        );
                    }
                } else {
                    this.threshold -= (this.threshold > 0 ? 0.50 : 0);
                }
                break;
            }
        }
    }
}

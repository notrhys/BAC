package me.rhys.bedrock.checks.movement.nofall;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;

@CheckInformation(checkName = "NoFall", description = "Checks if the player tries to spoof on ground.")
public class NoFallA extends Check {

    /*
        Very strange check..
     */

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (this.checkConditions(user)) {
                    this.threshold = 0;
                    return;
                }

                if (user.getMovementProcessor().getServerAirTicks() > 15
                        && !user.getBlockData().onGround && !user.getBlockData().onGround) {
                    double deltaY = Math.abs(user.getMovementProcessor().getDeltaY());

                    if (deltaY > 0 && !user.getMovementProcessor().isOnGround()) {
                        if ((this.threshold += .75) > 16) {
                            this.flag(user,
                                    "threshold=" + this.threshold,
                                    "deltaY=" + deltaY,
                                    "sat=" + user.getMovementProcessor().getServerAirTicks()
                            );
                        }
                    }
                } else {
                    threshold = 0;
                }
                break;
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 60
                || user.shouldCancel();
    }
}

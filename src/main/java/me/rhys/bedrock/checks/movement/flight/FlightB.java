package me.rhys.bedrock.checks.movement.flight;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "B", description = "Basic float check.")
public class FlightB extends Check {

    private double lastDelta;
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

                boolean onGround = user.getBlockData().onGround;
                boolean lastOnGround = user.getBlockData().lastOnGround;

                if (!onGround && !lastOnGround) {
                    double deltaY = Math.abs(user.getMovementProcessor().getDeltaY() - this.lastDelta);

                    if (deltaY < 3E-50) {
                        if (this.threshold++ > 4) {
                            this.flag(user,
                                    "deltaY=" + deltaY,
                                    "threshold=" + this.threshold
                            );
                        }
                    }
                } else {
                    this.threshold = (this.threshold > 0 ? Math.min(threshold - .75, 0) : 0);
                }

                this.lastDelta = user.getMovementProcessor().getDeltaY();
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 60
                || user.shouldCancel()
                || user.getMovementProcessor().getLastBlockPlacePacketTimer().hasNotPassed()
                || user.getBlockData().climbableTicks > 0 || user.getBlockData().bedTicks > 0;
    }
}

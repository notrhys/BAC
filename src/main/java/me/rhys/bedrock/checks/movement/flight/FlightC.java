package me.rhys.bedrock.checks.movement.flight;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "C", lagBack = false, canPunish = false)
public class FlightC extends Check {

    private int threshold;
    private double airTick;

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
                    this.airTick = 0;
                    return;
                }

                if (!user.getMovementProcessor().isOnGround() && !user.getMovementProcessor().isLastGround()) {
                    double deltaY = user.getMovementProcessor().getDeltaY();

                    if (deltaY > -0.009) {
                        if ((this.airTick += 0.50) > 6 && this.threshold++ > 15) {
                            this.flag(user,
                                    "air: " + this.airTick,
                                    "deltaY: " + deltaY,
                                    "threshold: " + this.threshold
                            );
                        }
                    } else {
                        this.airTick -= (this.airTick > 0 ? 0.0095 : 0);
                    }
                } else {
                    if (user.getBlockData().onGround) {
                        this.threshold = 0;
                        this.airTick = 0;
                    }
                }
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().slimeTicks > 0
                || user.getBlockData().climbableTicks > 0
                || user.shouldCancel()
                || user.getBlockData().liquidTicks > 0;
    }
}

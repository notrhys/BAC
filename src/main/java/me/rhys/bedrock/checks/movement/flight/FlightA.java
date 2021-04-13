package me.rhys.bedrock.checks.movement.flight;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;

@CheckInformation(checkName = "Flight", description = "Basic flight check.")
public class FlightA extends Check {

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
                    this.lastDelta = 0;
                    return;
                }

                boolean onGround = user.getMovementProcessor().isOnGround();
                boolean lastOnGround = user.getMovementProcessor().isLastGround();

                if (!onGround && !lastOnGround) {
                    double predict = this.lastDelta - 0.08 * 0.98;
                    double prediction = Math.abs(user.getMovementProcessor().getDeltaY() - predict);

                    if (predict > 0.003 && prediction > 0.009) {
                        if ((this.threshold += 1.5) > 7.5) {
                            this.flag(user,
                                    "threshold: " + this.threshold,
                                    "p: " + predict,
                                    "pd: " + prediction
                            );
                        }
                    } else {
                        this.threshold = (this.threshold > 0 ? Math.min(threshold - .50, 0) : 0);
                    }
                } else {
                    this.threshold = (this.threshold > 0 ? Math.min(threshold - .20, 0) : 0);
                }

                this.lastDelta = user.getMovementProcessor().getDeltaY();
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 60
                || user.shouldCancel();
    }
}

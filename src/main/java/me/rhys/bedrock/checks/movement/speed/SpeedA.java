package me.rhys.bedrock.checks.movement.speed;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Speed", description = "Basic friction check.")
public class SpeedA extends Check {

    private double lastDeltaXZ;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                double deltaX = user.getMovementProcessor().getDeltaX();
                double deltaZ = user.getMovementProcessor().getDeltaZ();
                double deltaXZ = (deltaX * deltaX) + (deltaZ * deltaZ);

                if (!this.checkConditions(user) && !user.getMovementProcessor().isOnGround()
                        && !user.getMovementProcessor().isLastGround()) {
                    double friction = (deltaXZ - this.lastDeltaXZ * .91f) * 138;

                    if (friction > this.getBestFriction(user)) {
                        this.flag(user,
                               "friction=" + friction
                        );
                    }
                }

                this.lastDeltaXZ = deltaXZ;
            }
        }
    }

    double getBestFriction(User user) {
        return (user.getActionProcessor().getServerPositionTimer().hasNotPassed()
                ? 3 : (user.getBlockData().iceTicks > 0 ? 13 : 1.08));
    }

    boolean checkConditions(User user) {
        return user.getTick() < 60
                || user.getBlockData().piston
                || user.shouldCancel()
                || user.getBlockData().fenceTicks > 0
                || user.getBlockData().snowTicks > 0
                || user.getBlockData().liquidTicks > 0
                || user.getActionProcessor().getServerPositionTimer().hasNotPassed()
                || user.getActionProcessor().getVelocityTimer().hasNotPassed();
    }
}

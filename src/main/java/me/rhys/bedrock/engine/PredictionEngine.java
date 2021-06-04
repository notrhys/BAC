package me.rhys.bedrock.engine;

import lombok.Getter;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.util.MathUtil;

@Getter
public class PredictionEngine {
    private final User user;

    private double lastMotionX, lastMotionZ, lastMotionY, currentMotionX, currentMotionZ, currentMotionY;
    private KeyboardKey keyboardKey = KeyboardKey.NONE;
    private double predX, predZ, lastPredicted, lastDelta, fixedXZ;

    public PredictionEngine(User user) {
        this.user = user;
    }

    public void processPacket(PacketEvent event) {

        this.currentMotionX = (user.getCurrentLocation().getX() - user.getLastLocation().getX());
        this.currentMotionY = (user.getCurrentLocation().getY() - user.getLastLocation().getY());
        this.currentMotionZ = (user.getCurrentLocation().getZ() - user.getLastLocation().getZ());

        if (!(event.getType().equalsIgnoreCase(Packet.Client.FLYING)
                && !event.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK))
                && Math.abs(user.getCurrentLocation().getYaw()) > 0.0054) {
            this.processMove();
        }

        double friction = 0.9100000262260437D;
        this.currentMotionX *= friction;
        this.currentMotionZ *= friction;

        if (user.getMovementProcessor().isOnGround()) {
            double add = 0.60000005239967D;
            this.currentMotionX *= add;
            this.currentMotionZ *= add;
        }

        if (Math.abs(this.currentMotionX) < 0.005D) {
            this.currentMotionX = 0.0D;
        }

        if (Math.abs(this.currentMotionY) < 0.005D) {
            this.currentMotionY = 0.0D;
        }

        if (Math.abs(this.currentMotionZ) < 0.005D) {
            this.currentMotionZ = 0.0D;
        }

        this.lastMotionX = this.currentMotionX;
        this.lastMotionY = this.currentMotionY;
        this.lastMotionZ = this.currentMotionZ;
    }

    void processMove() {
        double deltaX = this.currentMotionX - this.lastMotionX;
        double deltaZ = this.currentMotionZ - this.lastMotionZ;
        double absX = Math.abs(deltaX);
        double absZ = Math.abs(deltaZ);

        float motionYaw = ((float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F)
                - user.getCurrentLocation().getYaw();
        int yawDirection;

        while (motionYaw > 360.0F) {
            motionYaw -= 360.0F;
        }

        while (motionYaw < 0.0F) {
            motionYaw += 360.0F;
        }

        motionYaw /= 45.0F;

        if (Math.abs((absX + absZ)) > 1E-5) {
            yawDirection = MathUtil.roundHalfUp(1, motionYaw);

            switch (yawDirection) {
                case 1: {
                    keyboardKey = KeyboardKey.WD;
                    break;
                }

                case 2: {
                    keyboardKey = KeyboardKey.D;
                    break;
                }

                case 3: {
                    keyboardKey = KeyboardKey.SD;
                    break;
                }

                case 4: {
                    keyboardKey = KeyboardKey.S;
                    break;
                }

                case 5: {
                    keyboardKey = KeyboardKey.SA;
                    break;
                }

                case 6: {
                    keyboardKey = KeyboardKey.A;
                    break;
                }

                case 7: {
                    keyboardKey = KeyboardKey.WA;
                    break;
                }

                case 8:
                case 0: {
                    keyboardKey = KeyboardKey.W;
                    break;
                }
            }

            double absMotX = Math.abs(this.currentMotionX);
            double absMotZ = Math.abs(this.currentMotionZ);

            double x = this.predX * .6F;
            double z = this.predZ * .6F;
            double predDeltaX = Math.abs(x - absMotX);
            double predDeltaZ = Math.abs(z - absMotZ);
            double motionXZ = predDeltaX + predDeltaZ;
            double delta = Math.abs(motionXZ - this.lastPredicted);
            this.fixedXZ =Math.abs(delta - this.lastDelta);
            this.lastDelta = delta;
            this.lastPredicted = motionXZ;
            this.predX = absMotX;
            this.predZ = absMotZ;
        }
    }

        public enum KeyboardKey {
        WD,
        D,
        SD,
        S,
        SA,
        A,
        WA,
        W,
        NONE
    }
}

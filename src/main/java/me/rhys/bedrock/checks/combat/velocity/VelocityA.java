package me.rhys.bedrock.checks.combat.velocity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import me.rhys.bedrock.util.evicting.EvictingList;

import java.util.List;

@CheckInformation(checkName = "Velocity", lagBack = false, description = "Simple XZ based Velocity check")
public class VelocityA extends Check {

    private final List<VelocityEntry> velocityEntries = new EvictingList<>(20);
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Server.ENTITY_VELOCITY: {
                WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(event.getPacket(),
                        event.getUser().getPlayer());

                if (wrappedOutVelocityPacket.getId() == user.getPlayer().getEntityId()) {
                    this.velocityEntries.add(new VelocityEntry(
                            wrappedOutVelocityPacket.getX(),
                            wrappedOutVelocityPacket.getY(),
                            wrappedOutVelocityPacket.getZ(),
                            user.getMovementProcessor().getDeltaY(),
                            user.getTick()
                    ));
                }
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (this.checkConditions(user)) {
                    this.velocityEntries.clear();
                    return;
                }

                if (this.velocityEntries.size() > 0) {
                    this.velocityEntries.forEach(velocityEntry -> {
                        if (velocityEntry.tick++ > 5) {

                            double x = velocityEntry.getX();
                            double z = velocityEntry.getZ();
                            double deltaX = Math.abs(Math.abs(user.getCurrentLocation().getX()
                                    - user.getLastLocation().getX()) - x);
                            double deltaZ = Math.abs(Math.abs(user.getCurrentLocation().getZ()
                                    - user.getLastLocation().getZ()) - z);

                            double ratioX = Math.abs((deltaX / x) * 100);
                            double ratioZ = Math.abs((deltaZ / z) * 100);

                            if (ratioX == 100 || ratioZ == 100) {
                                if ((this.threshold += 0.85) > 4) {
                                    this.flag(user,
                                            "ratioX: " + ratioX,
                                            "ratioZ: " + ratioZ,
                                            "deltaX: " + deltaX,
                                            "deltaZ: " + deltaZ,
                                            "tick: " + velocityEntry.getTick(),
                                            "threshold: " + this.threshold
                                    );
                                }
                            } else {
                                this.threshold -= (this.threshold > 0 ? .1 : 0);
                            }

                            this.velocityEntries.remove(velocityEntry);
                        }
                    });
                }
                break;
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0 || user.getBlockData().climbableTicks > 0;
    }


    @Getter @AllArgsConstructor
    public static class VelocityEntry {
        private final double x, y, z;
        private final double deltaY;
        public int tick;
    }
}

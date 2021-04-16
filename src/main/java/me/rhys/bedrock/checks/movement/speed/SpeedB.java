package me.rhys.bedrock.checks.movement.speed;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.util.BlockUtil;
import me.rhys.bedrock.util.EventTimer;
import org.bukkit.Location;
import org.bukkit.Material;

@CheckInformation(checkName = "Speed", checkType = "B", description = "Basic limit check.")
public class SpeedB extends Check {

    private EventTimer lastJumpTimer, blockLevelChangeTimer;
    private double groundThreshold, airThreshold;
    private int lastLevelY;

    @Override
    public void onConnection(User user) {
        //because flying packet sometimes is delayed on Geyser
        this.processLevelChange(user);
    }

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (!this.checkConditions(user)) {
                    double deltaXZ = user.getMovementProcessor().getDeltaXZ();
                    this.processDeltaY(user);

                    Tags tag = this.findTag(user);

                    switch (tag) {
                        case GROUND: {
                            boolean expand = this.lastJumpTimer.hasNotPassed()
                                    || this.blockLevelChangeTimer.hasNotPassed();

                            //Not the best but will do the job for now.
                            double max = (expand ? .6325 : .3095);

                            if (user.getPotionProcessor().getSpeedTicks() > 0) {
                                max += (user.getPotionProcessor().getSpeedAmplifier() * 0.060);
                            } else if (user.getElytraProcessor().isUsingElytra()) {
                                max += user.getElytraProcessor().getFireworkBoost();
                            }

                            if (deltaXZ > max) {
                                if ((this.groundThreshold += 1.2) > 5.5) {
                                    this.flag(user,
                                            "tag: " + tag.name(),
                                            "speed: " + deltaXZ,
                                            "max: " + max,
                                            "threshold: " + this.groundThreshold,
                                            "expand: " + expand
                                    );
                                }
                            } else {
                                this.groundThreshold -= (this.groundThreshold > 0 ? 1 : 0);
                            }
                            break;
                        }

                        case AIR: {
                            if (user.getMovementProcessor().getServerGroundTicks() == 0) {
                                double max = (user.getPotionProcessor().getSpeedTicks() > 0
                                        ? .3655 + (user.getPotionProcessor().getSpeedAmplifier() * .030) : .3655);

                                if (user.getBlockData().iceTicks > 0) {
                                    max += .1922;
                                } else if (this.blockLevelChangeTimer.hasNotPassed()) {
                                    max += .2;
                                } else if (user.getElytraProcessor().isUsingElytra()) {
                                    max += user.getElytraProcessor().getFireworkBoost();
                                }

                                if (deltaXZ > max && (this.airThreshold += 1.1) > (user.getActionProcessor()
                                        .getVelocityTimer().hasNotPassed(60) ? 3.4 : 2.94)) {
                                    this.flag(user,
                                            "tag: " + tag.name(),
                                            "speed: " + deltaXZ,
                                            "max: " + max,
                                            "threshold: " + this.airThreshold,
                                            "at: " + user.getMovementProcessor().getAirTicks()
                                    );
                                }
                            } else {
                                this.airThreshold -= (this.airThreshold > 0 ? .6 : 0);
                            }
                            break;
                        }
                    }
                } else {
                    this.airThreshold = -1;
                    this.groundThreshold = -1;
                }
            }
        }
    }

    boolean checkConditions(User user) {
        return user.shouldCancel() || user.getBlockData().slabTicks > 0
                || user.getBlockData().stairTicks > 0
                || user.getActionProcessor().getServerPositionTimer().hasNotPassed()
                || user.getCombatProcessor().getPreVelocityTimer().hasNotPassed()
                || user.getBlockData().underBlockTicks > 0
                || user.getBlockData().blockAboveTimer.hasNotPassed()
                || user.getBlockData().iceTimer.hasNotPassed()
                || user.getBlockData().snowTicks > 0
                || user.getBlockData().slimeTicks > 0
                || user.getBlockData().slimeTimer.hasNotPassed()
                || user.getBlockData().stairSlabTimer.hasNotPassed()
                || user.getBlockData().liquidTicks > 0
                || user.getBlockData().climbableTicks > 0
                || user.getTick() < 120;
    }

    void processDeltaY(User user) {
        if (!user.getMovementProcessor().isOnGround()
                && user.getMovementProcessor().isLastGround()) {
            this.lastJumpTimer.reset();
        }
    }

    void processLevelChange(User user) {
        if (user.getCurrentLocation() != null && user.getTick() > 60) {
            Location location = user.getCurrentLocation().toBukkitLocation(user.getPlayer().getWorld());
            if (BlockUtil.getBlock(location.clone().add(0, -1, 0)).getType() != Material.AIR) {
                int blockY = location.getBlockY();

                if (Math.abs(this.lastLevelY - blockY) > 0) {
                    this.blockLevelChangeTimer.reset();
                }

                if (user.getBlockData().onGround) {
                    this.lastLevelY = blockY;
                }
            }
        }
    }

    Tags findTag(User user) {
        return (user.getBlockData().onGround ? Tags.GROUND : Tags.AIR);
    }

    @Override
    public void setupTimers(User user) {
        this.lastJumpTimer = new EventTimer(20, user);
        this.blockLevelChangeTimer = new EventTimer(20, user);
    }

    public enum Tags {
        GROUND,
        AIR
    }
}

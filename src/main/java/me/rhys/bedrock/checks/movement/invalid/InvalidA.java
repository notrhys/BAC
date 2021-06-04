package me.rhys.bedrock.checks.movement.invalid;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.util.BlockUtil;
import me.rhys.bedrock.util.EventTimer;
import org.bukkit.Location;
import org.bukkit.Material;

@CheckInformation(checkName = "Invalid", description = "Bootleg prediction check.", canPunish = false, lagBack = false)
public class InvalidA extends Check {

    private double threshold;

    private EventTimer blockLevelChangeTimer;
    private int lastLevelY;

    @Override
    public void setupTimers(User user) {
        this.blockLevelChangeTimer = new EventTimer(20, user);
    }

    @Override
    public void onConnection(User user) {
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
                double xz = user.getPredictionEngine().getFixedXZ();
                boolean valid = xz > 0 && user.getMovementProcessor().isOnGround() ? xz < .255 : xz < .5;

                if (this.checkConditions(user)) {
                    this.threshold = 0;
                    return;
                }

                if (valid && xz > (user.getMovementProcessor().getDeltaY() > 0 ? .24345 : .0938453)) {
                    if (this.threshold > (user.getMovementProcessor().isOnGround() ? 3.5 : 5.4)) {
                        this.flag(user,
                                "threshold=" + this.threshold,
                                "xz=" + xz,
                                "key(s)=" + user.getPredictionEngine().getKeyboardKey().name(),
                                "ground=" + user.getMovementProcessor().isOnGround()
                        );
                    }

                    this.threshold += this.threshold < 20 ? .50 : 0;
                } else {
                    this.threshold -= this.threshold > 0 ? 1 : 0;
                }
                break;
            }
        }
    }

    boolean checkConditions(User user) {
        return this.blockLevelChangeTimer.hasNotPassed(100)
                || user.getTick() < 500
                || user.getBlockData().liquidTicks > 0
                || user.getBlockData().slimeTicks > 0
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().shulkerTicks > 0
                || user.getBlockData().stairTicks > 0
                || user.getBlockData().slabTicks > 0
                || user.getBlockData().underBlockTicks > 0
                || user.shouldCancel();
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
}

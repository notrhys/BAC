package me.rhys.bedrock.base.processor;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import lombok.Getter;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.util.EventTimer;
import me.rhys.bedrock.util.PlayerLocation;
import me.rhys.bedrock.util.block.BlockChecker;
import me.rhys.bedrock.util.block.BlockEntry;
import org.bukkit.World;
import org.bukkit.block.Block;

@Getter
public class MovementProcessor {
    private final User user;
    private EventTimer lastGroundTimer;

    public MovementProcessor(User user) {
        this.user = user;
        this.setupTimers();
    }

    private boolean onGround, lastGround, positionYGround, lastPositionYGround;
    private int groundTicks, airTicks;
    private double deltaY;

    public void handle(String type, Object packet) {
        switch (type) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(packet, this.user.getPlayer());

                double x = wrappedInFlyingPacket.getX();
                double y = wrappedInFlyingPacket.getY();
                double z = wrappedInFlyingPacket.getZ();
                float yaw = wrappedInFlyingPacket.getYaw();
                float pitch = wrappedInFlyingPacket.getPitch();
                boolean ground = wrappedInFlyingPacket.isGround();

                if (wrappedInFlyingPacket.isPos()) {
                    this.deltaY = (user.getCurrentLocation().getY() - user.getLastLocation().getY());

                    user.setLastLocation(user.getCurrentLocation());
                    user.setCurrentLocation(new PlayerLocation(user.getPlayer().getWorld(), x, y, z,
                            yaw, pitch, ground));

                    this.lastPositionYGround = this.positionYGround;
                    this.positionYGround = y % 0.015625 < 0.009;

                    this.lastGround = this.onGround;
                    this.onGround = ground;

                    if (ground) {
                        this.lastGroundTimer.reset();
                        this.airTicks = 0;
                        if (this.groundTicks < 20) this.groundTicks++;
                    } else {
                        this.groundTicks = 0;
                        if (this.airTicks < 20) this.airTicks++;
                    }
                }

                this.processBlocks();
                this.user.setTick(this.user.getTick() + 1);
                break;
            }
        }
    }

    void processBlocks() {
        boolean badVector = Math.abs(user.getCurrentLocation().toVector().length()
                - user.getLastLocation().toVector().length()) >= 1;

        user.setBoundingBox(new BoundingBox((badVector ? user.getCurrentLocation().toVector()
                : user.getLastLocation().toVector()), user.getCurrentLocation().toVector())
                .grow(0.3f, 0, 0.3f).add(0, 0, 0, 0, 1.84f, 0));

        World world = user.getPlayer().getWorld();
        BlockChecker blockChecker = new BlockChecker(this.user);

        Atlas.getInstance().getBlockBoxManager().getBlockBox()
                .getCollidingBoxes(world, user.getBoundingBox()
                        .grow(0.35f, 0.3f, 0.35f)).parallelStream().forEach(boundingBox -> {
            Block block = BlockUtils.getBlock(boundingBox.getMinimum().toLocation(world));

            if (block != null) {
                blockChecker.check(new BlockEntry(block, boundingBox));
            }
        });

        this.cacheInformation(blockChecker);
    }

    void cacheInformation(BlockChecker blockChecker) {
        user.getBlockData().lastOnGround = user.getBlockData().onGround;
        user.getBlockData().onGround = blockChecker.isOnGround();
        user.getBlockData().nearLiquid = blockChecker.isNearLiquid();
        this.updateTicks();
    }

    void updateTicks() {
        if (user.getBlockData().nearLiquid) {
            user.getBlockData().liquidTicks += (user.getBlockData().liquidTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().liquidTicks -= (user.getBlockData().liquidTicks > 0 ? 1 : 0);
        }
    }

    void setupTimers() {
        this.lastGroundTimer = new EventTimer(20, this.user);
    }
}

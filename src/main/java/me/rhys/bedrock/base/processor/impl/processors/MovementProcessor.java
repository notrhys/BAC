package me.rhys.bedrock.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.Bedrock;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.processor.api.Processor;
import me.rhys.bedrock.base.processor.api.ProcessorInformation;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.rhys.bedrock.util.BlockUtil;
import me.rhys.bedrock.util.EventTimer;
import me.rhys.bedrock.util.MathUtil;
import me.rhys.bedrock.util.PlayerLocation;
import me.rhys.bedrock.util.block.BlockChecker;
import me.rhys.bedrock.util.block.BlockEntry;
import me.rhys.bedrock.util.box.BoundingBox;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent;

@ProcessorInformation(name = "Movement")
@Getter @Setter
public class MovementProcessor extends Processor {
    private EventTimer lastGroundTimer;
    private EventTimer lastBlockPlacePacketTimer;

    private boolean onGround, lastGround, positionYGround, lastPositionYGround, bouncedOnSlime;
    private int groundTicks, airTicks, lagBackTicks, serverAirTicks, serverGroundTicks;
    private double deltaY, deltaXZ, deltaX, deltaZ;
    private PlayerLocation lastSlimeLocation;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Server.POSITION: {
                user.getActionProcessor().add(ActionProcessor.Actions.SERVER_POSITION);
                break;
            }

            case Packet.Client.BLOCK_PLACE: {
                this.lastBlockPlacePacketTimer.reset();
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(event.getPacket(),
                        this.user.getPlayer());

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

                this.deltaX = Math.abs(Math.abs(user.getCurrentLocation().getX())
                        - Math.abs(user.getLastLocation().getX()));
                this.deltaZ = Math.abs(Math.abs(user.getCurrentLocation().getZ())
                        - Math.abs(user.getLastLocation().getZ()));
                this.deltaXZ = Math.hypot(this.deltaX, this.deltaZ);

                this.processBlocks();
                this.user.setTick(this.user.getTick() + 1);

                if (this.lagBackTicks-- > 0 && user.getTick() % 5 == 0) {
                    user.getPlayer().teleport(MathUtil.getGroundLocation(user),
                            PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
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

        Bedrock.getInstance().getBlockBoxManager().getBlockBox()
                .getCollidingBoxes(world, user.getBoundingBox()
                        .grow(0.35f, 0.3f, 0.35f)).parallelStream().forEach(boundingBox -> {
            Block block = BlockUtil.getBlock(boundingBox.getMinimum().toLocation(world));

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
        user.getBlockData().climbable = blockChecker.isClimbable();
        user.getBlockData().nearIce = blockChecker.isNearIce();
        user.getBlockData().slime = blockChecker.isSlime();
        user.getBlockData().piston = blockChecker.isPiston();
        user.getBlockData().snow = blockChecker.isSnow();
        user.getBlockData().fence = blockChecker.isFence();

        if (user.getBlockData().onGround) {
            if (this.serverGroundTicks < 20) this.serverGroundTicks++;
            this.serverAirTicks = 0;
        } else {
            this.serverGroundTicks = 0;
            if (this.serverAirTicks < 20) this.serverAirTicks++;
        }

        if (this.isOnGround() && user.getBlockData().slime) {
            this.lastSlimeLocation = user.getCurrentLocation().clone();
            this.bouncedOnSlime = true;
        }

        if (this.bouncedOnSlime) {
            if (this.isOnGround() && this.isLastGround() && user.getBlockData().slimeTicks < 1) {
                this.bouncedOnSlime = false;
            }

            if (this.lastSlimeLocation.distanceSquaredXZ(user.getCurrentLocation()) > 70) {
                this.bouncedOnSlime = false;
            }
        }

        this.updateTicks();
    }

    void updateTicks() {
        if (user.getBlockData().fence) {
            user.getBlockData().fenceTicks += (user.getBlockData().fenceTicks > 0 ? 1 : 0);
        } else {
            user.getBlockData().fenceTicks -= (user.getBlockData().fenceTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().snow) {
            user.getBlockData().snowTicks += (user.getBlockData().snowTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().snowTicks -= (user.getBlockData().snowTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().slime) {
            user.getBlockData().slimeTicks += (user.getBlockData().slimeTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().slimeTicks -= (user.getBlockData().slimeTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().nearIce) {
            user.getBlockData().iceTicks += (user.getBlockData().iceTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().iceTicks -= (user.getBlockData().iceTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().climbable) {
            user.getBlockData().climbableTicks += (user.getBlockData().climbableTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().climbableTicks -= (user.getBlockData().climbableTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().nearLiquid) {
            user.getBlockData().liquidTicks += (user.getBlockData().liquidTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().liquidTicks -= (user.getBlockData().liquidTicks > 0 ? 1 : 0);
        }
    }

    @Override
    public void setupTimers(User user) {
        this.lastGroundTimer = new EventTimer(20, user);
        this.lastBlockPlacePacketTimer = new EventTimer(20, user);
        this.lastSlimeLocation = new PlayerLocation(user.getPlayer().getWorld(), 0, 0, 0, 0,
                0, false);
    }
}

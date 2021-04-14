package me.rhys.bedrock.util.block;

import lombok.Getter;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.util.box.BoundingBox;
import org.bukkit.block.Block;

@Getter
public class BlockChecker {
    private final BoundingBox boundingBox;
    private final User user;

    public BlockChecker(User user) {
        this.boundingBox = user.getBoundingBox();
        this.user = user;
    }

    private boolean onGround, nearLiquid, nearIce, climbable, slime, piston, snow, fence;

    public void check(BlockEntry blockEntry) {
        Block block = blockEntry.getBlock();
        BoundingBox checkedBox = blockEntry.getBoundingBox();

        if (checkedBox.collidesVertically(this.boundingBox
                .subtract(0, 0.1f, 0, 0, 0, 0))) {
            this.onGround = true;
        }

        boolean checkMovingUp = false;

        switch (block.getType()) {
            case WATER:
            case STATIONARY_WATER:
            case STATIONARY_LAVA:
            case LAVA: {
                this.nearLiquid = true;
                break;
            }

            case ICE:
            case PACKED_ICE: {
                this.nearIce = true;
                break;
            }

            case LADDER:
            case VINE: {
                this.climbable = true;
                break;
            }

            case SLIME_BLOCK: {
                this.slime = true;
                break;
            }

            case PISTON_STICKY_BASE:
            case PISTON_BASE:
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE: {
                this.piston = true;
                checkMovingUp = true;
                break;
            }

            case SNOW: {
                this.snow = true;
                break;
            }

            case COBBLE_WALL:
            case FENCE: {
                this.fence = true;
                break;
            }
        }

        if (checkMovingUp) {
            /*
            double boxY = checkedBox.getMaximum().getY();
            double delta = Math.abs(boxY - user.getBlockData().lastBlockY);
            //TODO: do something here..

            user.getBlockData().lastBlockY = boxY;
            */
        }
    }
}

package me.rhys.bedrock.util.block;

import lombok.Getter;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;

@Getter
public class BlockChecker {
    private final BoundingBox boundingBox;
    private final User user;

    public BlockChecker(User user) {
        this.boundingBox = user.getBoundingBox();
        this.user = user;
    }

    private boolean onGround, nearLiquid, nearIce, climbable, slime, piston, snow, fence, bed,
            stair, slab, movingUp, underBlock, web;

    public void check(BlockEntry blockEntry) {
        Block block = blockEntry.getBlock();
        BoundingBox checkedBox = blockEntry.getBoundingBox();

        if (checkedBox.collidesVertically(this.boundingBox
                .subtract(0, 0.1f, 0, 0, 0, 0))) {
            this.onGround = true;
        }

        boolean checkMovingUp = false;
        Class<? extends MaterialData> blockData = block.getType().getData();

        if ((checkedBox.getMaximum().getY()) >= boundingBox.getMaximum().getY()
                && (checkedBox.collidesVertically(boundingBox
                .add(0, 0, 0, 0, 0.35f, 0))
                || checkedBox.collidesVertically(boundingBox
                .add(0, 0, 0, 0, 0.45f, 0)))
                && block.getType() != Material.DOUBLE_PLANT) {
            this.underBlock = true;
        }

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

            case BED:
            case BED_BLOCK: {
                this.bed = true;
                break;
            }

            case SANDSTONE_STAIRS:
            case SMOOTH_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case ACACIA_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case NETHER_BRICK_STAIRS:
            case QUARTZ_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case WOOD_STAIRS: {
                this.stair = true;
                checkMovingUp = true;
                break;
            }

            case WEB: {
                this.web = true;
                break;
            }
        }

        if (block.getType() == Material.STEP
                || blockData == Step.class
                || blockData == WoodenStep.class) {
            slab = true;
            checkMovingUp = true;
        }

        if (checkMovingUp) {
            double boxY = checkedBox.getMaximum().getY();
            double delta = Math.abs(boxY - user.getBlockData().lastBlockY);
            this.movingUp = delta > 0 && Math.abs(user.getMovementProcessor().getDeltaY()) > 0 && this.onGround;
            user.getBlockData().lastBlockY = boxY;
        }
    }
}

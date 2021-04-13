package me.rhys.bedrock.util.block;

import cc.funkemunky.api.utils.BoundingBox;
import lombok.Getter;
import me.rhys.bedrock.base.user.User;
import org.bukkit.block.Block;

@Getter
public class BlockChecker {
    private final BoundingBox boundingBox;
    private final User user;

    public BlockChecker(User user) {
        this.boundingBox = user.getBoundingBox();
        this.user = user;
    }

    private boolean onGround;
    private boolean nearLiquid;

    public void check(BlockEntry blockEntry) {
        Block block = blockEntry.getBlock();
        BoundingBox checkedBox = blockEntry.getBoundingBox();

        if (checkedBox.collidesVertically(this.boundingBox
                .subtract(0, 0.1f, 0, 0, 0, 0))) {
            this.onGround = true;
        }

        switch (block.getType()) {
            case WATER:
            case STATIONARY_WATER:
            case STATIONARY_LAVA:
            case LAVA: {
                this.nearLiquid = true;
                break;
            }
        }
    }
}

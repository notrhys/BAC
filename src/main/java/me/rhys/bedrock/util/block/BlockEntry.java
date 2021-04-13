package me.rhys.bedrock.util.block;

import cc.funkemunky.api.utils.BoundingBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;

@AllArgsConstructor @Getter
public class BlockEntry {
    private final Block block;
    private final BoundingBox boundingBox;
}

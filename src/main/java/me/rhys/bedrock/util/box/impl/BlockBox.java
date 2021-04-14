package me.rhys.bedrock.util.box.impl;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.util.box.BoundingBox;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

@Getter
@Setter
public abstract class BlockBox {
    private Material material;
    private BoundingBox original;

    BlockBox(Material material, BoundingBox original) {
        this.material = material;
        this.original = original;
    }

    abstract List<BoundingBox> getBox(Block block);
}

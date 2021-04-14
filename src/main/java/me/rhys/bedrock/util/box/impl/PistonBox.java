package me.rhys.bedrock.util.box.impl;

import me.rhys.bedrock.util.box.BoundingBox;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PistonBox extends BlockBox {
    public PistonBox(Material material) {
        super(material, new BoundingBox(0,0,0,1,1,1));
    }

    @Override
    List<BoundingBox> getBox(Block block) {
        switch (getMaterial()) {
            case PISTON_BASE:
            case PISTON_STICKY_BASE: {
                org.bukkit.material.PistonBaseMaterial piston = (org.bukkit.material.PistonBaseMaterial) block.getType().getNewData(block.getData());

                if (!piston.isPowered()) {
                    return Collections.singletonList(getOriginal().add(block.getLocation().toVector()));
                } else {
                    switch (piston.getFacing()) {
                        case DOWN:
                            return Collections.singletonList(new BoundingBox(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                        case UP:
                            return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F).add(block.getLocation().toVector()));
                        case NORTH:
                            return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                        case SOUTH:
                            return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F).add(block.getLocation().toVector()));
                        case WEST:
                            return Collections.singletonList(new BoundingBox(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                        case EAST:
                            return Collections.singletonList(new BoundingBox(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F).add(block.getLocation().toVector()));
                    }
                }
            }
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE: {
                org.bukkit.material.PistonExtensionMaterial piston = (org.bukkit.material.PistonExtensionMaterial) block.getType().getNewData(block.getData());

                switch (piston.getFacing()) {
                    case DOWN:
                        return Arrays.asList(new BoundingBox(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F).add(block.getLocation().toVector()), new BoundingBox(0, 0, 0, 1, .25f, 1).add(block.getLocation().toVector()));
                    case UP:
                        return Arrays.asList(new BoundingBox(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F).add(block.getLocation().toVector()), new BoundingBox(0, .75f, 0, 1, 1, 1).add(block.getLocation().toVector()));
                    case NORTH:
                        return Arrays.asList(new BoundingBox(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F).add(block.getLocation().toVector()), new BoundingBox(0, 0, 0, 1, 1, .25f).add(block.getLocation().toVector()));
                    case SOUTH:
                        return Arrays.asList(new BoundingBox(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F).add(block.getLocation().toVector()), new BoundingBox(0, 0, .75f, 1, 1, 1).add(block.getLocation().toVector()));
                    case WEST:
                        return Arrays.asList(new BoundingBox(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F).add(block.getLocation().toVector()), new BoundingBox(0, 0, 0, .25f, 1, 1).add(block.getLocation().toVector()));
                    case EAST:
                        return Arrays.asList(new BoundingBox(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F).add(block.getLocation().toVector()), new BoundingBox(.75f, 0, 0, 1, 1, 1).add(block.getLocation().toVector()));
                }
            }
        }
        return Collections.singletonList(getOriginal());
    }
}

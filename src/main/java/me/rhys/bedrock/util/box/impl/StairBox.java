package me.rhys.bedrock.util.box.impl;

import me.rhys.bedrock.util.box.BoundingBox;
import me.rhys.bedrock.util.box.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class StairBox extends BlockBox {
    public StairBox(Material material) {
        super(material, new BoundingBox(0, 0, 0, 1, 1, 1));
    }

    @Override
    List<BoundingBox> getBox(Block block) {

        Object vBlock = ReflectionUtil.getVanillaBlock(block);
        Object world = ReflectionUtil.getWorldHandle(block.getWorld());
        Method voxelShapeMethod = ReflectionUtil.getMethod(ReflectionUtil.getNMSClass("BlockStairs"), "a", ReflectionUtil.iBlockData, ReflectionUtil.iBlockAccess, ReflectionUtil.blockPosition);
        Object voxelShape = ReflectionUtil.getMethodValue(voxelShapeMethod, vBlock, ReflectionUtil.getBlockData(block), world, ReflectionUtil.getBlockPosition(block.getLocation()));

        return Collections.singletonList(ReflectionUtil.toBoundingBox(ReflectionUtil.getMethodValue(ReflectionUtil.getMethod(ReflectionUtil.getNMSClass("VoxelShape"), "a"), voxelShape)).add(block.getLocation().toVector()));
    }
}


package me.rhys.bedrock.util.box.boxes;

import me.rhys.bedrock.util.BlockUtil;
import me.rhys.bedrock.util.MathUtil;
import me.rhys.bedrock.util.box.BlockBox;
import me.rhys.bedrock.util.box.BoundingBox;
import me.rhys.bedrock.util.reflection.ReflectionsUtil;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBox1_12_R1 implements BlockBox {

    @Override
    public List<BoundingBox> getCollidingBoxes(org.bukkit.World world, BoundingBox box) {
        int minX = MathUtil.floor(box.minX);
        int maxX = MathUtil.floor(box.maxX + 1);
        int minY = MathUtil.floor(box.minY);
        int maxY = MathUtil.floor(box.maxY + 1);
        int minZ = MathUtil.floor(box.minZ);
        int maxZ = MathUtil.floor(box.maxZ + 1);

        List<Location> locations = new ArrayList<>();

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    Location loc = new Location(world, x, y, z);
                    locations.add(loc);
                }
            }
        }

        List<BoundingBox> boxes = Collections.synchronizedList(new ArrayList<>());
        boolean chunkLoaded = isChunkLoaded(box.getMinimum().toLocation(world));

        if(chunkLoaded) {
            locations.parallelStream().forEach(loc -> {
                org.bukkit.block.Block block = BlockUtil.getBlock(loc);
                if (block != null && !block.getType().equals(Material.AIR)) {
                    if(BlockUtil.collisionBoundingBoxes.containsKey(block.getType())) {
                        BoundingBox box2 = BlockUtil.collisionBoundingBoxes.get(block.getType()).
                                add(block.getLocation().toVector());
                        boxes.add(box2);
                    } else {
                        int x = block.getX(), y = block.getY(), z = block.getZ();

                        BlockPosition pos = new BlockPosition(x, y, z);
                        World nmsWorld = ((CraftWorld) world).getHandle();
                        IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(pos);
                        Block nmsBlock = nmsiBlockData.getBlock();
                        List<AxisAlignedBB> preBoxes = new ArrayList<>();

                        nmsBlock.updateState(nmsiBlockData, nmsWorld, pos);
                        nmsBlock.a(nmsiBlockData,
                                nmsWorld,
                                pos,
                                (AxisAlignedBB) box.toAxisAlignedBB(),
                                preBoxes,
                                null,
                                false);

                        if (preBoxes.size() > 0) {
                            for (AxisAlignedBB aabb : preBoxes) {
                                BoundingBox bb = new BoundingBox(
                                        (float)aabb.a,
                                        (float)aabb.b,
                                        (float)aabb.c,
                                        (float)aabb.d,
                                        (float)aabb.e,
                                        (float)aabb.f);

                                if(bb.collides(box)) {
                                    boxes.add(bb);
                                }
                            }

                            if (nmsBlock instanceof BlockShulkerBox) {
                                TileEntity tileentity = nmsWorld.getTileEntity(pos);
                                BlockShulkerBox shulker = (BlockShulkerBox) nmsBlock;

                                if (tileentity instanceof TileEntityShulkerBox) {
                                    TileEntityShulkerBox entity = (TileEntityShulkerBox) tileentity;
                                    boxes.add(ReflectionsUtil.toBoundingBox(entity.a(nmsiBlockData)));

                                    if (entity.p().toString().contains("OPEN")
                                            || entity.p().toString().contains("CLOSING")) {
                                        boxes.add(new BoundingBox(
                                                block.getX(),
                                                block.getY(),
                                                block.getZ(),
                                                block.getX() + 1,
                                                block.getY() + 1.5f,
                                                block.getZ() + 1));
                                    }
                                }
                            }
                        } else {
                            AxisAlignedBB aabb = nmsiBlockData.d(nmsWorld, pos);

                            if(aabb != null) {
                                BoundingBox bb = ReflectionsUtil.toBoundingBox(aabb).add(x, y, z, x, y, z);

                                if(bb.collides(box)) {
                                    boxes.add(bb);
                                }
                            }
                        }
                    }
                }
            });
        };

        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.clone().toVector(), loc.clone().toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_12_R1.World world =
                ((org.bukkit.craftbukkit.v1_12_R1.CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide
                && world.isLoaded(
                new net.minecraft.server.v1_12_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()))
                && world.areChunksLoaded(
                new net.minecraft.server.v1_12_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()), 4);
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_12_R1.EntityLiving entity =
                ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) player).getHandle();
        return entity.cJ() != null
                && entity.cJ().getItem().f(entity.cJ()) != net.minecraft.server.v1_12_R1.EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle()
                .getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = ((WorldServer) entityPlayer.getWorld()).tracker
                .trackedEntities.get(entityPlayer.getId());
        return entry.b().getId();
    }

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().cy();
    }
}
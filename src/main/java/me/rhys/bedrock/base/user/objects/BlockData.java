package me.rhys.bedrock.base.user.objects;

public class BlockData {
    public boolean onGround, lastOnGround, nearLiquid, nearIce, climbable, slime, piston, snow, fence;
    public int liquidTicks, climbableTicks, iceTicks, slimeTicks, snowTicks, fenceTicks;
    public double lastBlockY;
}

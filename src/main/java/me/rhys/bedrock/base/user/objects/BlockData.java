package me.rhys.bedrock.base.user.objects;

import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.util.EventTimer;

public class BlockData {
    public boolean onGround, lastOnGround, nearLiquid, nearIce, climbable, slime, piston, snow, fence,
            bed, stair, slab;
    public int liquidTicks, climbableTicks, iceTicks, slimeTicks, snowTicks, fenceTicks, bedTicks,
            stairTicks, slabTicks;
    public double lastBlockY;
    public EventTimer movingUpTimer, climbableTimer;

    public void setupTimers(User user) {
        this.movingUpTimer = new EventTimer(20, user);
        this.climbableTimer = new EventTimer(60, user);
    }
}

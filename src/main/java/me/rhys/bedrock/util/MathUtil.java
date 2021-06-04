package me.rhys.bedrock.util;

import me.rhys.bedrock.base.user.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static Location getGroundLocation(User user) {
        World world = user.getPlayer().getWorld();

        Location location = user.getCurrentLocation().toBukkitLocation(world);
        int i = 0;
        while (!BlockUtil.getBlock(location).getRelative(BlockFace.DOWN).getType().isSolid()
                && location.getY() != 0) {
            if (i++ > 20) {
                break;
            }
            location.add(0, -1, 0);
        }


        if (location.getY() == 0){
            return user.getCurrentLocation().toBukkitLocation(world);
        }

        location.add(0, .05, 0);
        return location;
    }

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    public static int roundHalfUp(int scale, double value) {
        return (int) new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}

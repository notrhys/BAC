package me.rhys.bedrock.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

@AllArgsConstructor @Getter
public class PlayerLocation {
    private final World world;
    private final double x, y, z;
    private final float yaw, pitch;
    private final boolean clientGround;

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public Location toBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}

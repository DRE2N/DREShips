package de.fyreum.dreships.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

public class SerializableLocation implements Serializable {

    private static final long serialVersionUID = -8820723990301699800L;
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    public SerializableLocation(Location location) {
        if (location == null) {
            throw new NullPointerException("location is null");
        }
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public SerializableLocation(World world, double x, double y, double z) {
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public SerializableLocation(World world, double x, double y, double z, float yaw, float pitch) {
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}

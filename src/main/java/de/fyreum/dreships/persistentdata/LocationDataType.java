package de.fyreum.dreships.persistentdata;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class LocationDataType implements PersistentDataType<byte[], Location> {

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<Location> getComplexType() {
        return Location.class;
    }

    /*
    - [0] world name
    - [1] X
    - [2] Y
    - [3] Z
    - [4] yaw
    - [5] pitch
     */
    @Override
    public byte[] toPrimitive(Location location,PersistentDataAdapterContext context) {
        return Serialization.serialize(new String[]{
                location.getWorld().getName(),
                String.valueOf(location.getX()),
                String.valueOf(location.getY()),
                String.valueOf(location.getZ()),
                String.valueOf(location.getYaw()),
                String.valueOf(location.getPitch())});
    }

    @Override
    public Location fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        String[] stringArray = Serialization.deserialize(primitive);
        return new Location(
                Bukkit.getWorld(stringArray[0]),
                Double.parseDouble(stringArray[1]),
                Double.parseDouble(stringArray[2]),
                Double.parseDouble(stringArray[3]),
                Float.parseFloat(stringArray[4]),
                Float.parseFloat(stringArray[5]));
    }
}

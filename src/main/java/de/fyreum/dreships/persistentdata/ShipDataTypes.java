package de.fyreum.dreships.persistentdata;

import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataType;

public class ShipDataTypes {

    public static final PersistentDataType<byte[], Location> LOCATION = new LocationDataType();

}

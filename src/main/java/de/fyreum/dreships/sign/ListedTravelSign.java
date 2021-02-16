package de.fyreum.dreships.sign;

import de.fyreum.dreships.serialization.SerializableLocation;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.io.Serializable;

public class ListedTravelSign implements Serializable {

    private static final long serialVersionUID = -927668382329954738L;

    private String name, destinationName;
    private final SerializableLocation location, destination;
    private int price;
    private int cooldown; // seconds
    private boolean disabled;

    public ListedTravelSign(String name, String destinationName, SerializableLocation location, SerializableLocation destination,
                            int price, int cooldown, boolean disabled) {
        this.name = name;
        this.destinationName = destinationName;
        this.location = location;
        this.destination = destination;
        this.price = price;
        this.cooldown = cooldown;
        this.disabled = disabled;
    }

    public ListedTravelSign(Sign sign) {
        this(new TravelSign(sign));
    }

    public ListedTravelSign(TravelSign sign) {
        this.name = sign.getName();
        this.destinationName = sign.getDestinationName();
        this.location = new SerializableLocation(sign.getLocation());
        this.destination = new SerializableLocation(sign.getDestination());
        this.price = sign.getPrice();
        this.cooldown = sign.getCooldown();
        this.disabled = sign.isDisabled();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public Location getLocation() {
        return location.getLocation();
    }

    public Location getDestination() {
        return destination.getLocation();
    }

    public SerializableLocation getSerializableLocation() {
        return location;
    }

    public SerializableLocation getSerializableDestination() {
        return destination;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}

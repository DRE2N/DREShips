package de.fyreum.dreships.config;

import de.fyreum.dreships.sign.ListedTravelSign;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This class is for visual use (/ds list) only.
 * Changing its values won't effect the actual signs.
 * @see de.fyreum.dreships.commands.ListCommand
 */
public class SignContainer implements Iterable<ListedTravelSign>, Serializable {

    private static final long serialVersionUID = -3362376096654924167L;
    private final Set<ListedTravelSign> travelSigns;

    public SignContainer() {
        this.travelSigns = new HashSet<>();
    }

    public SignContainer(Set<ListedTravelSign> travelSigns) {
        this.travelSigns = travelSigns;
    }

    public void add(TravelSign t) {
        this.travelSigns.add(new ListedTravelSign(t));
    }

    public void add(ListedTravelSign t) {
        this.travelSigns.add(t);
    }

    public boolean remove(ListedTravelSign t) {
        return this.travelSigns.remove(t);
    }

    public void remove(Location location) {
        try {
            travelSigns.removeIf(travelSign -> travelSign.getLocation().equals(location));
        } catch (NullPointerException ignored) {

        }
    }

    public boolean contains(TravelSign t) {
        return this.contains(t.getLocation());
    }

    public boolean contains(ListedTravelSign t) {
        return this.contains(t.getLocation());
    }

    public boolean contains(@NotNull Location location) {
        try {
            return this.stream().filter(sign -> sign.getLocation().equals(location)).findFirst().orElse(null) != null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    // getter

    public Set<ListedTravelSign> getListedTravelSigns() {
        return travelSigns;
    }

    // implementations

    @NotNull
    @Override
    public Iterator<ListedTravelSign> iterator() {
        return travelSigns.iterator();
    }

    @Override
    public void forEach(Consumer<? super ListedTravelSign> action) {
        travelSigns.forEach(action);
    }

    @Override
    public Spliterator<ListedTravelSign> spliterator() {
        return travelSigns.spliterator();
    }

    public Stream<ListedTravelSign> stream() {
        return travelSigns.stream();
    }

    public Stream<ListedTravelSign> parallelStream() {
        return travelSigns.stream();
    }
}

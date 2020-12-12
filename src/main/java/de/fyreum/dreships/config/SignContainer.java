package de.fyreum.dreships.config;

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

public class SignContainer implements Iterable<TravelSign>, Serializable {

    private static final long serialVersionUID = -3362376096654924167L;
    private final Set<TravelSign> travelSigns;

    public SignContainer() {
        this.travelSigns = new HashSet<>();
    }

    public SignContainer(Set<TravelSign> travelSigns) {
        this.travelSigns = travelSigns;
    }

    public void add(TravelSign t) {
        this.travelSigns.add(t);
    }

    public boolean remove(TravelSign t) {
        return this.travelSigns.remove(t);
    }

    public void remove(Location location) {
        travelSigns.removeIf(travelSign -> travelSign.getLocation().equals(location));
    }

    public boolean contains(TravelSign t) {
        return this.contains(t.getLocation());
    }

    public boolean contains(@NotNull Location location) {
        return this.stream().filter(sign -> sign.getLocation().equals(location)).findFirst().orElse(null) != null;
    }

    // getter

    public Set<TravelSign> getTravelSigns() {
        return travelSigns;
    }

    // implementations

    @NotNull
    @Override
    public Iterator<TravelSign> iterator() {
        return travelSigns.iterator();
    }

    @Override
    public void forEach(Consumer<? super TravelSign> action) {
        travelSigns.forEach(action);
    }

    @Override
    public Spliterator<TravelSign> spliterator() {
        return travelSigns.spliterator();
    }

    public Stream<TravelSign> stream() {
        return travelSigns.stream();
    }

    public Stream<TravelSign> parallelStream() {
        return travelSigns.stream();
    }
}

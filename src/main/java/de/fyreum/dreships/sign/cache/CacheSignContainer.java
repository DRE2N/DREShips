package de.fyreum.dreships.sign.cache;

import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CacheSignContainer {
    private CacheSign c1, c2; // values

    public void save(@NotNull CacheSign sign) {
        if (c1 == null || c1.getSign().getLocation().equals(sign.getSign().getLocation())) {
            c1 = sign;
        } else if (c2 == null || c2.getSign().getLocation().equals(sign.getSign().getLocation())){
            c2 = sign;
        } else {
            c1 = sign;
        }
    }

    public int getSize() {
        return isFull() ? 2 : (c1 != null ? 1 : 0);
    }

    public Set<CacheSign> getSaved() {
        Set<CacheSign> set = new HashSet<>();
        set.add(c1);
        set.add(c2);
        return set;
    }

    public void clear() {
        c1 = null;
        c2 = null;
    }

    public boolean contains(Sign sign) {
        return c1.getSign().getLocation().equals(sign.getLocation()) || c2.getSign().getLocation().equals(sign.getLocation());
    }

    public boolean isFull() {
        return c1 != null && c2 != null;
    }

    public CacheSign getFirst() {
        return c1;
    }

    public CacheSign getSecond() {
        return c2;
    }
}

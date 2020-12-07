package de.fyreum.dreships.sign.cache;

import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerCache {

    private final Map<UUID, CacheSignContainer> cacheMap;

    public PlayerCache() {
        this.cacheMap = new HashMap<>();
    }

    public CacheSignContainer getContainer(UUID uuid) {
        if (this.getCacheMap().get(uuid) == null) {
            getCacheMap().put(uuid, new CacheSignContainer());
        }
        return getCacheMap().get(uuid);
    }

    public void save(UUID uuid, CacheSign sign) {
        getContainer(uuid).save(sign);
    }

    public Set<CacheSign> getCached(UUID uuid) {
        return getContainer(uuid).getSaved();
    }

    public void clear(UUID uuid) {
        getContainer(uuid).clear();
    }

    public boolean alreadyCached(UUID uuid, Sign sign) {
        if (getCacheMap().get(uuid) == null) {
            return false;
        } else {
            return getContainer(uuid).contains(sign);
        }
    }

    public boolean isFull(UUID uuid) {
        return getContainer(uuid).isFull();
    }

    // getter

    public int getSize(UUID uuid) {
        return getContainer(uuid).getSize();
    }

    public CacheSign getFirst(UUID uuid) {
        return getContainer(uuid).getFirst();
    }

    public CacheSign getSecond(UUID uuid) {
        return getContainer(uuid).getSecond();
    }

    public Map<UUID, CacheSignContainer> getCacheMap() {
        return cacheMap;
    }

}

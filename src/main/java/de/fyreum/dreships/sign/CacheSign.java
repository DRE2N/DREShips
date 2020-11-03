package de.fyreum.dreships.sign;

import org.bukkit.block.Sign;

public class CacheSign {

    private final Sign sign;
    private final String name;

    public CacheSign(Sign sign, String name) {
        this.sign = sign;
        this.name = name;
    }

    public Sign getSign() {
        return sign;
    }

    public String getName() {
        return name;
    }
}

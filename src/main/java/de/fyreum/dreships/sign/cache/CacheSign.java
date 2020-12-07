package de.fyreum.dreships.sign.cache;

import org.bukkit.block.Sign;

public class CacheSign {

    private Sign sign;
    private String name;

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

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public void setName(String name) {
        this.name = name;
    }
}

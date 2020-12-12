package de.fyreum.dreships.persistentdata;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BooleanDataType implements PersistentDataType<String, Boolean> {

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Boolean> getComplexType() {
        return Boolean.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull Boolean b, @NotNull PersistentDataAdapterContext context) {
        return b.toString();
    }

    @NotNull
    @Override
    public Boolean fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext context) {
        return Boolean.parseBoolean(s);
    }
}

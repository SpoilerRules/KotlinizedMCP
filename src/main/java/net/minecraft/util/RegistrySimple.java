package net.minecraft.util;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RegistrySimple<K, V> implements IRegistry<K, V> {
    private static final Logger logger = LogManager.getLogger();
    protected final Map<K, V> registryObjects = this.createUnderlyingMap();

    protected Map<K, V> createUnderlyingMap() {
        return Maps.newHashMap();
    }

    public V getObject(K name) {
        return this.registryObjects.get(name);
    }

    public void putObject(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");

        if (this.registryObjects.containsKey(key)) {
            logger.debug("Adding duplicate key '" + key + "' to registry");
        }

        this.registryObjects.put(key, value);
    }

    public Set<K> getKeys() {
        return Collections.unmodifiableSet(this.registryObjects.keySet());
    }

    public boolean containsKey(K key) {
        return this.registryObjects.containsKey(key);
    }

    public @NotNull Iterator<V> iterator() {
        return this.registryObjects.values().iterator();
    }
}
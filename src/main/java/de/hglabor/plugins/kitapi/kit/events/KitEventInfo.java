package de.hglabor.plugins.kitapi.kit.events;

import org.bukkit.event.Event;

public class KitEventInfo {
    private final boolean ignoreCooldown;
    private final Class<? extends Event> event;

    @SuppressWarnings("unchecked")
    public KitEventInfo(boolean ignoreCooldown, Class<?> event) {
        this.ignoreCooldown = ignoreCooldown;
        this.event = (Class<? extends Event>) event;
    }

    public boolean isIgnoreCooldown() {
        return ignoreCooldown;
    }

    public Class<? extends Event> getEvent() {
        return event;
    }
}

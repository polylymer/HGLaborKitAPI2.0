package de.hglabor.plugins.kitapi.kit.config;

public class Cooldown {
    private final boolean hasCooldown;
    private long startTime;
    private int additionalTime = 0;

    public Cooldown(boolean hasCooldown) {
        this.hasCooldown = hasCooldown;
    }

    public Cooldown(boolean hasCooldown, long startTime) {
        this.hasCooldown = hasCooldown;
        this.startTime = startTime;
    }

    public boolean hasCooldown() {
        return hasCooldown;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getAdditionalTime() {
        return additionalTime;
    }
}

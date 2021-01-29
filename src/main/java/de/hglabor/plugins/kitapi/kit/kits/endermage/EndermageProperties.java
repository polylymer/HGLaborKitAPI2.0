package de.hglabor.plugins.kitapi.kit.kits.endermage;

import de.hglabor.plugins.kitapi.kit.config.KitProperties;

public class EndermageProperties extends KitProperties {
    private final long magedTimeStamp;

    public EndermageProperties(long magedTimeStamp) {
        this.magedTimeStamp = magedTimeStamp;
    }

    public long getMagedTimeStamp() {
        return magedTimeStamp;
    }
}

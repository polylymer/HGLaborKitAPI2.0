package de.hglabor.plugins.kitapi.kit.kits.endermage;

public class EndermageProperties {
    private final long magedTimeStamp;

    //TODO maybe there will be more information in future
    public EndermageProperties(long magedTimeStamp) {
        this.magedTimeStamp = magedTimeStamp;
    }

    public long getMagedTimeStamp() {
        return magedTimeStamp;
    }
}

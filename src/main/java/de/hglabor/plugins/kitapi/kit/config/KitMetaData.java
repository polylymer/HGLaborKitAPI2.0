package de.hglabor.plugins.kitapi.kit.config;

public enum KitMetaData {
    INGLADIATOR,
    GLADIATOR_BLOCK,
    HAS_BEEN_MAGED,
    SWITCHER_BALL,
    SPIT_SOUP,
    SPIT_PROJECTILE,
    ;

    public String getKey() {
        return name();
    }
}

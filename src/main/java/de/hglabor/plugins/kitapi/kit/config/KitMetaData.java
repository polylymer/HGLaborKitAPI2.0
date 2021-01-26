package de.hglabor.plugins.kitapi.kit.config;

public enum KitMetaData {
    INGLADIATOR,
    GLADIATOR_BLOCK,
    HAS_BEEN_MAGED,
    SWITCHER_BALL,
    FEAST_BLOCK,
    UNBREAKABLE_BLOCK;
    SPIT_SOUP,
    SPIT_PROJECTILE;


    public String getKey() {
        return name();
    }
}

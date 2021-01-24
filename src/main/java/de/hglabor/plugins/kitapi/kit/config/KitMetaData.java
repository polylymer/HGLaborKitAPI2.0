package de.hglabor.plugins.kitapi.kit.config;

public enum KitMetaData {
    INGLADIATOR,
    GLADIATOR_BLOCK,
    HAS_BEEN_MAGED,
    ;

    public String getKey() {
        return name();
    }
}

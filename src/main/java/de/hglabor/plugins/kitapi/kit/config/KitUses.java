package de.hglabor.plugins.kitapi.kit.config;

public class KitUses {
    private int use;

    public void increaseUse() {
        use++;
    }

    public void resetUse() {
        use = 0;
    }

    public int getUse() {
        return use;
    }
}

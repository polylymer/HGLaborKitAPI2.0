package de.hglabor.plugins.kitapi.util;

import java.util.Random;

public final class ChanceUtils {
    private ChanceUtils() {
    }

    public static boolean roll(int maximalChance) {
        Random random = new Random();
        return random.nextInt(99) + 1 <= maximalChance;
    }
}

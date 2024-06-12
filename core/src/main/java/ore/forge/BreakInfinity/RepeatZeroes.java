package ore.forge.BreakInfinity;

import java.util.HashMap;

class RepeatZeroes {
    private static final HashMap<Integer, String> cache = new HashMap<Integer, String>();

    public static String repeatZeroes(int count) {
        if (count <= 0) return "";

        if (!cache.containsKey(count)) {
            cache.put(count, "0".repeat(count));
        }

        return cache.get(count);
    }

    public static String trailZeroes(int places) {
        return places > 0 ? "." + repeatZeroes(places) : "";
    }

    public static String padRight(String string, int places) {
        return String.format("%-" + places + "s", string).replace(" ", "0");
    }
}

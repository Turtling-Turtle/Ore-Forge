package ore.forge.game.Items.Strategies.UpgradeStrategies.game;

public class Searching {

    public static <E extends Comparable<E>> boolean linearSearch(E[] data, E target) {
        return linearSearch(data, 0, data.length-1, target);
    }

    public static <E extends Comparable<E>> boolean linearSearch(E[] data, int min, int max, E target) {
        if (min < 0 || max >= data.length || min > max) { return false; }

        for (int i = min; i <= max; i++) {
            if (data[i].compareTo(target) == 0) {
                return true;
            }
        }
        return false;
    }

//    public static <E extends Comparable<E>> boolean binarySearch(E[] data, E target) {
//        return binarySearch(data, 0, data.length-1, target);
//    }
//
//    public static <E extends Comparable<E>> boolean binarySearch(E[] data, int min, int max, E target) {
//        if (min < 0 || max >= data.length || min > max) { return false; } // Base case of "not there"
//
//        int midPoint = (max + min) / 2;
//
//        if (data[midPoint].compareTo(target) == 0) { // Found it! Base case
//            return true;
//        } else if (data[midPoint].compareTo(target) > 0) { // when target should be in left partition
//            return binarySearch(data, min, midPoint-1, target);
//        } else { // when target should be in the right partition
//            return binarySearch(data, midPoint+1, max, target);
//        }
//    }

    public static <E extends Comparable<E>> E binarySearch(E[] data, E target) {
        int min = 0;
        int max = data.length-1;
        int midPoint;
        while (min <= max) {
           midPoint = (max + min) /2;
           if(data[midPoint].compareTo(target) == 0) {
               return data[midPoint];
           } else if(data[midPoint].compareTo(target) > 0) {
               max = midPoint-1;
           } else {
               min = midPoint+1;
           }
        }
        return null;
    }

}

package ore.forge.game.Items.Strategies.UpgradeStrategies.game;

public class Sorting {

    public static <E extends Comparable<E>> void selectionSort(E[] data) {
        int min; // the index of the next smallest item
        for (int i = 0; i < data.length; i++) { // loop through all indices
            min = i;
            for (int j = i + 1; j < data.length; j++) {
                if (data[j].compareTo(data[min]) < 0) {
                    min = j;
                }
            }
            swap(data, min, i);
        }
    }

    private static <E extends Comparable<E>> void swap(E[] data, int firstIndex, int secondIndex) {
        E temp = data[secondIndex];
        data[secondIndex] = data[firstIndex];
        data[firstIndex] = temp;
    }

    // Water analogy... dissolved bubbles coming to the surface
    public static <E extends Comparable<E>> void bubbleSort(E[] data) {
        // Loop over the items as the "water line" drops from the largest index down to 0
        for (int waterLine = data.length-1; waterLine >= 0; waterLine--) {
            // Check items from index 0 to just below the water line to snare the largest value
            for (int net = 0; net < waterLine; net++) {
                if (data[net].compareTo(data[net+1]) > 0) {
                // comparator.compare(data[net], data[net+1]) > 0
                    swap(data, net, net+1);
                }
            }
        }
    }


    /* Quick Sort
     *
     * [305 65 7 90 120 110 8]
     * [[65 7 8] 90 [305 120 110]]
     * [[[] 7 [65 8]]] 90 [[110] 120 [305]]]
     * [[[] 7 [[8] 65 []]]]] 90 [[110] 120 [305]]]
     * [7 8 65 90 110 120 305] *** Sorted
     *
     * Worst Case!!! n^2
     * [120 90 8 7 65 110 305]
     * [[] 7 [120 90 8 65 110 305]]
     * [7 [8 [120 90 65 110 305]]]
     * [7 [8 [[] 65 [120 90 110 305]]]]]
     * [7 [8 [65 [90 [120 110 305]]]]]
     * [7 [8 [65 [90 [110 [120 305]]]]]
     */

    public static <E extends Comparable<E>> void quickSort(E[] data) {
        quickSort(data, 0, data.length-1);
    }

    private static <E extends Comparable<E>> void quickSort(E[] data, int min, int max) {
        if (min >= max) { return; } //Base Case!!!

        // partition into two buckets (partitions) using the midpoint item for comparisons
        // resulting in the new index of the partition element (indexOfPartition)
        int indexOfPartition = partition(data, min, max);

        // quicksort the left-hand bucket [min -> indexOfPartition-1 ]
        quickSort(data, min, indexOfPartition-1);
        // quicksort the right-hand bucket [indexOfPartition+1 -> max]
        quickSort(data, indexOfPartition+1, max);
    }

    private static <E extends Comparable<E>> int partition(E[] data, int min, int max) {
        E partitionElement;
        int left, right;
        int midpoint = (min + max) / 2;

        // use the midpoint to grab the partitionElement for comparison
        partitionElement = data[midpoint];

        // move it out of the way ... for now
        swap(data, midpoint, min);

        // sweep from the left side and the right side
        left = min;
        right = max;

        while(left < right) {
            while (left < max && data[left].compareTo(partitionElement) <= 0) {
                left++;
            }
            // we have hypothetically found an element that needs to be moved to the right parition... at index left
            while (right > min && data[right].compareTo(partitionElement) > 0) {
                right--;
            }
            // we have hypothetically found an element that needs to be moved to the left parition... at index right
            if (left < right) {
                swap(data, left, right);
            }
        }
        // when this loop ends... the index at "right" IS THE PARTITION INDEX
        // it points to the LAST smaller element before moving on the the larger elements
        // [7 | 6 305 42] right = 1
        // [6 [7] 305 42]
        swap(data, min, right);

        return right;
    }

    public static <E extends Comparable<E>> void mergeSort(E[] data) {
        mergeSort(data, 0, data.length-1);
    }

    private static <E extends Comparable<E>> void mergeSort(E[] data, int min, int max) {
        if (min >= max) { return; } //Base Case!!!

        int midPoint = (min + max) / 2;

        // Left Bucket
        // data from min to midPoint
        // mergeSort the left bucket
        mergeSort(data, min, midPoint);

        // Right Bucket
        // data from midPoint+1 to max
        // mergeSort the right bucket
        mergeSort(data, midPoint+1, max);

        // Two sorted buckets
        // To MERGE the buckets
        merge(data, min, midPoint, max);
    }


    private static <E extends Comparable<E>> void merge(E[] data, int first, int midPoint, int last) {
        E[] temp = (E[])(new Comparable[data.length]); // this our playground array

        int firstLeft = first, lastLeft = midPoint; // left hand bucket boundaries
        int firstRight = midPoint + 1, lastRight = last; // right hand bucket boundaries
        int index = first; // index of the merged array

        while (firstLeft <= lastLeft && firstRight <= lastRight) {
            if (data[firstLeft].compareTo(data[firstRight]) < 0) { // if next left bucket item is smaller
                temp[index] = data[firstLeft];
                firstLeft++;
            } else {
                temp[index] = data[firstRight];
                firstRight++;
            }
            index++;
        }
        // remaining left bucket items...
        while (firstLeft <= lastLeft) {
            temp[index] = data[firstLeft];
            firstLeft++;
            index++;
        }
        // remaining right bucket items...
        while (firstRight <= lastRight) {
            temp[index] = data[firstRight];
            firstRight++;
            index++;
        }

        // Copy the sorted values into the original array
        for (int i = first; i <= last; i++) {
            data[i] = temp[i];
        }
    }


}

package org.scify.jedai.gui.utilities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Compares Block Cleaning method names, to put them in the correct order.
 * If Strings are included which are not Block Cleaning methods, they are put
 * after the Block Cleaning methods, in alphabetical order.
 *
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class BlockCleaningCustomComparator implements Comparator<String> {
    private final List<String> correctOrder;

    public BlockCleaningCustomComparator() {
        // Add strings in order
        correctOrder = Arrays.asList(
                JedaiOptions.SIZE_BASED_BLOCK_PURGING,
                JedaiOptions.COMPARISON_BASED_BLOCK_PURGING,
                JedaiOptions.BLOCK_FILTERING
        );
    }

    @Override
    public int compare(String o1, String o2) {
        int index1 = correctOrder.indexOf(o1);
        int index2 = correctOrder.indexOf(o2);

        if (index1 == index2) {
            // None of the Strings exist in the correctOrder list, or they are the same item
            return o1.compareTo(o2);
        } else if (index1 == -1) {
            // Positive integer: o1 > o2
            return 1;
        } else if (index2 == -1) {
            // Negative integer: o1 < o2
            return -1;
        }

        // Compare the indexes of the items
        return Integer.compare(index1, index2);
    }
}

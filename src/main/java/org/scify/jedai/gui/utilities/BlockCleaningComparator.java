package org.scify.jedai.gui.utilities;

import java.util.Arrays;

/**
 * Compares Block Cleaning method names, to put them in the correct order.
 * If Strings are included which are not Block Cleaning methods, they are put
 * after the Block Cleaning methods, in alphabetical order.
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class BlockCleaningComparator extends GenericMethodNameComparator {
    public BlockCleaningComparator() {
        // Add strings in order
        this.correctOrder.addAll(Arrays.asList(
                JedaiOptions.SIZE_BASED_BLOCK_PURGING,
                JedaiOptions.COMPARISON_BASED_BLOCK_PURGING,
                JedaiOptions.BLOCK_FILTERING
        ));
    }
}

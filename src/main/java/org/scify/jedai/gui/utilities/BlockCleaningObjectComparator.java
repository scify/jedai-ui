package org.scify.jedai.gui.utilities;

import java.util.Comparator;

/**
 * Compares Block Cleaning method configurations, to put them in the correct order.
 * Works by using BlockCleaningComparator to compare the method configuration object's names.
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class BlockCleaningObjectComparator implements Comparator<BlClMethodConfiguration> {
    private static BlockCleaningComparator blClComparator = new BlockCleaningComparator();

    @Override
    public int compare(BlClMethodConfiguration o1, BlClMethodConfiguration o2) {
        return blClComparator.compare(o1.getName(), o2.getName());
    }
}

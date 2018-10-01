package org.scify.jedai.gui.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Generic comparator to sort lists of method names in a specified order (by extending this class and adding the items
 * you want to sort in order, to the correctOrder list).
 */
public abstract class GenericMethodNameComparator implements Comparator<String> {
    final List<String> correctOrder = new ArrayList<>();

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

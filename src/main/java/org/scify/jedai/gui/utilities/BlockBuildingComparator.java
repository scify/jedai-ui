package org.scify.jedai.gui.utilities;

import java.util.Arrays;

public class BlockBuildingComparator extends GenericMethodNameComparator {
    public BlockBuildingComparator() {
        // Add strings in order
        this.correctOrder.addAll(Arrays.asList(
                JedaiOptions.STANDARD_TOKEN_BUILDING,
                JedaiOptions.SORTED_NEIGHBORHOOD,
                JedaiOptions.SORTED_NEIGHBORHOOD_EXTENDED,
                JedaiOptions.Q_GRAMS_BLOCKING,
                JedaiOptions.Q_GRAMS_BLOCKING_EXTENDED,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING_EXTENDED,
                JedaiOptions.LSH_SUPERBIT_BLOCKING,
                JedaiOptions.LSH_MINHASH_BLOCKING
        ));
    }
}

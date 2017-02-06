package wizard;

import Utilities.Enumerations.BlockBuildingMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MethodMapping {
    public static final Map<String, BlockBuildingMethod> blockBuildingMethods = createMap();

    private static Map<String, BlockBuildingMethod> createMap() {
        Map<String, BlockBuildingMethod> result = new HashMap<>();
        result.put("Standard/Token Blocking", BlockBuildingMethod.STANDARD_BLOCKING);
        result.put("Attribute Clustering", BlockBuildingMethod.ATTRIBUTE_CLUSTERING);
        result.put("Sorted Neighborhood", BlockBuildingMethod.SORTED_NEIGHBORHOOD);
        result.put("Sorted Neighborhood (Extended)", BlockBuildingMethod.EXTENDED_SORTED_NEIGHBORHOOD);
        result.put("Q-Grams Blocking", BlockBuildingMethod.Q_GRAMS_BLOCKING);
        result.put("Q-Grams Blocking (Extended)", BlockBuildingMethod.EXTENDED_Q_GRAMS_BLOCKING);
        result.put("Suffix Arrays Blocking", BlockBuildingMethod.SUFFIX_ARRAYS);
        result.put("Suffix Arrays Blocking (Extended)", BlockBuildingMethod.EXTENDED_SUFFIX_ARRAYS);
        return Collections.unmodifiableMap(result);
    }
}

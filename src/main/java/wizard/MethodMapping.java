package wizard;

import EntityClustering.*;
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

    public static IEntityClustering getEntityClusteringMethod(String methodStr) {
        IEntityClustering method;

        switch (methodStr) {
            case "Center Clustering":
                method = new CenterClustering();
                break;
            case "Connected Components Clustering":
                method = new ConnectedComponentsClustering();
                break;
            case "Cut Clustering":
                method = new CutClustering();
                break;
            case "Markov Clustering":
                method = new MarkovClustering();
                break;
            case "Merge-Center Clustering":
                method = new MergeCenterClustering();
                break;
            case "Ricochet SR Clustering":
                method = new RicochetSRClustering();
                break;
            default:
                method = null;
        }

        return method;
    }
}

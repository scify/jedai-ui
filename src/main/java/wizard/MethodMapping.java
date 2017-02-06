package wizard;

import BlockProcessing.BlockRefinement.BlockFiltering;
import BlockProcessing.BlockRefinement.BlockScheduling;
import BlockProcessing.BlockRefinement.ComparisonsBasedBlockPurging;
import BlockProcessing.BlockRefinement.SizeBasedBlockPurging;
import BlockProcessing.ComparisonRefinement.*;
import BlockProcessing.IBlockProcessing;
import DataModel.AbstractBlock;
import EntityClustering.*;
import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.Enumerations.WeightingScheme;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    public static List<AbstractBlock> processBlocks(List<AbstractBlock> blocks, String methodType, String method) {
        IBlockProcessing processingMethod;

        // Use appropriate processing method
        switch (method) {
            case "Block Filtering":
                processingMethod = new BlockFiltering();
                break;
            case "Block Scheduling":
                processingMethod = new BlockScheduling();
                break;
            case "Size-based Block Purging":
                processingMethod = new SizeBasedBlockPurging();
                break;
            case "Comparison-based Block Purging":
                processingMethod = new ComparisonsBasedBlockPurging();
                break;
            case "Comparison Propagation":
                processingMethod = new ComparisonPropagation();
                break;
            case "Cardinality Edge Pruning (CEP)":
//                processingMethod = new CardinalityEdgePruning();
//                break;
                return blocks;
            case "Cardinality Node Pruning (CNP)":
                processingMethod = new CardinalityNodePruning(WeightingScheme.CBS);
                break;
            case "Weighed Edge Pruning (WEP)":
                processingMethod = new WeightedEdgePruning(WeightingScheme.CBS);
                break;
            case "Weighed Node Pruning (WNP)":
                processingMethod = new WeightedNodePruning(WeightingScheme.CBS);
                break;
            case "Reciprocal Cardinality Node Pruning (ReCNP)":
                processingMethod = new ReciprocalCardinalityNodePruning(WeightingScheme.CBS);
                break;
            case "Reciprocal Weighed Node Pruning (ReWNP)":
                processingMethod = new ReciprocalWeightedNodePruning(WeightingScheme.CBS);
                break;
            default:
                System.err.println("Method not mapped??");

                return blocks;
        }

        // Process the blocks
        blocks = processingMethod.refineBlocks(blocks);

        // Return the blocks
        return blocks;
    }
}

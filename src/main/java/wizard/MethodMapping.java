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
import utils.JedaiOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodMapping {
    public static final Map<String, BlockBuildingMethod> blockBuildingMethods = createMap();

    private static Map<String, BlockBuildingMethod> createMap() {
        Map<String, BlockBuildingMethod> result = new HashMap<>();
        result.put(JedaiOptions.STANDARD_TOKEN_BUILDING, BlockBuildingMethod.STANDARD_BLOCKING);
        result.put(JedaiOptions.ATTRIBUTE_CLUSTERING, BlockBuildingMethod.ATTRIBUTE_CLUSTERING);
        result.put(JedaiOptions.SORTED_NEIGHBORHOOD, BlockBuildingMethod.SORTED_NEIGHBORHOOD);
        result.put(JedaiOptions.SORTED_NEIGHBORHOOD_EXTENDED, BlockBuildingMethod.EXTENDED_SORTED_NEIGHBORHOOD);
        result.put(JedaiOptions.Q_GRAMS_BLOCKING, BlockBuildingMethod.Q_GRAMS_BLOCKING);
        result.put(JedaiOptions.Q_GRAMS_BLOCKING_EXTENDED, BlockBuildingMethod.EXTENDED_Q_GRAMS_BLOCKING);
        result.put(JedaiOptions.SUFFIX_ARRAYS_BLOCKING, BlockBuildingMethod.SUFFIX_ARRAYS);
        result.put(JedaiOptions.SUFFIX_ARRAYS_BLOCKING_EXTENDED, BlockBuildingMethod.EXTENDED_SUFFIX_ARRAYS);
        return Collections.unmodifiableMap(result);
    }

    public static IEntityClustering getEntityClusteringMethod(String methodStr) {
        IEntityClustering method;

        switch (methodStr) {
            case JedaiOptions.CENTER_CLUSTERING:
                method = new CenterClustering();
                break;
            case JedaiOptions.CONNECTED_COMPONENTS_CLUSTERING:
                method = new ConnectedComponentsClustering();
                break;
            case JedaiOptions.CUT_CLUSTERING:
                method = new CutClustering();
                break;
            case JedaiOptions.MARKOV_CLUSTERING:
                method = new MarkovClustering();
                break;
            case JedaiOptions.MERGE_CENTER_CLUSTERING:
                method = new MergeCenterClustering();
                break;
            case JedaiOptions.RICOCHET_SR_CLUSTERING:
                method = new RicochetSRClustering();
                break;
            case JedaiOptions.UNIQUE_MAPPING_CLUSTERING:
                method = new UniqueMappingClustering();
                break;
            default:
                method = null;
        }

        return method;
    }

    public static List<AbstractBlock> processBlocks(List<AbstractBlock> blocks, String method) {
        IBlockProcessing processingMethod;

        // Use appropriate processing method
        switch (method) {
            case JedaiOptions.BLOCK_FILTERING:
                processingMethod = new BlockFiltering();
                break;
            case JedaiOptions.BLOCK_SCHEDULING:
                processingMethod = new BlockScheduling();
                break;
            case JedaiOptions.SIZE_BASED_BLOCK_PURGING:
                processingMethod = new SizeBasedBlockPurging();
                break;
            case JedaiOptions.COMPARISON_BASED_BLOCK_PURGING:
                processingMethod = new ComparisonsBasedBlockPurging();
                break;
            case JedaiOptions.COMPARISON_PROPAGATION:
                processingMethod = new ComparisonPropagation();
                break;
            case JedaiOptions.CARDINALITY_EDGE_PRUNING:
                processingMethod = new CardinalityEdgePruning(WeightingScheme.ECBS);
                break;
            case JedaiOptions.CARDINALITY_NODE_PRUNING:
                processingMethod = new CardinalityNodePruning(WeightingScheme.ECBS);
                break;
            case JedaiOptions.WEIGHED_EDGE_PRUNING:
                processingMethod = new WeightedEdgePruning(WeightingScheme.ECBS);
                break;
            case JedaiOptions.WEIGHED_NODE_PRUNING:
                processingMethod = new WeightedNodePruning(WeightingScheme.ECBS);
                break;
            case JedaiOptions.RECIPROCAL_CARDINALITY_NODE_PRUNING:
                processingMethod = new ReciprocalCardinalityNodePruning(WeightingScheme.ECBS);
                break;
            case JedaiOptions.RECIPROCAL_WEIGHED_NODE_PRUNING:
                processingMethod = new ReciprocalWeightedNodePruning(WeightingScheme.ECBS);
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

package wizard;

import BlockProcessing.BlockRefinement.BlockFiltering;
import BlockProcessing.BlockRefinement.ComparisonsBasedBlockPurging;
import BlockProcessing.BlockRefinement.SizeBasedBlockPurging;
import BlockProcessing.ComparisonRefinement.*;
import BlockProcessing.IBlockProcessing;
import EntityClustering.*;
import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.Enumerations.RepresentationModel;
import Utilities.Enumerations.SimilarityMetric;
import Utilities.Enumerations.WeightingScheme;
import utils.JedaiOptions;

import java.util.*;

public class MethodMapping {
    public static final Map<String, BlockBuildingMethod> blockBuildingMethods = createMap();
    private static final Map<SimilarityMetric, String> similarityMetricToString = createSimilarityMetricsMap();
    private static final Map<String, RepresentationModel> stringToRepresentationModel = createRepresentationModelsMap();
    private static final Map<String, SimilarityMetric> stringToSimilarityMetric = createStringToSimMetricMap();

    /**
     * Return map of block building methods' String names to their enumeration values
     *
     * @return
     */
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

    /**
     * Return map of Similarity Metrics (from the SimilarityMetric enumeration) to their String names
     *
     * @return
     */
    private static Map<SimilarityMetric, String> createSimilarityMetricsMap() {
        Map<SimilarityMetric, String> result = new HashMap<>();
        result.put(SimilarityMetric.ARCS_SIMILARITY, JedaiOptions.ARCS_SIMILARITY);
        result.put(SimilarityMetric.COSINE_SIMILARITY, JedaiOptions.COSINE_SIMILARITY);
        result.put(SimilarityMetric.ENHANCED_JACCARD_SIMILARITY, JedaiOptions.ENHANCED_JACCARD_SIMILARITY);
        result.put(SimilarityMetric.GENERALIZED_JACCARD_SIMILARITY, JedaiOptions.GENERALIZED_JACCARD_SIMILARITY);
        result.put(SimilarityMetric.GRAPH_CONTAINMENT_SIMILARITY, JedaiOptions.GRAPH_CONTAINMENT_SIMILARITY);
        result.put(SimilarityMetric.GRAPH_NORMALIZED_VALUE_SIMILARITY, JedaiOptions.GRAPH_NORMALIZED_VALUE_SIMILARITY);
        result.put(SimilarityMetric.GRAPH_VALUE_SIMILARITY, JedaiOptions.GRAPH_VALUE_SIMILARITY);
        result.put(SimilarityMetric.GRAPH_OVERALL_SIMILARITY, JedaiOptions.GRAPH_OVERALL_SIMILARITY);
        result.put(SimilarityMetric.JACCARD_SIMILARITY, JedaiOptions.JACCARD_SIMILARITY);
        result.put(SimilarityMetric.SIGMA_SIMILARITY, JedaiOptions.SIGMA_SIMILARITY);
        result.put(SimilarityMetric.WEIGHTED_JACCARD_SIMILARITY, JedaiOptions.WEIGHTED_JACCARD_SIMILARITY);

        return Collections.unmodifiableMap(result);
    }

    /**
     * Create reverse map of "similarityMetricToString"
     *
     * @return Map of Strings to their SimilarityMetric values
     */
    private static Map<String, SimilarityMetric> createStringToSimMetricMap() {
        Map<String, SimilarityMetric> result = new HashMap<>();

        for (SimilarityMetric metric : similarityMetricToString.keySet()) {
            result.put(similarityMetricToString.get(metric), metric);
        }

        return Collections.unmodifiableMap(result);
    }

    /**
     * Return map of representation models' String names to their enumeration values
     *
     * @return
     */
    private static Map<String, RepresentationModel> createRepresentationModelsMap() {
        Map<String, RepresentationModel> result = new HashMap<>();
        result.put(JedaiOptions.CHARACTER_BIGRAMS, RepresentationModel.CHARACTER_BIGRAMS);
        result.put(JedaiOptions.CHARACTER_BIGRAM_GRAPHS, RepresentationModel.CHARACTER_BIGRAM_GRAPHS);
        result.put(JedaiOptions.CHARACTER_TRIGRAMS, RepresentationModel.CHARACTER_TRIGRAMS);
        result.put(JedaiOptions.CHARACTER_TRIGRAM_GRAPHS, RepresentationModel.CHARACTER_TRIGRAM_GRAPHS);
        result.put(JedaiOptions.CHARACTER_FOURGRAMS, RepresentationModel.CHARACTER_FOURGRAMS);
        result.put(JedaiOptions.CHARACTER_FOURGRAM_GRAPHS, RepresentationModel.CHARACTER_FOURGRAM_GRAPHS);
        result.put(JedaiOptions.TOKEN_UNIGRAMS, RepresentationModel.TOKEN_UNIGRAMS);
        result.put(JedaiOptions.TOKEN_UNIGRAMS_TF_IDF, RepresentationModel.TOKEN_UNIGRAMS_TF_IDF);
        result.put(JedaiOptions.TOKEN_UNIGRAM_GRAPHS, RepresentationModel.TOKEN_UNIGRAM_GRAPHS);
        result.put(JedaiOptions.TOKEN_BIGRAMS, RepresentationModel.TOKEN_BIGRAMS);
        result.put(JedaiOptions.TOKEN_BIGRAMS_TF_IDF, RepresentationModel.TOKEN_BIGRAMS_TF_IDF);
        result.put(JedaiOptions.TOKEN_BIGRAM_GRAPHS, RepresentationModel.TOKEN_BIGRAM_GRAPHS);
        result.put(JedaiOptions.TOKEN_TRIGRAMS, RepresentationModel.TOKEN_TRIGRAMS);
        result.put(JedaiOptions.TOKEN_TRIGRAMS_TF_IDF, RepresentationModel.TOKEN_TRIGRAMS_TF_IDF);
        result.put(JedaiOptions.TOKEN_TRIGRAM_GRAPHS, RepresentationModel.TOKEN_TRIGRAM_GRAPHS);
        return Collections.unmodifiableMap(result);
    }

    /**
     * Get the compatible similarity metrics for a representation model
     *
     * @param representationModel String name of representation model
     * @return List of compatible similarity metrics (as Strings)
     */
    public static List<String> getAvailableMetricsForRepresentationModel(String representationModel) {
        RepresentationModel model = stringToRepresentationModel.get(representationModel);
        List<SimilarityMetric> availableMetrics = SimilarityMetric.getModelCompatibleSimMetrics(model);
        List<String> options = new ArrayList<>();

        for (SimilarityMetric sm : availableMetrics) {
            options.add(similarityMetricToString.get(sm));
        }

        return options;
    }

    /**
     * Get a representation model by its name
     *
     * @param modelName Name of wanted representation model
     * @return Representation Model
     */
    public static RepresentationModel getRepresentationModel(String modelName) {
        return stringToRepresentationModel.get(modelName);
    }

    /**
     * Get a similarity metric by its name
     *
     * @param metricName Name of wanted similarity metric
     * @return Similarity Metric
     */
    public static SimilarityMetric getSimilarityMetric(String metricName) {
        return stringToSimilarityMetric.get(metricName);
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

    public static IBlockProcessing getMethodByName(String method) {
        IBlockProcessing processingMethod = null;

        // Get appropriate processing method
        switch (method) {
            case JedaiOptions.BLOCK_FILTERING:
                processingMethod = new BlockFiltering();
                break;
//            case JedaiOptions.BLOCK_SCHEDULING:
//                processingMethod = new BlockScheduling();
//                break;
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
        }

        // Return the method
        return processingMethod;
    }
}

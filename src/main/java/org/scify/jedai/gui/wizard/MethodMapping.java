package org.scify.jedai.gui.wizard;

import org.scify.jedai.blockprocessing.IBlockProcessing;
import org.scify.jedai.blockprocessing.blockcleaning.BlockFiltering;
import org.scify.jedai.blockprocessing.blockcleaning.ComparisonsBasedBlockPurging;
import org.scify.jedai.blockprocessing.blockcleaning.SizeBasedBlockPurging;
import org.scify.jedai.blockprocessing.comparisoncleaning.*;
import org.scify.jedai.datareader.entityreader.*;
import org.scify.jedai.datareader.groundtruthreader.GtCSVReader;
import org.scify.jedai.datareader.groundtruthreader.GtRDFReader;
import org.scify.jedai.datareader.groundtruthreader.GtSerializationReader;
import org.scify.jedai.entityclustering.*;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.schemaclustering.AttributeNameClustering;
import org.scify.jedai.schemaclustering.AttributeValueClustering;
import org.scify.jedai.schemaclustering.HolisticAttributeClustering;
import org.scify.jedai.schemaclustering.ISchemaClustering;
import org.scify.jedai.similarityjoins.ISimilarityJoin;
import org.scify.jedai.similarityjoins.characterbased.AllPairs;
import org.scify.jedai.similarityjoins.characterbased.FastSS;
import org.scify.jedai.similarityjoins.characterbased.PassJoin;
import org.scify.jedai.similarityjoins.tokenbased.PPJoin;
import org.scify.jedai.utilities.IDocumentation;
import org.scify.jedai.utilities.enumerations.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MethodMapping {
    public static final Map<String, BlockBuildingMethod> blockBuildingMethods = createMap();
    public static final Map<String, SchemaClusteringMethod> schemaClusteringMethods = createSchemaClusteringMap();

    /**
     * Return map of block building methods' String names to their enumeration values
     *
     * @return Mapping of block building method names to enum values
     */
    private static Map<String, BlockBuildingMethod> createMap() {
        Map<String, BlockBuildingMethod> result = new HashMap<>();
        result.put(JedaiOptions.STANDARD_TOKEN_BUILDING, BlockBuildingMethod.STANDARD_BLOCKING);
        result.put(JedaiOptions.SORTED_NEIGHBORHOOD, BlockBuildingMethod.SORTED_NEIGHBORHOOD);
        result.put(JedaiOptions.SORTED_NEIGHBORHOOD_EXTENDED, BlockBuildingMethod.EXTENDED_SORTED_NEIGHBORHOOD);
        result.put(JedaiOptions.Q_GRAMS_BLOCKING, BlockBuildingMethod.Q_GRAMS_BLOCKING);
        result.put(JedaiOptions.Q_GRAMS_BLOCKING_EXTENDED, BlockBuildingMethod.EXTENDED_Q_GRAMS_BLOCKING);
        result.put(JedaiOptions.SUFFIX_ARRAYS_BLOCKING, BlockBuildingMethod.SUFFIX_ARRAYS);
        result.put(JedaiOptions.SUFFIX_ARRAYS_BLOCKING_EXTENDED, BlockBuildingMethod.EXTENDED_SUFFIX_ARRAYS);
        result.put(JedaiOptions.LSH_SUPERBIT_BLOCKING, BlockBuildingMethod.LSH_SUPERBIT_BLOCKING);
        result.put(JedaiOptions.LSH_MINHASH_BLOCKING, BlockBuildingMethod.LSH_MINHASH_BLOCKING);
        return Collections.unmodifiableMap(result);
    }

    /**
     * Return map of schema clustering methods' String names to their enumeration values
     *
     * @return Mapping of schema clustering method names to enum values
     */
    private static Map<String, SchemaClusteringMethod> createSchemaClusteringMap() {
        Map<String, SchemaClusteringMethod> result = new HashMap<>();
        result.put(JedaiOptions.ATTRIBUTE_NAME_CLUSTERING, SchemaClusteringMethod.ATTRIBUTE_NAME_CLUSTERING);
        result.put(JedaiOptions.ATTRIBUTE_VALUE_CLUSTERING, SchemaClusteringMethod.ATTRIBUTE_VALUE_CLUSTERING);
        result.put(JedaiOptions.HOLISTIC_ATTRIBUTE_CLUSTERING, SchemaClusteringMethod.HOLISTIC_ATTRIBUTE_CLUSTERING);
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

    public static ISchemaClustering getSchemaClusteringMethodByName(String methodName) {
        ISchemaClustering schemaClustering = null;

        switch (methodName) {
            case JedaiOptions.ATTRIBUTE_NAME_CLUSTERING:
                schemaClustering = new AttributeNameClustering(
                        RepresentationModel.CHARACTER_TRIGRAMS, SimilarityMetric.ENHANCED_JACCARD_SIMILARITY);
                break;
            case JedaiOptions.ATTRIBUTE_VALUE_CLUSTERING:
                schemaClustering = new AttributeValueClustering(
                        RepresentationModel.CHARACTER_TRIGRAMS, SimilarityMetric.ENHANCED_JACCARD_SIMILARITY);
                break;
            case JedaiOptions.HOLISTIC_ATTRIBUTE_CLUSTERING:
                schemaClustering = new HolisticAttributeClustering(
                        RepresentationModel.CHARACTER_TRIGRAMS, SimilarityMetric.ENHANCED_JACCARD_SIMILARITY);
                break;
        }

        return schemaClustering;
    }

    public static ISimilarityJoin getSimilarityJoinMethodByName(String methodName) {
        ISimilarityJoin similarityJoin = null;

        switch (methodName) {
            case JedaiOptions.ALL_PAIRS_CHAR_BASED:
                similarityJoin = new AllPairs(1);
                break;
            case JedaiOptions.ALL_PAIRS_TOKEN_BASED:
                similarityJoin = new org.scify.jedai.similarityjoins.tokenbased.AllPairs(1.);
                break;
            case JedaiOptions.FAST_SS:
                similarityJoin = new FastSS(1);
                break;
            case JedaiOptions.PASS_JOIN:
                similarityJoin = new PassJoin(1);
                break;
            case JedaiOptions.PP_JOIN:
                similarityJoin = new PPJoin(1.);
                break;
        }

        return similarityJoin;
    }

    /**
     * Get the IDocumentation instance for a specified Data Reader (either for Entities, or Ground Truth).
     * Useful for getting the parameters for a reader.
     *
     * @param groundTruth Set to true if you want a ground truth reader. If false, Entity Readers will be used instead
     * @param type        The type of reader
     * @return IDocumentation instance of the specified reader
     */
    public static IDocumentation getDataReader(boolean groundTruth, String type) {
        if (groundTruth) {
            // Return ground truth reader
            switch (type) {
                case JedaiOptions.SERIALIZED:
                    return new GtSerializationReader("");
                case JedaiOptions.CSV:
                    return new GtCSVReader("");
                case JedaiOptions.RDF:
                    return new GtRDFReader("");
            }
        } else {
            // Return entity reader
            switch (type) {
                case JedaiOptions.SERIALIZED:
                    return new EntitySerializationReader("");
                case JedaiOptions.CSV:
                    return new EntityCSVReader("");
                case JedaiOptions.RDF:
                    return new EntityRDFReader("");
                case JedaiOptions.DATABASE:
                    return new EntityDBReader("");
                case JedaiOptions.XML:
                    return new EntityXMLreader("");
            }
        }

        // If nothing was found, return null...
        return null;
    }

    /**
     * Create a block processing method instance to be used for running the workflow.
     *
     * @param method         Method name
     * @param isCleanCleanEr Whether we are using Clean-Clean ER or not (only matters for some methods)
     * @return
     */
    public static IBlockProcessing getMethodByName(String method, boolean isCleanCleanEr) {
        IBlockProcessing processingMethod = null;

        // Get appropriate processing method
        switch (method) {
            // Block Building methods
            case JedaiOptions.BLOCK_FILTERING:
                processingMethod = new BlockFiltering();
                break;
            case JedaiOptions.SIZE_BASED_BLOCK_PURGING:
                processingMethod = new SizeBasedBlockPurging();
                break;
            case JedaiOptions.COMPARISON_BASED_BLOCK_PURGING:
                processingMethod = new ComparisonsBasedBlockPurging(isCleanCleanEr);
                break;
            // Below: Comparison Cleaning methods
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
            case JedaiOptions.CANOPY_CLUSTERING:
                processingMethod = new CanopyClustering();
                break;
            case JedaiOptions.CANOPY_CLUSTERING_EXTENDED:
                processingMethod = new ExtendedCanopyClustering();
                break;
        }

        // Return the method
        return processingMethod;
    }
}

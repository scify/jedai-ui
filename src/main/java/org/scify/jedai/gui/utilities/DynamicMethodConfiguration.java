package org.scify.jedai.gui.utilities;

import com.google.inject.Injector;
import javafx.beans.property.ListProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.scify.jedai.blockbuilding.*;
import org.scify.jedai.blockprocessing.IBlockProcessing;
import org.scify.jedai.blockprocessing.blockcleaning.BlockFiltering;
import org.scify.jedai.blockprocessing.blockcleaning.ComparisonsBasedBlockPurging;
import org.scify.jedai.blockprocessing.blockcleaning.SizeBasedBlockPurging;
import org.scify.jedai.blockprocessing.comparisoncleaning.*;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.entityclustering.*;
import org.scify.jedai.entitymatching.GroupLinkage;
import org.scify.jedai.entitymatching.IEntityMatching;
import org.scify.jedai.entitymatching.ProfileMatcher;
import org.scify.jedai.gui.controllers.DynamicConfigurationController;
import org.scify.jedai.prioritization.*;
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

import java.util.List;

public class DynamicMethodConfiguration {
    /**
     * Display the window with the automatically generated form that allows the user to set a method's parameters.
     *
     * @param callerClass Class to get class loader from, to use for loading the window's FXML file
     * @param injector    Injector to use when loading FXML, so that the model etc. are injected automatically.
     * @param method      Method that the window should display configuration options for.
     */
    public static void displayModal(Class callerClass, Injector injector, IDocumentation method,
                                    ListProperty<MutablePair<String, Object>> paramsProperty) {
        // Load FXML and get controller
        Parent root = DialogHelper.loadFxml(callerClass, injector, "wizard-fxml/DynamicConfiguration.fxml");
        Object controller = null;
        if (root != null) {
            controller = root.getProperties().get("controller");
        }

        // Set controller options and show popup
        if (controller instanceof DynamicConfigurationController) {
            // Cast the controller instance since we know it's safe here
            DynamicConfigurationController popupController = (DynamicConfigurationController) controller;

            // Give the configuration options to the controller
            String methodName = method.getMethodName();
            JsonArray params = method.getParameterConfiguration();
            popupController.setParameters(params, paramsProperty);
            popupController.setMethodName(methodName);

            // Create the popup
            DialogHelper.showScene(root, Modality.APPLICATION_MODAL, true,
                    "JedAI - " + methodName + " Parameter Configuration");
        } else {
            // This shouldn't ever happen.
            System.err.println("Error when showing the parameter customization popup (Wrong controller instance?)");
        }
    }

    /**
     * Create a (GUI) node that displays manual configuration parameters for any method.
     *
     * @param parametersProperty Parameters of method
     * @return Node with list of parameters
     */
    public static Node newParamsNode(ListProperty<MutablePair<String, Object>> parametersProperty) {
        // Create the node to show the parameters
        ListView<MutablePair<String, Object>> paramsList = new ListView<>();
        paramsList.setMaxHeight(60);

        // Bind the ListView's items to the given parameters property
        paramsList.itemsProperty().bind(parametersProperty);

        return paramsList;
    }

    /**
     * Check if a method is configured correctly.
     *
     * @param method Method to check
     * @param params Parameters for method
     * @return True if configuration seems correct, false otherwise
     */
    public static boolean configurationOk(IDocumentation method, List<MutablePair<String, Object>> params) {
        // Get the expected parameter configuration
        JsonArray paramDetails = method.getParameterConfiguration();

        // Check that the arrays aren't null and have the same size
        if (params == null || paramDetails == null || params.size() != paramDetails.size()) {
            return false;
        }

        // Check if each parameter has a valid value
        for (int i = 0; i < paramDetails.size(); i++) {
            JsonObject paramDescription = paramDetails.get(i).getAsObject();
            MutablePair<String, Object> paramConfig = params.get(i);
//            System.out.println(paramDescription);
//            System.out.println(paramConfig + "\n");

            // Check that the parameter names are the same
            if (!paramDescription.get("name").getAsString().value().equals(paramConfig.getLeft())) {
                return false;
            }

            //todo: Check that value is of correct type
//            System.out.println("------");
        }

        return true;
    }

    /**
     * Given a Block Building method and a list of parameters, initialize and return that method with the given
     * parameters. Assumes the parameters are of the correct type (they are cast) and correct number.
     *
     * @param method     Method to initialize
     * @param parameters Parameter values
     * @return Block Building method configured with the given parameters
     */
    public static IBlockBuilding configureBlockBuildingMethod(BlockBuildingMethod method,
                                                              List<MutablePair<String, Object>> parameters) {
        switch (method) {
            case STANDARD_BLOCKING:
                return new StandardBlocking();
            case SUFFIX_ARRAYS:
                // todo: make sure these are correct
                return new SuffixArraysBlocking(
                        (int) parameters.get(0).getRight(), // Maximum Suffix Frequency
                        (int) parameters.get(1).getRight()  // Minimum Suffix Length
                );
            case Q_GRAMS_BLOCKING:
                return new QGramsBlocking(
                        (int) parameters.get(0).getRight()
                );
            case SORTED_NEIGHBORHOOD:
                return new SortedNeighborhoodBlocking(
                        (int) parameters.get(0).getRight()
                );
            case EXTENDED_SUFFIX_ARRAYS:
                // todo: make sure these are correct
                return new ExtendedSuffixArraysBlocking(
                        (int) parameters.get(0).getRight(), // Maximum Substring Frequency
                        (int) parameters.get(1).getRight()  // Minimum Substring Length
                );
            case EXTENDED_Q_GRAMS_BLOCKING:
                return new ExtendedQGramsBlocking(
                        (double) parameters.get(1).getRight(),
                        (int) parameters.get(0).getRight()
                );
            case EXTENDED_SORTED_NEIGHBORHOOD:
                return new ExtendedSortedNeighborhoodBlocking(
                        (int) parameters.get(0).getRight()
                );
            case LSH_SUPERBIT_BLOCKING:
                return new LSHSuperBitBlocking(
                        (int) parameters.get(0).getRight(),
                        (int) parameters.get(1).getRight()
                );
            case LSH_MINHASH_BLOCKING:
                return new LSHMinHashBlocking(
                        (int) parameters.get(0).getRight(),
                        (int) parameters.get(1).getRight()
                );
            default:
                return null;
        }
    }

    /**
     * Given a prioritization method name and a list of parameters, initialize and return a method instance.
     * Assumes the parameters are of correct type & number.
     *
     * @param methodName Name of similarity join method.
     * @param params     Parameters list for method.
     * @return Prioritization method instance
     */
    public static IPrioritization configurePrioritizationMethod(String methodName, List<MutablePair<String, Object>> params) {
        switch (methodName) {
            case JedaiOptions.GLOBAL_PROGRESSIVE_SORTED_NEIGHBORHOOR:
                return new GlobalProgressiveSortedNeighborhood(
                        (int) params.get(0).getRight(), // Budget
                        (ProgressiveWeightingScheme) params.get(1).getRight() // Weighting Scheme
                );
            case JedaiOptions.LOCAL_PROGRESSIVE_SORTED_NEIGHBORHOOD:
                return new LocalProgressiveSortedNeighborhood(
                        (int) params.get(0).getRight(), // Budget
                        (ProgressiveWeightingScheme) params.get(1).getRight() // Weighting Scheme
                );
            case JedaiOptions.PROGRESSIVE_BLOCK_SCHEDULING:
                return new ProgressiveBlockScheduling(
                        (int) params.get(0).getRight(), // Budget
                        (WeightingScheme) params.get(1).getRight() // Weighting Scheme
                );
            case JedaiOptions.PROGRESSIVE_ENTITY_SCHEDULING:
                return new ProgressiveEntityScheduling(
                        (int) params.get(0).getRight(), // Budget
                        (WeightingScheme) params.get(1).getRight() // Weighting Scheme
                );
            case JedaiOptions.PROGRESSIVE_GLOBAL_TOP_COMPARISONS:
                return new ProgressiveGlobalTopComparisons(
                        (int) params.get(0).getRight(), // Budget
                        (WeightingScheme) params.get(1).getRight() // Weighting Scheme
                );
            case JedaiOptions.PROGRESSIVE_LOCAL_TOP_COMPARISONS:
                return new ProgressiveLocalTopComparisons(
                        (int) params.get(0).getRight(), // Budget
                        (WeightingScheme) params.get(1).getRight() // Weighting Scheme
                );
            case JedaiOptions.PROGRESSIVE_GLOBAL_RANDOM_COMPARISONS:
                return new ProgressiveGlobalRandomComparisons(
                        (int) params.get(0).getRight() // Budget
                );
            default:
                return null;
        }
    }

    /**
     * Given a similarity join method name and a list of parameters, initialize and return a method instance.
     * Assumes the parameters are of correct type & number.
     *
     * @param methodName Name of similarity join method.
     * @param parameters Parameters list for method.
     * @return Similarity join method instance
     */
    public static ISimilarityJoin configureSimilarityJoinMethod(String methodName,
                                                                List<MutablePair<String, Object>> parameters) {
        switch (methodName) {
            case JedaiOptions.ALL_PAIRS_CHAR_BASED:
                return new AllPairs(
                        (int) parameters.get(0).getRight() // Threshold
                );
            case JedaiOptions.ALL_PAIRS_TOKEN_BASED:
                return new org.scify.jedai.similarityjoins.tokenbased.AllPairs(
                        (double) parameters.get(0).getRight() // Threshold
                );
            case JedaiOptions.FAST_SS:
                return new FastSS(
                        (int) parameters.get(0).getRight() // Threshold
                );
            case JedaiOptions.PASS_JOIN:
                return new PassJoin(
                        (int) parameters.get(0).getRight() // Threshold
                );
            case JedaiOptions.PP_JOIN:
                return new PPJoin(
                        (double) parameters.get(0).getRight() // Threshold
                );
            default:
                return null;
        }
    }

    /**
     * Given a Comparison Cleaning method name and a list of parameters, initialize and return that method with these
     * parameters. Assumes the parameters are of the correct type (they are cast) and correct number.
     *
     * @param methodName Name of comparison cleaning method
     * @param parameters Parameters for method
     * @return Configured comparison cleaning method
     */
    public static IBlockProcessing configureComparisonCleaningMethod(String methodName,
                                                                     List<MutablePair<String, Object>> parameters) {
        IBlockProcessing processingMethod = null;

        // Get appropriate processing method
        switch (methodName) {
            case JedaiOptions.COMPARISON_PROPAGATION:
                processingMethod = new ComparisonPropagation(); // Parameter-free
                break;
            case JedaiOptions.CARDINALITY_EDGE_PRUNING:
                processingMethod = new CardinalityEdgePruning(
                        (WeightingScheme) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.CARDINALITY_NODE_PRUNING:
                processingMethod = new CardinalityNodePruning(
                        (WeightingScheme) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.WEIGHED_EDGE_PRUNING:
                processingMethod = new WeightedEdgePruning(
                        (WeightingScheme) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.WEIGHED_NODE_PRUNING:
                processingMethod = new WeightedNodePruning(
                        (WeightingScheme) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.RECIPROCAL_CARDINALITY_NODE_PRUNING:
                processingMethod = new ReciprocalCardinalityNodePruning(
                        (WeightingScheme) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.RECIPROCAL_WEIGHED_NODE_PRUNING:
                processingMethod = new ReciprocalWeightedNodePruning(
                        (WeightingScheme) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.CANOPY_CLUSTERING:
                processingMethod = new CanopyClustering(
                        (double) parameters.get(0).getRight(), // Inclusive threshold
                        (double) parameters.get(1).getRight()  // Exclusive threshold
                );
                break;
            case JedaiOptions.CANOPY_CLUSTERING_EXTENDED:
                processingMethod = new ExtendedCanopyClustering(
                        (int) parameters.get(0).getRight(), // Inclusive threshold
                        (int) parameters.get(1).getRight()  // Exclusive threshold
                );
                break;
        }

        return processingMethod;
    }

    /**
     * Given a Block Cleaning method configuration object, create an instance of the method, configured with the
     * specified parameters. Assumes that configuration type is manual.
     *
     * @param methodName Name of the block cleaning method
     * @param parameters Parameters for method
     * @return Configured block cleaning method
     */
    public static IBlockProcessing configureBlockCleaningMethod(String methodName,
                                                                List<MutablePair<String, Object>> parameters) {
        IBlockProcessing processingMethod = null;

        // Get appropriate processing method
        switch (methodName) {
            case JedaiOptions.BLOCK_FILTERING:
                processingMethod = new BlockFiltering(
                        (double) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.SIZE_BASED_BLOCK_PURGING:
                processingMethod = new SizeBasedBlockPurging(
                        (double) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.COMPARISON_BASED_BLOCK_PURGING:
                processingMethod = new ComparisonsBasedBlockPurging(
                        (double) parameters.get(0).getRight()
                );
                break;
        }

        return processingMethod;
    }

    /**
     * Given a schema clustering method name and list of parameters, create an instance of the method configured with
     * the specified parameters. Used for manual configuration.
     *
     * @param methodName Name of the block cleaning method
     * @param parameters Parameters for method
     * @return Configured schema clustering method
     */
    public static ISchemaClustering configureSchemaClusteringMethod(String methodName,
                                                                    List<MutablePair<String, Object>> parameters) {
        ISchemaClustering processingMethod = null;

        // Get appropriate processing method
        switch (methodName) {
            case JedaiOptions.ATTRIBUTE_NAME_CLUSTERING:
                processingMethod = new AttributeNameClustering(
                        (RepresentationModel) parameters.get(0).getRight(),
                        (SimilarityMetric) parameters.get(1).getRight()
                );
                break;
            case JedaiOptions.ATTRIBUTE_VALUE_CLUSTERING:
                processingMethod = new AttributeValueClustering(
                        (RepresentationModel) parameters.get(0).getRight(),
                        (SimilarityMetric) parameters.get(1).getRight()
                );
                break;
            case JedaiOptions.HOLISTIC_ATTRIBUTE_CLUSTERING:
                processingMethod = new HolisticAttributeClustering(
                        (RepresentationModel) parameters.get(0).getRight(),
                        (SimilarityMetric) parameters.get(1).getRight()
                );
                break;
        }

        return processingMethod;
    }

    /**
     * Given a Entity Clustering method name and a list of parameters, initialize and return that method with these
     * parameters. Assumes the parameters are of the correct type (they are cast) and correct number.
     *
     * @param methodName Name of entity clustering method
     * @param parameters Parameters for method
     * @return Configured entity clustering method
     */
    public static IEntityClustering configureEntityClusteringMethod(String methodName,
                                                                    List<MutablePair<String, Object>> parameters) {
        IEntityClustering ecMethod = null;

        // Get appropriate processing method
        switch (methodName) {
            case JedaiOptions.CENTER_CLUSTERING:
                ecMethod = new CenterClustering(
                        (double) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.CONNECTED_COMPONENTS_CLUSTERING:
                ecMethod = new ConnectedComponentsClustering(
                        (double) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.CUT_CLUSTERING:
                ecMethod = new CutClustering(
                        (double) parameters.get(1).getRight(),  // 1st parameter of CutClustering, but 2nd in JedAI-core
                        (double) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.MARKOV_CLUSTERING:
                ecMethod = new MarkovClustering(
                        (double) parameters.get(1).getRight(),  // Cluster Threshold
                        (double) parameters.get(2).getRight(),  // Matrix Similarity Threshold
                        (int) parameters.get(3).getRight(),     // Similarity Checks Limit
                        (double) parameters.get(0).getRight()   // Similarity Threshold
                );
                break;
            case JedaiOptions.MERGE_CENTER_CLUSTERING:
                ecMethod = new MergeCenterClustering(
                        (double) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.RICOCHET_SR_CLUSTERING:
                ecMethod = new RicochetSRClustering(
                        (double) parameters.get(0).getRight()
                );
                break;
            case JedaiOptions.UNIQUE_MAPPING_CLUSTERING:
                ecMethod = new UniqueMappingClustering(
                        (double) parameters.get(0).getRight()
                );
                break;
        }

        return ecMethod;
    }

    /**
     * Create and return an Entity Matching method, with the specified parameters
     *
     * @param emMethodName Name of the Entity Matching method
     * @param parameters   Parameters for the method
     * @return Entity Matching method configured with the given parameters
     */
    public static IEntityMatching configureEntityMatchingMethod(String emMethodName, List<EntityProfile> profilesD1,
                                                                List<EntityProfile> profilesD2,
                                                                List<MutablePair<String, Object>> parameters) {
        RepresentationModel rep;
        SimilarityMetric simMetric;

        switch (emMethodName) {
            case JedaiOptions.GROUP_LINKAGE:
                // Get similarity threshold, representation model & similarity metric
                double simThr = (parameters != null) ? (double) parameters.get(2).getRight() : 0.5;
                rep = (parameters != null) ?
                        (RepresentationModel) parameters.get(0).getRight() : RepresentationModel.TOKEN_UNIGRAM_GRAPHS;
                simMetric = (parameters != null) ?
                        (SimilarityMetric) parameters.get(1).getRight() : SimilarityMetric.GRAPH_VALUE_SIMILARITY;

                // Give them to the constructor, together with the entity profiles
                if (profilesD2 == null) {
                    // Dirty ER, only one list populated
                    return new GroupLinkage(simThr, profilesD1, rep, simMetric);
                } else {
                    // Clean-Clean ER
                    return new GroupLinkage(simThr, profilesD1, profilesD2, rep, simMetric);
                }
            case JedaiOptions.PROFILE_MATCHER:
                // Get representation model & similarity metric
                rep = (parameters != null) ?
                        (RepresentationModel) parameters.get(0).getRight() : RepresentationModel.TOKEN_UNIGRAM_GRAPHS;
                simMetric = (parameters != null) ?
                        (SimilarityMetric) parameters.get(1).getRight() : SimilarityMetric.GRAPH_VALUE_SIMILARITY;

                // Give them to the constructor, together with the entity profiles
                if (profilesD2 == null) {
                    // Dirty ER, only one list populated
                    return new ProfileMatcher(profilesD1, rep, simMetric);
                } else {
                    // Clean-Clean ER
                    return new ProfileMatcher(profilesD1, profilesD2, rep, simMetric);
                }
            default:
                return null;
        }
    }
}

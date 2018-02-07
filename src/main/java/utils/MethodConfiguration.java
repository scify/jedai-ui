package utils;

import BlockBuilding.*;
import BlockProcessing.ComparisonCleaning.*;
import BlockProcessing.IBlockProcessing;
import DataReader.EntityReader.EntityCSVReader;
import DataReader.EntityReader.EntityDBReader;
import DataReader.EntityReader.EntityRDFReader;
import DataReader.EntityReader.EntitySerializationReader;
import DataReader.GroundTruthReader.GtCSVReader;
import DataReader.GroundTruthReader.GtRDFReader;
import DataReader.GroundTruthReader.GtSerializationReader;
import EntityClustering.*;
import EntityMatching.GroupLinkage;
import EntityMatching.IEntityMatching;
import EntityMatching.ProfileMatcher;
import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.Enumerations.RepresentationModel;
import Utilities.Enumerations.SimilarityMetric;
import Utilities.Enumerations.WeightingScheme;
import Utilities.IDocumentation;
import com.google.inject.Injector;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.jena.atlas.json.JsonArray;
import wizard.DynamicConfigurationController;

import java.io.IOException;
import java.util.List;

public class MethodConfiguration {
    /**
     * Display the window with the automatically generated form that allows the user to set a method's parameters.
     *
     * @param callerClass Class to get class loader from, to use for loading the window's FXML file
     * @param injector    Injector to use when loading FXML, so that the model etc. are injected automatically.
     * @param method      Method that the window should display configuration options for.
     */
    public static void displayModal(Class callerClass, Injector injector, IDocumentation method, ListProperty<JPair<String, Object>> paramsProperty) {
        Parent root;
        FXMLLoader loader = new FXMLLoader(
                callerClass.getClassLoader().getResource("wizard-fxml/DynamicConfiguration.fxml"),
                null,
                new JavaFXBuilderFactory(),
                injector::getInstance
        );

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        root.getProperties().put("controller", loader.getController());

        Object controller = loader.getController();
        if (controller instanceof DynamicConfigurationController) {
            // Cast the controller instance since we know it's safe here
            DynamicConfigurationController popupController = (DynamicConfigurationController) controller;

            // Give the configuration options to the controller
            JsonArray params = method.getParameterConfiguration();
            popupController.setParameters(params, paramsProperty);

            // Create the popup
            Stage dialog = new Stage();
            dialog.setScene(new Scene(root));
            dialog.setTitle("JedAI - Parameter Configuration");
            dialog.initModality(Modality.APPLICATION_MODAL);

//            dialog.show();
            dialog.showAndWait();
        } else {
            // This shouldn't ever happen.
            System.err.println("Error when showing the parameter customization popup (Wrong controller instance?)");
        }
    }

    /**
     * Given a Block Building method and a list of parameters, initialize and return that method with the given
     * parameters. Assumes the parameters are of the correct type (they are cast) and correct number.
     *
     * @param method     Method to initialize
     * @param parameters Parameter values
     * @return Block Building method configured with the given parameters
     */
    public static IBlockBuilding configureBlockBuildingMethod(BlockBuildingMethod method, List<JPair<String, Object>> parameters) {
        switch (method) {
            case STANDARD_BLOCKING:
                return new StandardBlocking();
            case SUFFIX_ARRAYS:
                return new SuffixArraysBlocking(
                        (int) parameters.get(0).getRight(),
                        (int) parameters.get(1).getRight()
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
                return new ExtendedSuffixArraysBlocking(
                        (int) parameters.get(0).getRight(),
                        (int) parameters.get(1).getRight()
                );
            case EXTENDED_Q_GRAMS_BLOCKING:
                return new ExtendedQGramsBlocking(
                        (double) parameters.get(0).getRight(),
                        (int) parameters.get(1).getRight()
                );
            case EXTENDED_SORTED_NEIGHBORHOOD:
                return new ExtendedSortedNeighborhoodBlocking(
                        (int) parameters.get(0).getRight()
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
    public static IBlockProcessing configureComparisonCleaningMethod(String methodName, List<JPair<String, Object>> parameters) {
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
    public static IEntityClustering configureEntityClusteringMethod(String methodName, List<JPair<String, Object>> parameters) {
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
                        (double) parameters.get(1).getRight(),  // Acap is first parameter in CutClustering, but 2nd in JedAI library
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
    public static IEntityMatching configureEntityMatchingMethod(String emMethodName, List<JPair<String, Object>> parameters) {
        RepresentationModel rep;
        SimilarityMetric simMetric;

        //todo: The default values could be taken from the JSON configurations of the methods
        switch (emMethodName) {
            case JedaiOptions.GROUP_LINKAGE:
                double simThr = (parameters != null) ? (double) parameters.get(2).getRight() : 0.5;
                rep = (parameters != null) ? (RepresentationModel) parameters.get(0).getRight() : RepresentationModel.TOKEN_UNIGRAM_GRAPHS;
                simMetric = (parameters != null) ? (SimilarityMetric) parameters.get(1).getRight() : SimilarityMetric.GRAPH_VALUE_SIMILARITY;

                return new GroupLinkage(simThr, rep, simMetric);
            case JedaiOptions.PROFILE_MATCHER:
                rep = (parameters != null) ? (RepresentationModel) parameters.get(0).getRight() : RepresentationModel.TOKEN_UNIGRAM_GRAPHS;
                simMetric = (parameters != null) ? (SimilarityMetric) parameters.get(1).getRight() : SimilarityMetric.GRAPH_VALUE_SIMILARITY;

                return new ProfileMatcher(rep, simMetric);
            default:
                return null;
        }
    }

    /**
     * Get the IDocumentation instance for a specified Data Reader (either for Entities, or Ground Truth). Useful for getting
     * the parameters for a reader.
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
            }
        }

        // If nothing was found, return null...
        return null;
    }
}

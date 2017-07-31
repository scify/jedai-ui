package utils;

import BlockBuilding.*;
import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.Enumerations.RepresentationModel;
import Utilities.Enumerations.SimilarityMetric;
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

public class CustomMethodConfiguration {
    /**
     * Display the window with the automatically generated form that allows the user to set a method's parameters.
     *
     * @param callerClass Class to get class loader from, to use for loading the window's FXML file
     * @param injector    Injector to use when loading FXML, so that the model etc. are injected automatically.
     * @param method      Method that the window should display configuration options for.
     */
    public static void displayModal(Class callerClass, Injector injector, IDocumentation method, ListProperty<Object> paramsProperty) {
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

            dialog.show();
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
     * @return
     */
    public static IBlockBuilding configureBlockBuildingMethod(BlockBuildingMethod method, List<Object> parameters) {
        switch (method) {
            case STANDARD_BLOCKING:
                return new StandardBlocking();
            case ATTRIBUTE_CLUSTERING:
                return new AttributeClusteringBlocking(
                        (RepresentationModel) parameters.get(0),
                        (SimilarityMetric) parameters.get(1)
                );
            case SUFFIX_ARRAYS:
                return new SuffixArraysBlocking(
                        (int) parameters.get(0),
                        (int) parameters.get(1)
                );
            case Q_GRAMS_BLOCKING:
                return new QGramsBlocking(
                        (int) parameters.get(0)
                );
            case SORTED_NEIGHBORHOOD:
                return new SortedNeighborhoodBlocking(
                        (int) parameters.get(0)
                );
            case EXTENDED_SUFFIX_ARRAYS:
                return new ExtendedSuffixArraysBlocking(
                        (int) parameters.get(0),
                        (int) parameters.get(1)
                );
            case EXTENDED_Q_GRAMS_BLOCKING:
                return new ExtendedQGramsBlocking(
                        (double) parameters.get(0),
                        (int) parameters.get(1)
                );
            case EXTENDED_SORTED_NEIGHBORHOOD:
                return new ExtendedSortedNeighborhoodBlocking(
                        (int) parameters.get(0)
                );
            default:
                return null;
        }
    }
}

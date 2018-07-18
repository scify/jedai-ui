package org.scify.jedai.gui.wizard.steps;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.beans.property.ListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.gui.utilities.DataReadingHelper;
import org.scify.jedai.gui.utilities.JPair;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.utilities.dynamic_configuration.MethodConfiguration;
import org.scify.jedai.gui.wizard.DatasetExplorationController;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.scify.jedai.utilities.IDocumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Step1Controller {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public ComboBox<String> entitiesD1FileTypeCombo;
    public ComboBox<String> entitiesD2FileTypeCombo;
    public ComboBox<String> groundTruthFileTypeCombo;
    public Label entityProfilesD2Label;
    public Button entitiesD1ConfigBtn;
    public Button entitiesD2ConfigBtn;
    public Button gTruthConfigBtn;
    public GridPane controlsGrid;
    public Button exploreD1Btn;
    public Button exploreD2Btn;
    public Button exploreGtBtn;
    private Logger log = LoggerFactory.getLogger(Step1Controller.class);

    @Inject
    private WizardData model;

    @Inject
    private Injector injector;

    @FXML
    public void initialize() {
        // Bind file type combobox values to the model
        entitiesD1FileTypeCombo.valueProperty().bindBidirectional(model.entityProfilesD1TypeProperty());
        entitiesD2FileTypeCombo.valueProperty().bindBidirectional(model.entityProfilesD2TypeProperty());
        groundTruthFileTypeCombo.valueProperty().bindBidirectional(model.groundTruthTypeProperty());

        // Create radio buttons
        List<String> buttons = Arrays.asList(
                JedaiOptions.DIRTY_ER,
                JedaiOptions.CLEAN_CLEAN_ER
        );

        RadioButtonHelper.createButtonGroup(radioBtnsContainer, buttons, model.erTypeProperty());

        // Add options to the 2 dataset file type comboboxes
        List<ComboBox<String>> comboboxes = Arrays.asList(entitiesD1FileTypeCombo, entitiesD2FileTypeCombo);
        List<String> fileTypeOptions = Arrays.asList(
                JedaiOptions.CSV,
                JedaiOptions.DATABASE,
                JedaiOptions.RDF,
                JedaiOptions.XML,
                JedaiOptions.SERIALIZED
        );

        for (ComboBox<String> c : comboboxes) {
            c.getItems().addAll(fileTypeOptions);
        }

        // Add options to the ground truth combobox
        groundTruthFileTypeCombo.getItems().add(JedaiOptions.CSV);
        groundTruthFileTypeCombo.getItems().add(JedaiOptions.RDF);
        groundTruthFileTypeCombo.getItems().add(JedaiOptions.SERIALIZED);

        // Disable 2nd dataset selection when Dirty ER is selected
        entitiesD2FileTypeCombo.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));
        entitiesD2ConfigBtn.disableProperty().bind(
                model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER)
                        .or(model.entityProfilesD2TypeProperty().isNull()));
        entityProfilesD2Label.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));

        // Disable configure buttons until a reader type is selected (for 2nd dataset it is done above)
        entitiesD1ConfigBtn.disableProperty().bind(model.entityProfilesD1TypeProperty().isNull());
        gTruthConfigBtn.disableProperty().bind(model.groundTruthTypeProperty().isNull());

        // Create parameters list node for 2nd dataset (which becomes disabled when Dirty ER is selected)
        Node d2ParamsList = MethodConfiguration.newParamsNode(model.entityProfilesD2ParametersProperty());
        d2ParamsList.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));

        // Add lists of parameters
        controlsGrid.add(MethodConfiguration.newParamsNode(model.entityProfilesD1ParametersProperty()), 4, 0);
        controlsGrid.add(d2ParamsList, 4, 1);
        controlsGrid.add(MethodConfiguration.newParamsNode(model.groundTruthParametersProperty()), 4, 2);

        // todo: Disable exploration buttons when there are no options selected

        // Set initial values to text fields (for testing...)
//        model.setEntityProfilesD1Type(JedaiOptions.SERIALIZED);
//        model.setGroundTruthType(JedaiOptions.SERIALIZED);
//        model.setEntityProfilesD1Parameters(FXCollections.observableArrayList(
//                new JPair<>("File Path", "C:\\Users\\leots\\Documents\\JedAIToolkit\\jedai-core\\data\\dirtyErDatasets\\restaurantProfiles")
//        ));
//        model.setGroundTruthParameters(FXCollections.observableArrayList(
//                new JPair<>("File Path", "C:\\Users\\leots\\Documents\\JedAIToolkit\\jedai-core\\data\\dirtyErDatasets\\\\restaurantIdDuplicates")
//        ));
    }

    @Validate
    public boolean validate() {
        // Create HashMap with values to check (ordered)
        Map<String, List<JPair<String, Object>>> readerParams = new LinkedHashMap<>();
        readerParams.put("entities1", model.getEntityProfilesD1Parameters());
        if (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER))
            // For Clean-Clean ER, will also check that the 2nd path has been filled
            readerParams.put("entities2", model.getEntityProfilesD2Parameters());
        readerParams.put("ground_truth", model.getGroundTruthParameters());

        // Get file types
        String entitiesD1Type = model.getEntityProfilesD1Type();
        String entitiesD2Type = model.getEntityProfilesD2Type();
        String groundTruthType = model.getGroundTruthType();

        boolean ok;

        // Check that file paths and types have been entered
        ok = readerParams.get("entities1") != null && !readerParams.get("entities1").isEmpty()
                && readerParams.get("ground_truth") != null && !readerParams.get("ground_truth").isEmpty()
                && entitiesD1Type != null && groundTruthType != null;

        if (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER)) {
            ok = ok && readerParams.get("entities2") != null
                    && !readerParams.get("entities2").isEmpty()
                    && entitiesD2Type != null;
        }

        if (!ok) {
            // Show missing field error
            showError("Missing Field", "Please configure all required inputs!");
            return false;
        }

        // Check that the files can actually be read with the appropriate readers
        try {
            String erType = model.getErType();

            // Read 1st profiles file
            List<EntityProfile> profilesD1 =
                    DataReadingHelper.getEntities(entitiesD1Type, readerParams.get("entities1"));

            // In case Clean-Clear ER is selected, also read 2nd profiles file
            List<EntityProfile> profilesD2 = null;
            if (erType.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
                profilesD2 = DataReadingHelper.getEntities(entitiesD2Type, readerParams.get("entities2"));
            }

            // Read ground truth
            DataReadingHelper
                    .getGroundTruth(groundTruthType, readerParams.get("ground_truth"), erType, profilesD1, profilesD2);
        } catch (Exception e) {
            // Show invalid input file error and stop checking other files
            showError("Invalid input files!",
                    "The input files could not be read successfully.\n\nDetails: " + e.toString() + " (" + e.getMessage() + ")");
            return false;
        }

        return true;
    }

    /**
     * Show a dataset selection error popup with customizable header & text
     *
     * @param header Header of error message
     * @param text   Text of error message
     */
    private void showError(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dataset Selection");
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

    @Submit
    public void submit() {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 1");
        }
    }

    /**
     * Show the advanced configuration window for the pressed button
     *
     * @param actionEvent Button action event
     */
    public void configBtnHandler(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            // Get button ID
            String id = ((Button) actionEvent.getSource()).getId();

            // Get the required parameters to give to configuration modal
            ListProperty<JPair<String, Object>> modelProperty = null;
            String readerType = null;
            boolean groundTruth = false;
            IDocumentation reader;

            switch (id) {
                case "entitiesD1ConfigBtn":
                    readerType = model.getEntityProfilesD1Type();
                    modelProperty = model.entityProfilesD1ParametersProperty();

                    break;
                case "entitiesD2ConfigBtn":
                    readerType = model.getEntityProfilesD2Type();
                    modelProperty = model.entityProfilesD2ParametersProperty();

                    break;
                case "gTruthConfigBtn":
                    readerType = model.getGroundTruthType();
                    modelProperty = model.groundTruthParametersProperty();
                    groundTruth = true;

                    break;
            }

            reader = MethodConfiguration.getDataReader(groundTruth, readerType);

            // Now that we have all required parameters, show the configuration window
            MethodConfiguration.displayModal(getClass(), injector, reader, modelProperty);
        }
    }

    /**
     * Explore a ground truth dataset
     *
     * @param actionEvent Button action
     */
    public void exploreGroundTruth(ActionEvent actionEvent) {
        // todo: implement
        System.out.println("Explore ground truth");
    }

    /**
     * Explore a selected dataset
     *
     * @param actionEvent Button action (used to detect which dataset to explore)
     */
    public void exploreDataset(ActionEvent actionEvent) {
        Object src = actionEvent.getSource();

        int datasetNum = -1;
        String datasetType = null;
        List<JPair<String, Object>> datasetParams = null;

        if (src instanceof Button) {
            // Get the button ID
            String btnId = ((Button) src).getId();

            // Check if it was the button for the 1st or 2nd dataset and get its type and parameters
            if (btnId.equals("exploreD1Btn")) {
                datasetType = model.getEntityProfilesD1Type();
                datasetParams = model.getEntityProfilesD1Parameters();
                datasetNum = 1;
            } else if (btnId.equals("exploreD2Btn")) {
                datasetType = model.getEntityProfilesD2Type();
                datasetParams = model.getEntityProfilesD2Parameters();
                datasetNum = 2;
            }
        } else {
            // Not a button instance? Something went wrong...
            return;
        }

        System.out.println("Dataset type:" + datasetType + "\nParams:" + datasetParams);

        // Display exploration window
        Parent root;
        FXMLLoader loader = new FXMLLoader(
                this.getClass().getClassLoader().getResource("wizard-fxml/DatasetExploration.fxml"),
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
        if (controller instanceof DatasetExplorationController) {
            // Cast the controller instance since we know it's safe here
            DatasetExplorationController popupController = (DatasetExplorationController) controller;

            // Give the configuration options to the controller
            popupController.setDatasetType(datasetType);
            popupController.setDatasetParams(datasetParams);

            // Create the popup
            Stage dialog = new Stage();
            dialog.setScene(new Scene(root));

            assert datasetParams != null;
            dialog.setTitle("JedAI - Dataset " + datasetNum + " Exploration");
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.show();
        } else {
            // This shouldn't ever happen.
            System.err.println("Error when showing the dataset exploration popup (Wrong controller instance?)");
        }
    }
}

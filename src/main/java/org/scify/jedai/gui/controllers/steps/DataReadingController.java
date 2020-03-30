package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.beans.property.ListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.jena.riot.RiotException;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datamodel.EquivalenceCluster;
import org.scify.jedai.gui.controllers.DatasetExplorationController;
import org.scify.jedai.gui.controllers.EntityClusterExplorationController;
import org.scify.jedai.gui.utilities.*;
import org.scify.jedai.gui.wizard.MethodMapping;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.scify.jedai.utilities.IDocumentation;
import org.scify.jedai.utilities.datastructures.AbstractDuplicatePropagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataReadingController {
    private final String errorTitle = "Dataset Selection";
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
    private Logger log = LoggerFactory.getLogger(DataReadingController.class);

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
        Node d2ParamsList = DynamicMethodConfiguration.newParamsNode(model.entityProfilesD2ParametersProperty());
        d2ParamsList.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));

        // Add lists of parameters
        controlsGrid.add(DynamicMethodConfiguration.newParamsNode(model.entityProfilesD1ParametersProperty()), 4, 0);
        controlsGrid.add(d2ParamsList, 4, 1);
        controlsGrid.add(DynamicMethodConfiguration.newParamsNode(model.groundTruthParametersProperty()), 4, 2);

        // Disable exploration buttons when there are no options selected
        exploreD1Btn.disableProperty().bind(model.entityProfilesD1TypeProperty().isNull());
        exploreD2Btn.disableProperty().bind(
                model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER)
                        .or(model.entityProfilesD2TypeProperty().isNull())
        );
        exploreGtBtn.disableProperty().bind(model.groundTruthTypeProperty().isNull());

        // Set initial values to text fields (for testing...)
//        model.setEntityProfilesD1Type(JedaiOptions.SERIALIZED);
//        model.setGroundTruthType(JedaiOptions.SERIALIZED);
//        model.setEntityProfilesD1Parameters(FXCollections.observableArrayList(
//                new JPair<>("File Path", "C:\\Users\\leots\\Documents\\JedAIToolkit\\jedai-core\\data\\dirtyErDatasets\\restaurantProfiles")
//        ));
//        model.setGroundTruthParameters(FXCollections.observableArrayList(
//                new JPair<>("File Path", "C:\\Users\\leots\\Documents\\JedAIToolkit\\jedai-core\\data\\dirtyErDatasets\\restaurantIdDuplicates")
//        ));
    }

    @Validate
    public boolean validate() {
        // Create HashMap with values to check (ordered)
        Map<String, List<MutablePair<String, Object>>> readerParams = new LinkedHashMap<>();
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
            DialogHelper.showError(errorTitle, "Missing Field", "Please configure all required inputs!");
            return false;
        }

        // Check that the files can actually be read with the appropriate readers
        List<EntityProfile> profilesD1;
        List<EntityProfile> profilesD2 = null;
        AbstractDuplicatePropagation groundTruth;
        try {
            String erType = model.getErType();

            // Read 1st profiles file
            profilesD1 = DataReader.getEntities(entitiesD1Type, readerParams.get("entities1"));

            // In case Clean-Clear ER is selected, also read 2nd profiles file
            if (erType.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
                profilesD2 = DataReader.getEntities(entitiesD2Type, readerParams.get("entities2"));
            }

            // Read ground truth
            groundTruth = DataReader
                    .getGroundTruth(groundTruthType, readerParams.get("ground_truth"), erType, profilesD1, profilesD2);
        } catch (Exception e) {
            // Show invalid input file error and stop checking other files
            DialogHelper.showError(errorTitle, "Invalid input files!",
                    "The input files could not be read successfully.\n\nDetails: " + e.toString() + " (" + e.getMessage() + ")");
            return false;
        }

        // Check that dataset 1 is not empty
        if (profilesD1 != null && profilesD1.isEmpty()) {
            DialogHelper.showError(errorTitle, "Dataset 1 is empty!", "The 1st dataset contains 0 entities!");
            return false;
        }

        // Check that dataset 2 is not empty
        if (profilesD2 != null && profilesD2.isEmpty()) {
            DialogHelper.showError(errorTitle, "Dataset 2 is empty!", "The 2nd dataset contains 0 entities!");
            return false;
        }

        // Check that the ground truth is not empty
        if (groundTruth != null && groundTruth.getDuplicates().isEmpty()) {
            DialogHelper.showError(errorTitle, "Ground truth is empty!", "The ground truth file contains 0 duplicates!");
            return false;
        }

        return true;
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
            ListProperty<MutablePair<String, Object>> modelProperty = null;
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

            reader = MethodMapping.getDataReader(groundTruth, readerType);

            // Now that we have all required parameters, show the configuration window
            DynamicMethodConfiguration.displayModal(getClass(), injector, reader, modelProperty);
        }
    }

    /**
     * Explore a ground truth dataset
     *
     * @param actionEvent Button action
     */
    public void exploreGroundTruth(ActionEvent actionEvent) {
        // Get ground truth reader type and parameters
        String gtType = model.getGroundTruthType();
        List<MutablePair<String, Object>> gtParams = model.getGroundTruthParameters();

        // Get ER type (to know if we need to read 2nd dataset or not)
        String erType = model.getErType();

        // Read dataset 1
        List<EntityProfile> entitiesD1 = DataReader.getEntities(
                model.getEntityProfilesD1Type(),
                model.getEntityProfilesD1Parameters()
        );

        // Read dataset 2 (if needed)
        List<EntityProfile> entitiesD2 = null;
        if (erType.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
            entitiesD2 = DataReader.getEntities(
                    model.getEntityProfilesD2Type(),
                    model.getEntityProfilesD2Parameters()
            );
        }

        // Get ground truth
        AbstractDuplicatePropagation groundTruth;
        try {
            groundTruth = DataReader.getGroundTruth(gtType, gtParams, erType, entitiesD1, entitiesD2);
        } catch (RiotException e) {
            // Catch possible exception from RDF reader
            DialogHelper.showError(errorTitle, "Ground truth could not be read!",
                    "Please check that the ground truth reader settings are correct.\n\nError details:\n"
                            + e.getMessage());
            return;
        }

        // Check that ground truth is not null
        if (groundTruth == null) {
            DialogHelper.showError(errorTitle, "Ground truth could not be read!",
                    "Please check that the ground truth reader settings are correct.");
            return;
        }

        List<EquivalenceCluster> duplicates = groundTruth.getRealEquivalenceClusters();

        // Load FXML for exploration window and get the controller
        Parent root = DialogHelper.loadFxml(this.getClass(), injector,
                "wizard-fxml/EntityClusterExploration.fxml");
        Object controller = null;
        if (root != null) {
            controller = root.getProperties().get("controller");
        }

        // Set properties of the controller & show window
        if (controller instanceof EntityClusterExplorationController) {
            // Cast the controller instance since we know it's safe here
            EntityClusterExplorationController popupController = (EntityClusterExplorationController) controller;

            // Give the configuration options to the controller
            if (model.getErType().equals(JedaiOptions.DIRTY_ER)) {
                popupController.setDuplicates(duplicates, entitiesD1);
            } else {
                popupController.setDuplicates(duplicates, entitiesD1, entitiesD2);
            }

            // Create the popup
            DialogHelper.showScene(root, Modality.WINDOW_MODAL, false,
                    "JedAI - Ground Truth Exploration");
        } else {
            // This shouldn't ever happen.
            System.err.println("Error when showing the ground truth exploration popup (Wrong controller instance?)");
        }
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
        List<MutablePair<String, Object>> datasetParams = null;

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

        // Load FXML for exploration window and get the controller
        Parent root = DialogHelper.loadFxml(this.getClass(), injector,
                "wizard-fxml/DatasetExploration.fxml");
        Object controller = null;
        if (root != null) {
            controller = root.getProperties().get("controller");
        }

        // Set properties of the controller & show window
        if (controller instanceof DatasetExplorationController) {
            // Cast the controller instance since we know it's safe here
            DatasetExplorationController popupController = (DatasetExplorationController) controller;

            // Give the configuration options to the controller
            popupController.setDatasetType(datasetType);
            popupController.setDatasetParams(datasetParams);

            // Create the popup
            DialogHelper.showScene(root, Modality.WINDOW_MODAL, false,
                    "JedAI - Dataset " + datasetNum + " Exploration");
        } else {
            // This shouldn't ever happen.
            System.err.println("Error when showing the dataset exploration popup (Wrong controller instance?)");
        }
    }
}

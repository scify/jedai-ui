package wizard.steps;

import DataModel.EntityProfile;
import Utilities.IDocumentation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.beans.property.ListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DataReadingHelper;
import utils.JPair;
import utils.JedaiOptions;
import utils.RadioButtonHelper;
import utils.dynamic_configuration.MethodConfiguration;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

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

        // Add options to the three file type comboboxes
        List<ComboBox<String>> comboboxes = Arrays.asList(entitiesD1FileTypeCombo, entitiesD2FileTypeCombo);
        List<String> fileTypeOptions = Arrays.asList(JedaiOptions.CSV, JedaiOptions.DATABASE, JedaiOptions.RDF, JedaiOptions.SERIALIZED);

        for (ComboBox<String> c : comboboxes) {
            c.getItems().addAll(fileTypeOptions);
        }

        // Ground truth does not have a Database reader
        groundTruthFileTypeCombo.getItems().add(JedaiOptions.CSV);
        groundTruthFileTypeCombo.getItems().add(JedaiOptions.RDF);
        groundTruthFileTypeCombo.getItems().add(JedaiOptions.SERIALIZED);

        // Disable 2nd dataset selection when Dirty ER is selected
        entitiesD2FileTypeCombo.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));
        entitiesD2ConfigBtn.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));
        entityProfilesD2Label.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));

        // Disable configure buttons until a reader type is selected
        entitiesD1ConfigBtn.disableProperty().bind(model.entityProfilesD1TypeProperty().isNull());
        entitiesD2ConfigBtn.disableProperty().bind(model.entityProfilesD2TypeProperty().isNull());
        gTruthConfigBtn.disableProperty().bind(model.groundTruthTypeProperty().isNull());

        // Set initial values to text fields (for testing...)
//        model.setEntityProfilesD1Type(JedaiOptions.SERIALIZED);
//        model.setGroundTruthType(JedaiOptions.SERIALIZED);
//        model.setEntityProfilesD1Parameters(FXCollections.observableArrayList(
//                new JPair<>("File Path", "C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\dirtyERfiles\\restaurantProfiles")
//        ));
//        model.setGroundTruthParameters(FXCollections.observableArrayList(
//                new JPair<>("File Path", "C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\dirtyERfiles\\restaurantIdDuplicates")
//        ));
    }

    @Validate
    public boolean validate() throws Exception {
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
            ok = ok && readerParams.get("entities2") != null && !readerParams.get("entities2").isEmpty() && entitiesD2Type != null;
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
            List<EntityProfile> profilesD1 = DataReadingHelper.getEntities(entitiesD1Type, readerParams.get("entities1"));

            // In case Clean-Clear ER is selected, also read 2nd profiles file
            List<EntityProfile> profilesD2 = null;
            if (erType.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
                profilesD2 = DataReadingHelper.getEntities(entitiesD2Type, readerParams.get("entities2"));
            }

            // Read ground truth
            DataReadingHelper.getGroundTruth(groundTruthType, readerParams.get("ground_truth"), erType, profilesD1, profilesD2);
        } catch (Exception e) {
            // Show invalid input file error and stop checking other files
            showError("Invalid input files!", "The input files could not be read successfully.\n\nDetails: " + e.toString() + " (" + e.getMessage() + ")");
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
}

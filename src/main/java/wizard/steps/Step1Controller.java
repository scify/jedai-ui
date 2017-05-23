package wizard.steps;

import DataModel.EntityProfile;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DataReadingHelper;
import utils.JedaiOptions;
import utils.RadioButtonHelper;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Step1Controller {
    public Button selectGroundTruthBtn;
    public TextField entityProfTextField;
    public TextField groundTruthTextField;
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public Button selectEntityD1Btn;
    public Button selectEntityD2Btn;
    public TextField entityProfD2TextField;
    public ComboBox<String> entitiesD1FileTypeCombo;
    public ComboBox<String> entitiesD2FileTypeCombo;
    public ComboBox<String> groundTruthFileTypeCombo;
    public Label entityProfilesD2Label;
    private Logger log = LoggerFactory.getLogger(Step1Controller.class);
    private FileChooser fileChooser;
    private File previousFolder;

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Create FileChooser instance
        fileChooser = new FileChooser();

        // Bind text field values to the model
        entityProfTextField.textProperty().bindBidirectional(model.entityProfilesPathProperty());
        entityProfD2TextField.textProperty().bindBidirectional(model.entityProfilesD2PathProperty());
        groundTruthTextField.textProperty().bindBidirectional(model.groundTruthPathProperty());

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
        entityProfD2TextField.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));
        selectEntityD2Btn.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));
        entitiesD2FileTypeCombo.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));
        entityProfilesD2Label.disableProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.DIRTY_ER));

        // Set initial values to text fields (for testing...)
//        entityProfTextField.setText("C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\dirtyErFiles\\restaurantProfiles");
//        groundTruthTextField.setText("C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\dirtyErFiles\\restaurantIdDuplicates");
    }

    @Validate
    public boolean validate() throws Exception {
        // Create HashMap with values to check (ordered)
        Map<String, String> files = new LinkedHashMap<>();
        files.put("entities1", model.getEntityProfilesPath());
        if (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER))
            // For Clean-Clean ER, will also check that the 2nd path has been filled
            files.put("entities2", model.getEntityProfilesD2Path());
        files.put("ground_truth", model.getGroundTruthPath());

        // Get file types
        String entitiesD1Type = model.getEntityProfilesD1Type();
        String entitiesD2Type = model.getEntityProfilesD2Type();
        String groundTruthType = model.getGroundTruthType();

        boolean ok;

        // Check that file paths and types have been entered
        ok = files.get("entities1") != null && !files.get("entities1").isEmpty()
                && files.get("ground_truth") != null && !files.get("ground_truth").isEmpty()
                && entitiesD1Type != null && groundTruthType != null;

        if (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER)) {
            ok = ok && files.get("entities2") != null && !files.get("entities2").isEmpty() && entitiesD2Type != null;
        }

        if (!ok) {
            // Show missing field error
            showError("Missing Field", "Please fill all the enabled fields!");
            return false;
        }

        // Check that the file paths given exist and are actually files
        for (String path : files.values()) {
            if (!new File(path).isFile()) {
                // Show error and stop validation
                showError("File does not exist!", "The file: \"" + path + "\"does not exist!");
                return false;
            }
        }

        // Check that the files can actually be read with the appropriate readers
        try {
            String erType = model.getErType();

            // Read 1st profiles file
            List<EntityProfile> profilesD1 = DataReadingHelper.getEntities(files.get("entities1"), entitiesD1Type);

            // In case Clean-Clear ER is selected, also read 2nd profiles file
            List<EntityProfile> profilesD2 = null;
            if (erType.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
                profilesD2 = DataReadingHelper.getEntities(files.get("entities2"), entitiesD2Type);
            }

            // Read ground truth
            DataReadingHelper.getGroundTruth(files.get("ground_truth"), groundTruthType, erType, profilesD1, profilesD2);
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
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 1");
        }
    }

    public void selectBtnHandler(ActionEvent actionEvent) {
        // Get ID of button that was pressed
        String btnId = ((Button) actionEvent.getTarget()).getId();

        // Open file chooser
        if (previousFolder != null) {
            fileChooser.setInitialDirectory(previousFolder);
        }
        File file = fileChooser.showOpenDialog(containerVBox.getScene().getWindow());

        if (file != null) {
            // Put the selected file's path to the corresponding text field
            switch (btnId) {
                case "selectEntityD1Btn":
                    entityProfTextField.setText(file.getAbsolutePath());
                    break;
                case "selectEntityD2Btn":
                    entityProfD2TextField.setText(file.getAbsolutePath());
                    break;
                case "selectGroundTruthBtn":
                    groundTruthTextField.setText(file.getAbsolutePath());
                    break;
            }

            // Save the file's directory to remember it if the FileChooser is opened again
            previousFolder = file.getParentFile();
        }
    }
}

package wizard.steps;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jfxtras.scene.control.ToggleGroupValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RadioButtonHelper;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Step1Controller {
    public Button selectGroundTruthBtn;
    public TextField entityProfTextField;
    public TextField groundTruthTextField;
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public Button selectEntityD1Btn;
    public Button selectEntityD2Btn;
    public TextField entityProfD2TextField;
    private Logger log = LoggerFactory.getLogger(Step1Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Bind text field values to the model
        entityProfTextField.textProperty().bindBidirectional(model.entityProfilesPathProperty());
        groundTruthTextField.textProperty().bindBidirectional(model.groundTruthPathProperty());

        // Create radio buttons
        List<String> buttons = Arrays.asList(
                "Dirty ER",
                "Clean-Clean ER"
        );

        ToggleGroupValue tgv = RadioButtonHelper.createButtonGroup(radioBtnsContainer, buttons, model.erTypeProperty());

        // Add listener to the radio buttons to enable/disable the 2nd entity file selection
        tgv.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Dirty ER")) {
                // Disable second dataset selection
                entityProfD2TextField.setDisable(true);
                selectEntityD2Btn.setDisable(true);
            } else {
                // Enable second dataset selection
                entityProfD2TextField.setDisable(false);
                selectEntityD2Btn.setDisable(false);
            }
        });

        // Make first radio button selected
        tgv.setValue(buttons.get(0));

        // Set initial values to text fields (for testing...)
//        entityProfTextField.setText("C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\restaurantProfiles");
//        groundTruthTextField.setText("C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\restaurantIdDuplicates");
    }

    @Validate
    public boolean validate() throws Exception {
        //todo: update this function to take into account new text field
        // Check that text fields have a value
        if (entityProfTextField.getText() == null || entityProfTextField.getText().isEmpty()
                || groundTruthTextField.getText() == null || groundTruthTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dataset Selection");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting an entity profiles and ground truth dataset is required.");
            alert.showAndWait();
            return false;
        }

        //todo: check that files exist because it is possible to set the paths directly from the text fields

        return true;
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
        FileChooser fileChooser = new FileChooser();
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
        }
    }
}

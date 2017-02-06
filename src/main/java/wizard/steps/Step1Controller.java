package wizard.steps;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.io.File;

public class Step1Controller {
    public Button selectEntityProfBtn;
    public Button selectGroundTruthBtn;
    public TextField entityProfTextField;
    public TextField groundTruthTextField;
    public VBox containerVBox;
    private Logger log = LoggerFactory.getLogger(Step1Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Bind text field values to the model
        entityProfTextField.textProperty().bindBidirectional(model.entityProfilesPathProperty());
        groundTruthTextField.textProperty().bindBidirectional(model.groundTruthPathProperty());

        // Set initial values to text fields (for testing...)
//        entityProfTextField.setText("C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\restaurantProfiles");
//        groundTruthTextField.setText("C:\\Users\\leots\\Documents\\JedAIToolkit\\datasets\\restaurantIdDuplicates");
    }

    @Validate
    public boolean validate() throws Exception {
        // Check that text fields have a value
        if (entityProfTextField.getText() == null || entityProfTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dataset Selection");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting an Entity Profiles dataset is required.");
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
                case "selectEntityProfBtn":
                    entityProfTextField.setText(file.getAbsolutePath());
                    break;
                case "selectGroundTruthBtn":
                    groundTruthTextField.setText(file.getAbsolutePath());
                    break;
            }
        }
    }
}

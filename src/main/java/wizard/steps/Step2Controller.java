package wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.ToggleGroupValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

public class Step2Controller {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    private ToggleGroupValue radioBtnsValue;
    private Logger log = LoggerFactory.getLogger(Step2Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Create ToggleGroup and ToggleGroupValue for the radio buttons
        ToggleGroup blockBuilding = new ToggleGroup();
        radioBtnsValue = new ToggleGroupValue<>();

        // Create List with options
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Standard/Token Blocking",
                        "Attribute Clustering",
                        "Sorted Neighborhood",
                        "Extended Sorted Neighborhood",
                        "Q-Grams Blocking",
                        "Extended Q-Grams Blocking",
                        "Suffix Arrays Blocking",
                        "Extended Suffix Arrays Blocking"
                );

        // Create a radio button for each option
        for (String s : options) {
            // Create radio button for this option
            RadioButton radioBtn = new RadioButton(s);
            radioBtn.setUserData(s);
            radioBtn.setToggleGroup(blockBuilding);

            // Add to RadioButton to the VBox and the ToggleGroupValue
            radioBtnsContainer.getChildren().add(radioBtn);
            radioBtnsValue.add(radioBtn, radioBtn.getUserData());
        }

        // Bind toggle group value to model
        model.blockBuildingProperty().bindBidirectional(radioBtnsValue.valueProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (radioBtnsValue.getValue() == null || radioBtnsValue.getValue().toString().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Block Building Selection");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Block Building Selection is required.");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 2");
        }
    }
}

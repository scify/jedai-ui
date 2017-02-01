package wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

public class Step2Controller {

    public ComboBox<String> blockBuildingCombobox;
    private Logger log = LoggerFactory.getLogger(Step2Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Standard/Token Blocking",
                        "Attribute Clustering",
                        "Sorted Neighborhood",
                        "Sorted Neighborhood (Extended)",
                        "Q-Grams Blocking",
                        "Q-Grams Blocking (Extended)",
                        "Suffix Arrays Blocking",
                        "Suffix Arrays Blocking (Extended)"
                );

        blockBuildingCombobox.setItems(comboboxOptions);

        // Bind combobox selection to model
        blockBuildingCombobox.valueProperty().bindBidirectional(model.blockBuildingProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (blockBuildingCombobox.getValue() == null || blockBuildingCombobox.getValue().isEmpty()) {
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

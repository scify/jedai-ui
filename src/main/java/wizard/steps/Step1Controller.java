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

public class Step1Controller {
    public ComboBox<String> datasetCombobox;
    private Logger log = LoggerFactory.getLogger(Step1Controller.class);

    @Inject
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Dataset 1",
                        "Dataset 2",
                        "Dataset 3"
                );
        datasetCombobox.setItems(comboboxOptions);

        // Bind combobox selection to model
        datasetCombobox.valueProperty().bindBidirectional(model.datasetProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (datasetCombobox.getValue() == null || datasetCombobox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Dataset Selection");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting a dataset is required.");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 1");
        }
    }
}

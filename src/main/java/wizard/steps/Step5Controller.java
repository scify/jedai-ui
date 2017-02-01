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

public class Step5Controller {
    public ComboBox<String> entityClusteringMethodCombobox;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Center Clustering",
                        "Connected Components Clustering",
                        "Cut Clustering",
                        "Markov Clustering",
                        "Merge-Center Clustering",
                        "Ricochet SR Clustering"
                );
        entityClusteringMethodCombobox.setItems(comboboxOptions);

        // Bind combobox selection to model
        entityClusteringMethodCombobox.valueProperty().bindBidirectional(model.entityClusteringProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (entityClusteringMethodCombobox.getValue() == null || entityClusteringMethodCombobox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entity Clustering Method");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting an Entity Clustering Method is required.");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 5");
        }
    }
}



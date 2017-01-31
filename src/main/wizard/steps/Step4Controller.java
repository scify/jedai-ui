package main.wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import main.wizard.Submit;
import main.wizard.Validate;
import main.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step4Controller {
    public ComboBox<String> entityMatchingMethodCombobox;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Group Linkage",
                        "Profile Matcher"
                );
        entityMatchingMethodCombobox.setItems(comboboxOptions);

        // Bind combobox selection to model
        entityMatchingMethodCombobox.valueProperty().bindBidirectional(model.entityMatchingProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (entityMatchingMethodCombobox.getValue() == null || entityMatchingMethodCombobox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entity Matching Method");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting an Entity Matching Method is required.");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 4");
        }
    }
}



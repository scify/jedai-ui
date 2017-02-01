package wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

public class Step4Controller {
    public ComboBox<String> entityMatchingMethodCombobox;
    public ComboBox<String> pMatcherTypeCombobox;
    public TextArea pMatcherTextArea;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to Entity Matching method selection combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Group Linkage",
                        "Profile Matcher"
                );
        entityMatchingMethodCombobox.setItems(comboboxOptions);

        // Add options to Profile Matcher parameter selection combobox
        ObservableList<String> pMatcherOptions =
                FXCollections.observableArrayList(
                        "Representation",
                        "Similarity"
                );
        pMatcherTypeCombobox.setItems(pMatcherOptions);

        // Bind comboboxes' selections to model
        entityMatchingMethodCombobox.valueProperty().bindBidirectional(model.entityMatchingProperty());
        pMatcherTypeCombobox.valueProperty().bindBidirectional(model.profileMatcherParamProperty());

        // Hide the profile matcher parameter selection combobox until needed
        pMatcherTypeCombobox.setVisible(false);
        pMatcherTextArea.setVisible(false);
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

        if (model.getEntityMatching().equals("Profile Matcher") &&
                (pMatcherTypeCombobox.getValue() == null || pMatcherTypeCombobox.getValue().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Profile Matcher Parameter");
            alert.setHeaderText("Missing Field");
            alert.setContentText("When using Profile Matcher for Entity Matching, selecting a parameter for it is required.");
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

    /**
     * Show or hide the controls for parameter selection of Profile Matcher depending on if it's selected or not
     */
    public void entityMatchingChangeHandler() {
        if (model.getEntityMatching().equals("Profile Matcher")) {
            // Show profile matcher parameter selection
            pMatcherTypeCombobox.setVisible(true);
            pMatcherTextArea.setVisible(true);
        } else {
            // Hide profile matcher parameter selection
            pMatcherTypeCombobox.setVisible(false);
            pMatcherTextArea.setVisible(false);
        }
    }
}



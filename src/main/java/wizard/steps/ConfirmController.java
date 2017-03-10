package wizard.steps;

import com.google.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JedaiOptions;
import wizard.Submit;
import wizard.WizardData;

public class ConfirmController {
    public Label profileMatcherLabel;
    public ListView<String> blockRefinementList;
    public Label erTypeLabel;
    public Label entityProfilesD1Label;
    public Label entityProfilesD2Label;
    public Label groundTruthLabel;
    public Label blockBuildingLabel;
    public Label compRefinementLabel;
    public Label entityMatchingLabel;
    public Label pMatcherParamLabel;
    public Label entityClusteringLabel;
    public Label entityProfilesD2TitleLabel;
    private Logger log = LoggerFactory.getLogger(ConfirmController.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        erTypeLabel.textProperty().bind(model.erTypeProperty());
        entityProfilesD1Label.textProperty().bind(model.entityProfilesPathProperty());
        blockBuildingLabel.textProperty().bind(model.blockBuildingProperty());
        groundTruthLabel.textProperty().bind(model.groundTruthPathProperty());
        compRefinementLabel.textProperty().bind(model.comparisonRefinementMethodProperty());
        entityMatchingLabel.textProperty().bind(model.entityMatchingProperty());
        entityClusteringLabel.textProperty().bind(model.entityClusteringProperty());

        // Create listeners for profile matcher and 2nd dataset path
        ChangeListener<String> profileMatcherListener = (observable, oldValue, newValue) -> {
            if (model.getEntityMatching().equals("Profile Matcher")) {
                // Profile Matcher selected, add profile matcher parameter
                pMatcherParamLabel.setText(model.getProfileMatcherParam());
            } else {
                // Group Linkage selected, so prof. matcher parameter is not applicable
                pMatcherParamLabel.setText("Not applicable");
            }
        };

        ChangeListener<String> datasetPathListener = (observable, oldValue, newValue) -> {
            if (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER)) {
                // Add the new value to the text field
                entityProfilesD2Label.setText(model.getEntityProfilesD2Path());
            } else {
                // Dirty ER was selected, so 2nd dataset is not applicable
                entityProfilesD2Label.setText("Not applicable");
            }
        };

        // Add listeners for profile matcher parameter
        model.entityMatchingProperty().addListener(profileMatcherListener);
        model.profileMatcherParamProperty().addListener(profileMatcherListener);

        // Add listeners for 2nd dataset path
        model.erTypeProperty().addListener(datasetPathListener);
        model.entityProfilesD2PathProperty().addListener(datasetPathListener);

        // Show block refinement methods in list
        model.blockProcessingMethodsProperty().addListener(((observable, oldValue, newValue) -> {
            blockRefinementList.setItems(model.getBlockProcessingMethods());
        }));
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] Running algorithm with specified parameters");
        }
    }
}

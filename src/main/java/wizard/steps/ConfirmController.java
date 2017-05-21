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
    public Label representationMethodLabel;
    public Label similarityMetricLabel;
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
        compRefinementLabel.textProperty().bind(model.comparisonCleaningMethodProperty());
        entityMatchingLabel.textProperty().bind(model.entityMatchingProperty());
        entityClusteringLabel.textProperty().bind(model.entityClusteringProperty());
        pMatcherParamLabel.textProperty().bind(model.profileMatcherParamProperty());
        representationMethodLabel.textProperty().bind(model.representationModelProperty());
        similarityMetricLabel.textProperty().bind(model.similarityMethodProperty());

        // Add listener to show/hide 2nd dataset path depending on selected ER type
        ChangeListener<String> datasetPathListener = (observable, oldValue, newValue) -> {
            if (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER)) {
                // Add the new value to the text field
                entityProfilesD2Label.setText(model.getEntityProfilesD2Path());
            } else {
                // Dirty ER was selected, so 2nd dataset is not applicable
                entityProfilesD2Label.setText("Not applicable");
            }
        };

        // Add listeners for 2nd dataset path
        model.erTypeProperty().addListener(datasetPathListener);
        model.entityProfilesD2PathProperty().addListener(datasetPathListener);

        // Show block refinement methods in list
        model.blockCleaningMethodsProperty().addListener(((observable, oldValue, newValue) -> {
            blockRefinementList.setItems(model.getBlockCleaningMethods());
        }));
    }

    /**
     * Set a new model, and rerun the initialization. Useful when showing a detailed configuration in a popup.
     *
     * @param newModel
     */
    public void setModel(WizardData newModel) {
        this.model = newModel;

        // Rerun the initialization to bind the labels etc. to the new model
        initialize();
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] Confirmation step completed");
        }
    }
}

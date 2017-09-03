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
    public Label entityClusteringLabel;
    public Label entityProfilesD2TitleLabel;
    public Label representationMethodLabel;
    public Label similarityMetricLabel;
    private Logger log = LoggerFactory.getLogger(ConfirmController.class);

    @Inject
    private
    WizardData model;

    // Declare the change listener for the 2nd dataset path
    private final ChangeListener<String> datasetPathListener = (observable, oldValue, newValue) -> {
        if (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER)) {
            // Add the new value to the text field
//            entityProfilesD2Label.setText(model.getEntityProfilesD2Path());
        } else {
            // Dirty ER was selected, so 2nd dataset is not applicable
            entityProfilesD2Label.setText("Not applicable");
        }
    };

    @FXML
    public void initialize() {
        erTypeLabel.textProperty().bind(model.erTypeProperty());
//        entityProfilesD1Label.textProperty().bind(model.entityProfilesD1PathProperty());
        blockBuildingLabel.textProperty().bind(model.blockBuildingProperty());
        groundTruthLabel.textProperty().bind(model.groundTruthPathProperty());
        compRefinementLabel.textProperty().bind(model.comparisonCleaningMethodProperty());
        entityMatchingLabel.textProperty().bind(model.entityMatchingProperty());
        entityClusteringLabel.textProperty().bind(model.entityClusteringProperty());
        representationMethodLabel.textProperty().bind(model.representationModelProperty());
        similarityMetricLabel.textProperty().bind(model.similarityMethodProperty());

        // Add listeners for 2nd dataset path
        model.erTypeProperty().addListener(datasetPathListener);
        //todo: needs update
//        model.entityProfilesD2PathProperty().addListener(datasetPathListener);

        // Show block refinement methods in list
        blockRefinementList.itemsProperty().bind(model.blockCleaningMethodsProperty());
    }

    /**
     * Set a new model, and rerun the initialization. Useful when showing a detailed configuration in a popup.
     *
     * @param newModel New model to show
     */
    public void setModel(WizardData newModel) {
        // Unbind UI items from the previous model
        erTypeLabel.textProperty().unbind();
        entityProfilesD1Label.textProperty().unbind();
        blockBuildingLabel.textProperty().unbind();
        groundTruthLabel.textProperty().unbind();
        compRefinementLabel.textProperty().unbind();
        entityMatchingLabel.textProperty().unbind();
        entityClusteringLabel.textProperty().unbind();
        representationMethodLabel.textProperty().unbind();
        similarityMetricLabel.textProperty().unbind();
        blockRefinementList.itemsProperty().unbind();
        model.erTypeProperty().removeListener(datasetPathListener);
//        model.entityProfilesD2PathProperty().removeListener(datasetPathListener);
        //todo: needs update

        // Set the new model
        this.model = newModel;

        // Rerun the initialization to bind UI items to the new model
        initialize();
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] Confirmation step completed");
        }
    }
}

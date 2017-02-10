package wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.WizardData;

public class ConfirmController {
    public TextField pMatcherParamField;
    public TextField groundTruthTextField;
    private Logger log = LoggerFactory.getLogger(ConfirmController.class);

    @FXML
    TextField entityProfilesTextField, blockBuildingTextField, compRefinementTextField, entityMatchingTextField, entityClusteringTextField;

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        entityProfilesTextField.textProperty().bind(model.entityProfilesPathProperty());
        blockBuildingTextField.textProperty().bind(model.blockBuildingProperty());
        groundTruthTextField.textProperty().bind(model.groundTruthPathProperty());
        compRefinementTextField.textProperty().bind(model.comparisonRefinementMethodProperty());
        entityMatchingTextField.textProperty().bind(model.entityMatchingProperty());
        entityClusteringTextField.textProperty().bind(model.entityClusteringProperty());
        pMatcherParamField.textProperty().bind(model.profileMatcherParamProperty());
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] Running algorithm with specified parameters");
        }
    }
}

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
    private Logger log = LoggerFactory.getLogger(ConfirmController.class);

    @FXML
    TextField tfField1, tfField2, tfField3, tfField4, tfField5;

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        tfField1.textProperty().bind(model.entityProfilesPathProperty());
        tfField2.textProperty().bind(model.blockBuildingProperty());
        tfField3.textProperty().bind(model.blockProcessingTypeProperty());
        tfField4.textProperty().bind(model.entityMatchingProperty());
        tfField5.textProperty().bind(model.entityClusteringProperty());
        pMatcherParamField.textProperty().bind(model.profileMatcherParamProperty());
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] Running algorithm with specified parameters");
        }
    }
}

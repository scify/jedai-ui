package wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RadioButtonHelper;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class Step6Controller {
    public VBox radioBtnsContainer;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        List<String> options = Arrays.asList(
                "Center Clustering",
                "Connected Components Clustering",
                "Cut Clustering",
                "Markov Clustering",
                "Merge-Center Clustering",
                "Ricochet SR Clustering"
        );

        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.entityClusteringProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (model.getEntityClustering() == null || model.getEntityClustering().isEmpty()) {
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
            log.debug("[SUBMIT] the user has completed step 6");
        }
    }
}



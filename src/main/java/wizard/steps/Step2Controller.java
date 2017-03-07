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

public class Step2Controller {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    private Logger log = LoggerFactory.getLogger(Step2Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Create List with options
        List<String> options = Arrays.asList(
                "Standard/Token Blocking",
                "Attribute Clustering",
                "Sorted Neighborhood",
                "Extended Sorted Neighborhood",
                "Q-Grams Blocking",
                "Extended Q-Grams Blocking",
                "Suffix Arrays Blocking",
                "Extended Suffix Arrays Blocking"
        );

        // Create radio buttons using helper method
        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.blockBuildingProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        String value = model.blockBuildingProperty().getValue();

        if (value == null || value.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Block Building Selection");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Block Building Selection is required.");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 2");
        }
    }
}

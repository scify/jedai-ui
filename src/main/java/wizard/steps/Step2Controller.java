package wizard.steps;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JedaiOptions;
import utils.RadioButtonHelper;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Step2Controller {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public Button paramsBtn;
    private Logger log = LoggerFactory.getLogger(Step2Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Create List with options
        List<String> options = Arrays.asList(
                JedaiOptions.STANDARD_TOKEN_BUILDING,
                JedaiOptions.ATTRIBUTE_CLUSTERING,
                JedaiOptions.SORTED_NEIGHBORHOOD,
                JedaiOptions.SORTED_NEIGHBORHOOD_EXTENDED,
                JedaiOptions.Q_GRAMS_BLOCKING,
                JedaiOptions.Q_GRAMS_BLOCKING_EXTENDED,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING_EXTENDED
        );

        // Create radio buttons using helper method
        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.blockBuildingProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (model.getBlockBuilding() == null || model.getBlockBuilding().isEmpty()) {
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

    /**
     * Display a window for configuration of the selected method's parameters.
     *
     * @param actionEvent Button event
     * @throws IOException In case FXML file is not found
     */
    public void customizeParameters(ActionEvent actionEvent) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("wizard-fxml/DynamicConfiguration.fxml")
        );

        root = loader.load();
        root.getProperties().put("controller", loader.getController());

        Stage dialog = new Stage();
        dialog.setScene(new Scene(root));
        dialog.setTitle("JedAI - Parameter Configuration");
        dialog.initModality(Modality.APPLICATION_MODAL);

        dialog.show();
    }
}

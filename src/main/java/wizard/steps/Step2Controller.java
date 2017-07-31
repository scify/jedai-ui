package wizard.steps;

import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.IDocumentation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CustomMethodConfiguration;
import utils.JedaiOptions;
import utils.RadioButtonHelper;
import wizard.MethodMapping;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class Step2Controller {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public Button paramsBtn;
    private Logger log = LoggerFactory.getLogger(Step2Controller.class);

    @Inject
    private WizardData model;

    @Inject
    private Injector injector;

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
     */
    public void customizeParameters(ActionEvent actionEvent) {
        // Get method instance in order for the modal to get its configuration
        String methodName = model.getBlockBuilding();
        IDocumentation method = BlockBuildingMethod.getDefaultConfiguration(
                MethodMapping.blockBuildingMethods.get(methodName)
        );

        // Display the configuration modal
        CustomMethodConfiguration.displayModal(getClass(), injector, method);
    }
}

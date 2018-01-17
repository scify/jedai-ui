package wizard.steps;

import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.IDocumentation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JedaiOptions;
import utils.MethodConfiguration;
import utils.RadioButtonHelper;
import wizard.*;

import java.util.Arrays;
import java.util.List;

public class Step2Controller {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public Button paramsBtn;
    public VBox confTypeContainer;
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
                JedaiOptions.SORTED_NEIGHBORHOOD,
                JedaiOptions.SORTED_NEIGHBORHOOD_EXTENDED,
                JedaiOptions.Q_GRAMS_BLOCKING,
                JedaiOptions.Q_GRAMS_BLOCKING_EXTENDED,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING_EXTENDED
        );

        // Create radio buttons using helper method
        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.blockBuildingProperty());

        // Add default/automatic/manual configuration buttons
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.blockBuildingConfigTypeProperty())
        );
    }

    @Validate
    public boolean validate() throws Exception {
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
        MethodConfiguration.displayModal(getClass(), injector, method, model.blockBuildingParametersProperty());
    }
}

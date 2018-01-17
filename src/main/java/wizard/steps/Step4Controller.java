package wizard.steps;

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

public class Step4Controller {
    public VBox radioBtnsContainer;
    public Button advancedConfigBtn;
    public VBox confTypeContainer;
    private Logger log = LoggerFactory.getLogger(Step4Controller.class);

    @Inject
    private WizardData model;

    @Inject
    private Injector injector;

    @FXML
    public void initialize() {
        List<String> options = Arrays.asList(
                JedaiOptions.NO_CLEANING,
                JedaiOptions.COMPARISON_PROPAGATION,
                JedaiOptions.CARDINALITY_EDGE_PRUNING,
                JedaiOptions.CARDINALITY_NODE_PRUNING,
                JedaiOptions.WEIGHED_EDGE_PRUNING,
                JedaiOptions.WEIGHED_NODE_PRUNING,
                JedaiOptions.RECIPROCAL_CARDINALITY_NODE_PRUNING,
                JedaiOptions.RECIPROCAL_WEIGHED_NODE_PRUNING
        );

        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.comparisonCleaningProperty());

        // Disable the advanced configuration button if "No cleaning" method is selected
        advancedConfigBtn.disableProperty().bind(model.comparisonCleaningProperty().isEqualTo(JedaiOptions.NO_CLEANING));

        // Add configuration type selection control
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.comparisonCleaningConfigTypeProperty())
        );
    }

    @Validate
    public boolean validate() throws Exception {
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 4");
        }
    }

    /**
     * Get the selected method and show a modal with configuration for it
     *
     * @param actionEvent Button event
     */
    public void advancedConfigBtnHandler(ActionEvent actionEvent) {
        // Get the selected method
        IDocumentation method = MethodMapping.getMethodByName(model.getComparisonCleaning());

        // Display the configuration window
        MethodConfiguration.displayModal(getClass(), injector, method, model.comparisonCleaningParametersProperty());
    }
}

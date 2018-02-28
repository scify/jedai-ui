package org.scify.jedai.gui.wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.utilities.dynamic_configuration.ConfigurationTypeSelector;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class Step4Controller {
    public VBox radioBtnsContainer;
    public VBox confTypeContainer;
    private Logger log = LoggerFactory.getLogger(Step4Controller.class);

    @Inject
    private WizardData model;

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
}

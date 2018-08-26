package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.nodes.dynamic_configuration.ConfigurationTypeSelector;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SchemaClusteringController {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public VBox confTypeContainer;
    private Logger log = LoggerFactory.getLogger(SchemaClusteringController.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create List with options
        List<String> options = Arrays.asList(
                JedaiOptions.NO_SCHEMA_CLUSTERING,
                JedaiOptions.ATTRIBUTE_NAME_CLUSTERING,
                JedaiOptions.ATTRIBUTE_VALUE_CLUSTERING,
                JedaiOptions.HOLISTIC_ATTRIBUTE_CLUSTERING
        );

        // Create radio buttons using helper method
        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.schemaClusteringProperty());

        // Add default/automatic/manual configuration buttons
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.schemaClusteringConfigTypeProperty())
        );
    }

    @Validate
    public boolean validate() {
        return true;
    }

    @Submit
    public void submit() {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed schema clustering");
        }
    }
}

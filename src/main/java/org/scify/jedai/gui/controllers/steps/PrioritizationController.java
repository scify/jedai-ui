package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.nodes.dynamic_configuration.ConfigurationTypeSelector;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class PrioritizationController {
    public VBox radioBtnsContainer;
    public VBox confTypeContainer;
    public VBox containerVBox;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create List with options
        List<String> options = Arrays.asList(
                JedaiOptions.GLOBAL_PROGRESSIVE_SORTED_NEIGHBORHOOR,
                JedaiOptions.LOCAL_PROGRESSIVE_SORTED_NEIGHBORHOOD,
                JedaiOptions.PROGRESSIVE_BLOCK_SCHEDULING,
                JedaiOptions.PROGRESSIVE_ENTITY_SCHEDULING,
                JedaiOptions.PROGRESSIVE_GLOBAL_TOP_COMPARISONS,
                JedaiOptions.PROGRESSIVE_LOCAL_TOP_COMPARISONS
        );

        // Create radio buttons using helper method
        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.prioritizationProperty());

        // Add default/automatic/manual configuration buttons
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.prioritizationConfigTypeProperty())
        );
    }
}

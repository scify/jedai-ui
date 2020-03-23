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
    public VBox confTypeContainer;
    public VBox containerVBox;
    public VBox noBlBuMethodsContainer;
    public VBox blBuMethodsContainer;

    // Lists of methods depending on whether there is at least one block building method selected or not
    private List<String> noBlBuOptions;
    private List<String> blBuOptions;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create list with options for when no block building methods are selected
        noBlBuOptions = Arrays.asList(
                JedaiOptions.GLOBAL_PROGRESSIVE_SORTED_NEIGHBORHOOR,
                JedaiOptions.LOCAL_PROGRESSIVE_SORTED_NEIGHBORHOOD
        );

        // Methods for when at least one block building method is selected
        blBuOptions = Arrays.asList(
                JedaiOptions.PROGRESSIVE_BLOCK_SCHEDULING,
                JedaiOptions.PROGRESSIVE_ENTITY_SCHEDULING,
                JedaiOptions.PROGRESSIVE_GLOBAL_TOP_COMPARISONS,
                JedaiOptions.PROGRESSIVE_LOCAL_TOP_COMPARISONS
        );

        // Create radio buttons using helper method
        RadioButtonHelper.createButtonGroup(noBlBuMethodsContainer, noBlBuOptions, model.prioritizationProperty());
        RadioButtonHelper.createButtonGroup(blBuMethodsContainer, blBuOptions, model.prioritizationProperty());
        // todo: add listener to disable each button group depending on enabled BlBu methods

        // Add default/automatic/manual configuration buttons
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.prioritizationConfigTypeProperty())
        );
    }
}

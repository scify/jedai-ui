package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.ToggleGroupValue;
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

    private ToggleGroupValue<String> noBlBuValue;
    private ToggleGroupValue<String> blBuValue;

    private List<String> noBlBuOptions;
    private List<String> blBuOptions;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create list with options for when no block building methods are selected
        // Lists of methods depending on whether there is at least one block building method selected or not
        noBlBuOptions = Arrays.asList(
                JedaiOptions.GLOBAL_PROGRESSIVE_SORTED_NEIGHBORHOOR,
                JedaiOptions.LOCAL_PROGRESSIVE_SORTED_NEIGHBORHOOD
        );

        // Methods for when at least one block building method is selected
        blBuOptions = Arrays.asList(
                JedaiOptions.PROGRESSIVE_BLOCK_SCHEDULING,
                JedaiOptions.PROGRESSIVE_ENTITY_SCHEDULING,
                JedaiOptions.PROGRESSIVE_GLOBAL_TOP_COMPARISONS,
                JedaiOptions.PROGRESSIVE_LOCAL_TOP_COMPARISONS,
                JedaiOptions.PROGRESSIVE_GLOBAL_RANDOM_COMPARISONS
        );

        // Create radio buttons using helper method
        noBlBuValue = RadioButtonHelper
                .createButtonGroup(noBlBuMethodsContainer, noBlBuOptions, model.prioritizationProperty());
        blBuValue = RadioButtonHelper
                .createButtonGroup(blBuMethodsContainer, blBuOptions, model.prioritizationProperty());

        // Add listener for when the number of enabled block building methods changes
        model.enabledBlockBuildingMethodsProperty().addListener(
                (observable, oldValue, newValue) -> {
                    int oldVal = oldValue.intValue();
                    int newVal = newValue.intValue();

                    // Check if the # of methods was 0 and one was selected or if all were deselected
                    if ((oldVal == 0 && newVal != 0) || (oldVal != 0 && newVal == 0)) {
                        // Need to update enabled radio buttons
                        updateEnabledRadioButtons(newValue.intValue());
                    }
                }
        );

        // Add default/automatic/manual configuration buttons
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.prioritizationConfigTypeProperty())
        );

        // Enable/disable the appropriate radio buttons initially
        updateEnabledRadioButtons(model.getEnabledBlockBuildingMethods());
    }

    /**
     * Update which radio buttons are enabled based on the number of enabled block building methods.
     *
     * @param enabledMethods Number of currently enabled block building methods
     */
    private void updateEnabledRadioButtons(int enabledMethods) {
        boolean hasBlBu = (enabledMethods > 0);

        // Enable/disable the appropriate radio buttons
        noBlBuMethodsContainer.getChildren().forEach(node -> node.setDisable(hasBlBu));
        blBuMethodsContainer.getChildren().forEach(node -> node.setDisable(!hasBlBu));

        // Bind the model properties
        model.prioritizationProperty().unbindBidirectional(hasBlBu ? noBlBuValue.valueProperty() : blBuValue.valueProperty());
        model.prioritizationProperty().bindBidirectional(hasBlBu ? blBuValue.valueProperty() : noBlBuValue.valueProperty());

        // Select first option
        model.setPrioritization(hasBlBu ? blBuOptions.get(0) : noBlBuOptions.get(0));
    }
}

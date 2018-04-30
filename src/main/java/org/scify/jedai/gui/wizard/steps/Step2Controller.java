package org.scify.jedai.gui.wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.utilities.dynamic_configuration.ConfigurationTypeSelector;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Step2Controller {
    public VBox containerVBox;
    public VBox radioBtnsContainer;
    public VBox confTypeContainer;
    private Logger log = LoggerFactory.getLogger(Step2Controller.class);

    @Inject
    private WizardData model;

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
    public boolean validate() {
        return true;
    }

    @Submit
    public void submit() {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 2");
        }
    }
}

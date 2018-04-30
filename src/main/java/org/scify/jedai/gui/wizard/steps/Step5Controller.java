package org.scify.jedai.gui.wizard.steps;

import com.google.inject.Inject;
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

public class Step5Controller {
    public VBox matchingMethodContainer;
    public VBox confTypeContainer;
    private Logger log = LoggerFactory.getLogger(Step5Controller.class);

    @Inject
    private WizardData model;

    public void initialize() {
        // Create options list for entity matching method
        List<String> entityMatchingOptions = Arrays.asList(
                JedaiOptions.GROUP_LINKAGE,
                JedaiOptions.PROFILE_MATCHER
        );

        // Create the radio buttons
        RadioButtonHelper.createButtonGroup(matchingMethodContainer, entityMatchingOptions, model.entityMatchingProperty());

        // Add configuration type selection control
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.entityMatchingConfigTypeProperty())
        );
    }

    @Validate
    public boolean validate() {
        return true;
    }

    @Submit
    public void submit() {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 5");
        }
    }
}

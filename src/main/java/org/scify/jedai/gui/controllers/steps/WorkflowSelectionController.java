package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class WorkflowSelectionController {
    public VBox workflowSelectorContainer;
    private Logger log = LoggerFactory.getLogger(WorkflowSelectionController.class);

    @Inject
    WizardData model;

    public void initialize() {
        // Create options list for entity matching method
        List<String> workflowOptions = Arrays.asList(
                JedaiOptions.WORKFLOW_BLOCKING_BASED,
                JedaiOptions.WORKFLOW_JOIN_BASED,
                JedaiOptions.WORKFLOW_PROGRESSIVE
        );

        // Create the radio buttons
        RadioButtonHelper.createButtonGroup(workflowSelectorContainer, workflowOptions, model.workflowProperty());
    }

    @Validate
    public boolean validate() {
        return true;
    }

    @Submit
    public void submit() {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 0");
        }
    }
}

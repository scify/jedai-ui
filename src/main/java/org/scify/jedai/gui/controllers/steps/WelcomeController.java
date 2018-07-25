package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WelcomeController {
    private Logger log = LoggerFactory.getLogger(WelcomeController.class);

    @Inject
    WizardData model;

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

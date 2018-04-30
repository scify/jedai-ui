package org.scify.jedai.gui.wizard.steps;

import com.google.inject.Inject;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step0Controller {
    private Logger log = LoggerFactory.getLogger(Step0Controller.class);

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

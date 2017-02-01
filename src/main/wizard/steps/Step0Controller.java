package main.wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import main.wizard.Submit;
import main.wizard.Validate;
import main.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step0Controller {

    private Logger log = LoggerFactory.getLogger(Step0Controller.class);

    @Inject
    WizardData model;

    @FXML
    public void initialize() {
    }

    @Validate
    public boolean validate() throws Exception {
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 0");
        }
    }
}

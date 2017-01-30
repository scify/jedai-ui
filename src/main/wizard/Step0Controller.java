package main.wizard;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step0Controller {

    private Logger log = LoggerFactory.getLogger(Step1Controller.class);

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

        if( log.isDebugEnabled() ) {
            log.debug("[SUBMIT] the user has completed step 0");
        }
    }
}

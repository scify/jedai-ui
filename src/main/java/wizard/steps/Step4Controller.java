package wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

public class Step4Controller {
    public VBox containerVBox;
    private Logger log = LoggerFactory.getLogger(Step4Controller.class);

    @Inject
    private
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
            log.debug("[SUBMIT] the user has completed step 4");
        }
    }
}
